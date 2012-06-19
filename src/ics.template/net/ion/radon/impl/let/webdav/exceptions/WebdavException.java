/*
 * Copyright 1999,2004 The Apache Software Foundation.
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

package net.ion.radon.impl.let.webdav.exceptions;

import org.restlet.data.Status;

public class WebdavException extends RuntimeException {

	private static final long serialVersionUID = 5301687936273828398L;
	private Status status = Status.SERVER_ERROR_INTERNAL;

	public WebdavException(Status status) {
        super(status.getDescription());
        this.status = status ;
    }

	public WebdavException(String message) {
        super(message);
    }

	public WebdavException(Throwable cause) {
        super(cause);
    }
	
    public WebdavException(Status status, Throwable cause) {
        super(cause);
        this.status = status ;
    }
    
    public static WebdavException create(Status status){
    	return new WebdavException(status) ;
    }

	public static WebdavException create(Status status, Throwable cause) {
    	return new WebdavException(status, cause) ;
	}
	
	public Status getStatus(){
		return status ;
	}

	public static WebdavException create(String message) {
		return new WebdavException(message);
	}
}
