package net.ion.radon.impl.let.webdav.locking;

import net.ion.radon.impl.let.webdav.ITransaction;
import net.ion.radon.impl.let.webdav.exceptions.LockFailedException;
import net.ion.radon.impl.let.webdav.locking.LockedObject.Type;

public class IgnoreLockManager implements LockManager {

	public void addLock(String path, Type type, LockedObject lockedObject) {
		
	}

	public void checkTimeouts(ITransaction transaction) {
			
	}

	public boolean exclusiveLock(ITransaction transaction, String path, String owner, int depth, int timeout) throws LockFailedException {
		return false;
	}

	public LockedObject getLockedObject(Type type) {
		return null;
	}

	public LockedObject getReadLockedObjectByID(ITransaction transaction, String id) {
		return null;
	}

	public LockedObject getReadLockedObjectByPath(ITransaction transaction, String path) {
		return null;
	}

	public LockedObject getWriteLockedObjectByID(ITransaction transaction, String id) {
		return null;
	}

	public LockedObject getWriteLockedObjectByPath(ITransaction transaction, String path) {
		return null;
	}

	public boolean lock(ITransaction transaction, String path, String owner, boolean exclusive, int depth, int timeout, boolean isReadLock) throws LockFailedException {
		return true;
	}

	public void removeLocks(LockedObject lo) {
		
	}

	public boolean sharedLock(ITransaction transaction, String path, String owner, int depth, int timeout) throws LockFailedException {
		return true;
	}

	public boolean unlock(ITransaction transaction, String id, String owner) {
		return true;
	}

	public void unlockReadLockedObjects(ITransaction transaction, String path, String owner) {
		
	}

}
