package net.ion.radon.impl.let.webdav.locking;

import java.util.Hashtable;
import java.util.List;

import net.ion.framework.util.Debug;
import net.ion.framework.util.ListUtil;
import net.ion.radon.impl.let.webdav.ITransaction;
import net.ion.radon.impl.let.webdav.exceptions.LockFailedException;
import net.ion.radon.impl.let.webdav.locking.LockedObject.Type;

import org.restlet.data.Status;
import org.restlet.resource.ResourceException;

/**
 * simple locking management for concurrent data access, NOT the webdav locking. ( could that be used instead? )
 * 
 * IT IS ACTUALLY USED FOR DOLOCK
 * 
 * @author re
 */
public class MemLockManager implements IResourceLocks, LockManager {

	/**
	 * after creating this much LockedObjects, a cleanup deletes unused LockedObjects
	 */
	private final int _cleanupLimit = 100000;

	protected int _cleanupCounter = 0;

	/**
	 * keys: path value: LockedObject from that path Concurrent access can occur
	 */
	protected Hashtable<String, LockedObject> _locks = new Hashtable<String, LockedObject>();

	/**
	 * keys: id value: LockedObject from that id Concurrent access can occur
	 */
	protected Hashtable<String, LockedObject> _locksByID = new Hashtable<String, LockedObject>();

	/**
	 * keys: path value: Temporary LockedObject from that path Concurrent access can occur
	 */
	protected Hashtable<String, LockedObject> _tempLocks = new Hashtable<String, LockedObject>();

	/**
	 * keys: id value: Temporary LockedObject from that id Concurrent access can occur
	 */
	protected Hashtable<String, LockedObject> _tempLocksByID = new Hashtable<String, LockedObject>();

	// REMEMBER TO REMOVE UNUSED LOCKS FROM THE HASHTABLE AS WELL

	protected LockedObject _writeLockRoot = null;

	protected LockedObject _readLockRoot = null;

	public MemLockManager() {
		_writeLockRoot = createLockedObject("/", Type.WRITE);
		_readLockRoot = createLockedObject("/", Type.READ);
	}

	public synchronized boolean lock(ITransaction transaction, String path, String owner, boolean exclusive, int depth, int timeout, boolean isReadLock) throws LockFailedException {

		LockedObject lo = null;

		if (isReadLock) {
			lo = generateLockedObjects(transaction, path, _locks, _writeLockRoot);
		} else {
			lo = generateLockedObjects(transaction, path, _tempLocks, _readLockRoot);
		}

		if (lo.checkLocks(exclusive, depth)) {
			lo.setProp(exclusive, depth, System.currentTimeMillis() + (timeout * 1000));
			lo.lengthenParentExpire();
			lo.addLockedObjectOwner(owner); 
			return true;
		} else {
			// can not lock
			Debug.trace("Lock resource at " + path + " failed because" + "\na parent or child resource is currently locked");
			throw new ResourceException(Status.CLIENT_ERROR_LOCKED) ;
		}
	}

	public synchronized boolean unlock(ITransaction transaction, String id, String owner) {

		if (_locksByID.containsKey(id)) {
			String path = _locksByID.get(id).getPath();
			if (_locks.containsKey(path)) {
				LockedObject lo = _locks.get(path);
				lo.removeLockedObjectOwner(owner);

				if (lo.getChildren().size() == 0 && lo.getOwner().length == 0)
					lo.removeLockedObject(lo);

			} else {
				// there is no lock at that path. someone tried to unlock it anyway. could point to a problem
				Debug.trace("locking.ResourceLocks.unlock(): no lock for path " + path);
				return false;
			}

			if (_cleanupCounter > _cleanupLimit) {
				_cleanupCounter = 0;
				cleanLockedObjects(transaction, _writeLockRoot);
			}
		}
		checkTimeouts(transaction);

		return true;

	}

	public synchronized void unlockReadLockedObjects(ITransaction transaction, String path, String owner) {
		if (_tempLocks.containsKey(path)) {
			LockedObject lo = _tempLocks.get(path);
			lo.removeLockedObjectOwner(owner);

		} else {
			// there is no lock at that path. someone tried to unlock it anyway. could point to a problem
			Debug.trace("net.sf.webdav.locking.ResourceLocks.unlock(): no lock for path " + path);
		}

		if (_cleanupCounter > _cleanupLimit) {
			_cleanupCounter = 0;
			cleanLockedObjects(transaction, _readLockRoot);
		}

		checkTimeouts(transaction);

	}

