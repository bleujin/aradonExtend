package net.ion.radon.impl.let.webdav;

import org.restlet.security.User;

public interface ITransaction {

	public static ITransaction EMPTY = new ITransaction() {
		public  User getUser() {
			return new User();
		}
	}; 
	
	User getUser();

}
