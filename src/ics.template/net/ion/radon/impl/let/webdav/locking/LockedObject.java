package net.ion.radon.impl.let.webdav.locking;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.UUID;

import net.ion.radon.core.let.InnerRequest;

/**
 * a helper class for ResourceLocks, represents the Locks
 * 
 * @author re
 * 
 */
public class LockedObject {

	enum Type {
		READ, WRITE
	}
	
	private LockManager lockManager;

	private String path;

	private String id;

	/**
	 * Describing the depth of a locked collection. If the locked resource is not a collection, depth is 0 / doesn't matter.
	 */
	private int lockDepth;

	/**
	 * Describing the timeout of a locked object (ms)
	 */
	private long expiresAt;

	/**
	 * owner of the lock. shared locks can have multiple owners. is null if no owner is present
	 */
	// protected String[] _owner = null;
	private Set<String> thisOwner = new LinkedHashSet<String>();

	/**
	 * children of that lock
	 */
	//private LockedObject[] thisChildren = new LockedObject[0];
	private Set<LockedObject> thisChildren = new HashSet<LockedObject>();

	private LockedObject thisParent = null;

	/**
	 * weather the lock is exclusive or not. if owner=null the exclusive value doesn't matter
	 */
	private boolean thisEexclusive = false;

	/**
	 * weather the lock is a write or read lock
	 */
	private Type _type = Type.READ;

	public LockedObject(LockManager resLocks, String path) {
		this(resLocks, path, Type.WRITE) ;
	}


	public LockedObject(LockManager resLocks, String path, Type type) {
		this.path = path;
		id = UUID.randomUUID().toString();
		lockManager = resLocks;
		this._type = type;
		
		lockManager.addLock(path, type, this) ;
	}

	/**
	 * adds a new owner to a lock
	 * 
	 * @param owner
	 *            string that represents the owner
	 * @return true if the owner was added, false otherwise
	 */
	public boolean addLockedObjectOwner(String owner) {
		return thisOwner.add(owner) ;
	}

	/**
	 * tries to remove the owner from the lock
	 * 
	 * @param newOwner
	 *            string that represents the owner
	 */
	public void removeLockedObjectOwner(String newOwner) {
		thisOwner.remove(newOwner) ;
	}

	/**
	 * adds a new child lock to this lock
	 * 
	 * @param newChild
	 *            new child
	 */
	public void addChild(LockedObject newChild) {
		thisChildren.add(newChild);
	}
	
	public void removeLockedObject(LockedObject lo) {
		LockedObject root =  lockManager.getLockedObject(lo.getType()) ; 
		
		if (this != root && !this.getPath().equals("/")) {
			lo.thisParent.getChildren().remove(lo);
			
			/*int size = lo.thisParent.getChildren().length;
			for (int i = 0; i < size; i++) {
				if (lo.thisParent.thisChildren[i].equals(this)) {
					LockedObject[] newChildren = new LockedObject[size - 1];
					for (int i2 = 0; i2 < (size - 1); i2++) {
						if (i2 < i) {
							newChildren[i2] = lo.thisParent.thisChildren[i2];
						} else {
							newChildren[i2] = lo.thisParent.thisChildren[i2 + 1];
						}
					}
					if (newChildren.length != 0) {
						lo.thisParent.thisChildren = newChildren;
					} else {
						lo.thisParent.thisChildren = null;
					}
					break;
				}
			}*/
			
			lockManager.removeLocks(this);
		}
	}
	

	/**
	 * checks if a lock of the given exclusivity can be placed, only considering children up to "depth"
	 * 
	 * @param exclusive
	 *            wheather the new lock should be exclusive
	 * @param depth
	 *            the depth to which should be checked
	 * @return true if the lock can be placed
	 */
	public boolean checkLocks(boolean exclusive, int depth) {
		if (checkParents(exclusive) && checkChildren(exclusive, depth)) {
			return true;
		}
		return false;
	}

	/**
	 * helper of checkLocks(). looks if the parents are locked
	 * 
	 * @param exclusive
	 *            wheather the new lock should be exclusive
	 * @return true if no locks at the parent path are forbidding a new lock
	 */
	private boolean checkParents(boolean exclusive) {
		if (path.equals("/") || path.equals("afield://")) {
			return true;
		} else {
			if (thisOwner.size() == 0) {
				// no owner, checking parents
				return thisParent != null && thisParent.checkParents(exclusive);
			} else {
				// there already is a owner
				return !(thisEexclusive || exclusive) && thisParent.checkParents(exclusive);
			}
		}
	}

