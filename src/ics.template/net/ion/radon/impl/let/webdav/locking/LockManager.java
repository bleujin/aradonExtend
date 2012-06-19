/*
 * Copyright 2005-2006 webdav-servlet group.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.ion.radon.impl.let.webdav.locking;

import net.ion.radon.impl.let.webdav.ITransaction;
import net.ion.radon.impl.let.webdav.exceptions.LockFailedException;
import net.ion.radon.impl.let.webdav.locking.LockedObject.Type;

/**
 * simple locking management for concurrent data access, NOT the webdav locking. ( could that be used instead? )
 * 
 * IT IS ACTUALLY USED FOR DOLOCK
 * 
 * @author re
 */
public interface LockManager extends IResourceLocks {

	public boolean lock(ITransaction transaction, String path, String owner, boolean exclusive, int depth, int timeout, boolean isReadLock) throws LockFailedException;

	public boolean unlock(ITransaction transaction, String id, String owner);

	public void unlockReadLockedObjects(ITransaction transaction, String path, String owner);

	public void checkTimeouts(ITransaction transaction);

	public boolean exclusiveLock(ITransaction transaction, String path, String owner, int depth, int timeout) throws LockFailedException;

	public boolean sharedLock(ITransaction transaction, String path, String owner, int depth, int timeout) throws LockFailedException;

	public LockedObject getWriteLockedObjectByID(ITransaction transaction, String id);

	public LockedObject getWriteLockedObjectByPath(ITransaction transaction, String path);

	public LockedObject getReadLockedObjectByID(ITransaction transaction, String id);

	public LockedObject getReadLockedObjectByPath(ITransaction transaction, String path);

	public void removeLocks(LockedObject lo);

	public void addLock(String path, Type type, LockedObject lockedObject);

	public LockedObject getLockedObject(Type type);

}