	public void checkTimeouts(ITransaction transaction) {
		List<LockedObject> removeTarget = ListUtil.newList();

		for (LockedObject lo : _locks.values()) {
			if (lo.isExpired()) {
				removeTarget.add(lo);
			}
		}
		for (LockedObject lo : _tempLocks.values()) {
			if (lo.isExpired()) {
				removeTarget.add(lo);
			}
		}

		for (LockedObject target : removeTarget) {
			target.removeLockedObject(target);
		}
	}

	public boolean exclusiveLock(ITransaction transaction, String path, String owner, int depth, int timeout) throws LockFailedException {
		return lock(transaction, path, owner, true, depth, timeout, false);
	}

	public boolean sharedLock(ITransaction transaction, String path, String owner, int depth, int timeout) throws LockFailedException {
		return lock(transaction, path, owner, false, depth, timeout, false);
	}

	public LockedObject getWriteLockedObjectByID(ITransaction transaction, String id) {
		if (_locksByID.containsKey(id)) {
			return _locksByID.get(id);
		} else {
			return null;
		}
	}

	public LockedObject getWriteLockedObjectByPath(ITransaction transaction, String path) {
		if (_locks.containsKey(path)) {
			return (LockedObject) this._locks.get(path);
		} else {
			return null;
		}
	}

	public LockedObject getReadLockedObjectByID(ITransaction transaction, String id) {
		if (_tempLocksByID.containsKey(id)) {
			return _tempLocksByID.get(id);
		} else {
			return null;
		}
	}

	public LockedObject getReadLockedObjectByPath(ITransaction transaction, String path) {
		if (_tempLocks.containsKey(path)) {
			return (LockedObject) this._tempLocks.get(path);
		} else {
			return null;
		}
	}

	private LockedObject generateLockedObjects(ITransaction transaction, String path, Hashtable<String, LockedObject> lockTable, LockedObject root) {
		if (!lockTable.containsKey(path)) {

			LockedObject returnObject = createLockedObject(path, root.getType());
			String parentPath = getParentPath(path);
			if (parentPath != null) {
				LockedObject parentLockedObject = generateLockedObjects(transaction, parentPath, lockTable, root);
				parentLockedObject.addChild(returnObject);
				returnObject.setParent(parentLockedObject);
			} else {
				returnObject.setParent(root);
			}
			return returnObject;
		} else {
			// there is already a LockedObject on the specified path
			return (LockedObject) lockTable.get(path);
		}
	}

	private LockedObject createLockedObject(String path, Type type){
		LockedObject lo = new LockedObject(this, path, type) ;
		return lo ;
	}

	public void addLock(String path, Type type, LockedObject lo) {
		if (Type.WRITE == type) {
			_locks.put(path, lo);
			_locksByID.put(lo.getID(), lo);
		} else {
			_tempLocks.put(path, lo);
			_tempLocksByID.put(lo.getID(), lo);
		}
		_cleanupCounter++;
	}
	

	private boolean cleanLockedObjects(ITransaction transaction, LockedObject lo) {

		if (lo.hasNotChild()) {
			if (lo.hasNoOwner()) {
				lo.removeLockedObject(lo);
				return true;
			} else {
				return false;
			}
		} else {
			boolean canDelete = true;

			for (LockedObject child : lo.getChildren()) {
				if (!cleanLockedObjects(transaction, lo)) {
					canDelete = false;
				}
			}

			if (canDelete) {
				if (lo.hasNoOwner()) {
					lo.removeLockedObject(lo);
					return true;
				} else {
					return false;
				}
			} else {
				return false;
			}
		}
	}

	/**
	 * creates the parent path from the given path by removing the last '/' and everything after that
	 * 
	 * @param path
	 *            the path
	 * @return parent path
	 */
	private String getParentPath(String path) {
		int slash = path.lastIndexOf('/');
		if (slash == -1) {
			return null;
		} else {
			if (slash == 0) {
				// return "root" if parent path is empty string
				return "/";
			} else {
				return path.substring(0, slash);
			}
		}
	}

	public void removeLocks(LockedObject lo) {
		if (lo.getType() == Type.READ) {
			_tempLocksByID.remove(lo.getID());
			_tempLocks.remove(lo.getPath());
		} else {
			_locksByID.remove(lo.getID());
			_locks.remove(lo.getPath());
		}
	}

	public LockedObject getLockedObject(Type type) {
		return (type == Type.READ) ? _readLockRoot : _writeLockRoot;
	}

}