	/**
	 * helper of checkLocks(). looks if the children are locked
	 * 
	 * @param exclusive
	 *            wheather the new lock should be exclusive
	 * @return true if no locks at the children paths are forbidding a new lock
	 * @param depth
	 *            depth
	 */
	private boolean checkChildren(boolean exclusive, int depth) {
		if (thisChildren == null) {
			// a file
			return thisOwner.size() == 0|| !(thisEexclusive || exclusive);
		} else {
			// a folder

			if (thisOwner.size() == 0) {
				// no owner, checking children

				if (depth != 0) {
					boolean canLock = true;
					for(LockedObject child : thisChildren){
						if(!child.checkChildren(exclusive, depth-1)){
							canLock = false;
						}
					}
					return canLock;
				} else {
					// depth == 0 -> we don't care for children
					return true;
				}
			} else {
				// there already is a owner
				return !(thisEexclusive || exclusive);
			}
		}

	}

	/**
	 * Sets a new timeout for the LockedObject
	 * 
	 * @param timeout
	 */
	public void refreshTimeout(int timeout) {
		expiresAt = System.currentTimeMillis() + (timeout * 1000);
	}

	/**
	 * Gets the timeout for the LockedObject
	 * 
	 * @return timeout
	 */
	public long getTimeoutMillis() {
		return (expiresAt - System.currentTimeMillis());
	}

	/**
	 * Return true if the lock has expired.
	 * 
	 * @return true if timeout has passed
	 */
	public boolean hasExpired() {
		if (expiresAt != 0) {
			return (System.currentTimeMillis() > expiresAt);
		} else {
			return true;
		}
	}

	/**
	 * Gets the LockID (locktoken) for the LockedObject
	 * 
	 * @return locktoken
	 */
	public String getID() {
		return id;
	}

	/**
	 * Gets the owners for the LockedObject
	 * 
	 * @return owners
	 */
	public String[] getOwner() {
		return thisOwner.toArray(new String[0]);
	}

	/**
	 * Gets the path for the LockedObject
	 * 
	 * @return path
	 */
	public String getPath() {
		return path;
	}

	/**
	 * Sets the exclusivity for the LockedObject
	 * 
	 * @param exclusive
	 */
	public void setExclusive(boolean exclusive) {
		thisEexclusive = exclusive;
	}

	/**
	 * Gets the exclusivity for the LockedObject
	 * 
	 * @return exclusivity
	 */
	public boolean isExclusive() {
		return thisEexclusive;
	}

	/**
	 * Gets the exclusivity for the LockedObject
	 * 
	 * @return exclusivity
	 */
	public boolean isShared() {
		return !thisEexclusive;
	}

	/**
	 * Gets the type of the lock
	 * 
	 * @return type
	 */
	public Type getType() {
		return _type;
	}

	/**
	 * Gets the depth of the lock
	 * 
	 * @return depth
	 */
	public int getLockDepth() {
		return lockDepth;
	}

	public void setType(Type type) {
		this._type = type ;
	}

	public void setProp(boolean exclusive, int depth, long expiresAt) {
		this.thisEexclusive = exclusive;
		this.lockDepth = depth;
		this.expiresAt = expiresAt;
	}

	public void setParent(LockedObject parent) {
		this.thisParent = parent ;
		
	}

	public void lengthenParentExpire() {
		if (thisParent != null) thisParent.expiresAt = this.expiresAt ;
	}

	public boolean isExpired() {
		return this.expiresAt < System.currentTimeMillis() ;
	}

	public boolean hasNotChild() {
		return thisChildren == null ;
	}

	public boolean hasNoOwner() {
		return thisOwner.size() == 0 ;
	}

	public Set<LockedObject> getChildren() {
		return this.thisChildren;
	}

	public boolean isReadLock() {
		return Type.READ == getType();
	}
	public boolean isWriteLock() {
		return ! isReadLock();
	}
	public static String makeLockOwner(String prefixName, InnerRequest request) {
		return prefixName + System.currentTimeMillis() + request.toString();
	}

}
