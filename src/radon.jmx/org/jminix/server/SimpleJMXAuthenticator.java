/* 
 * ------------------------------------------------------------------------------------------------
 * Copyright 2011 by Swiss Post, Information Technology Services
 * ------------------------------------------------------------------------------------------------
 * $Id: SimpleJMXAuthenticator.java,v 1.1 2012/02/17 11:58:58 bleujin Exp $
 * ------------------------------------------------------------------------------------------------
 *
 */
package org.jminix.server;

import javax.management.remote.JMXAuthenticator;
import javax.security.auth.Subject;

/**
 * A trivial authenticator.
 *
 * @author bovetl
 * @version $Revision: 1.1 $
 * @see <script>links('$HeadURL: https://jminix.googlecode.com/svn/tags/jminix-1.0.0/src/main/java/org/jminix/server/SimpleJMXAuthenticator.java $');</script>
 */
public class SimpleJMXAuthenticator implements JMXAuthenticator, CredentialsHolder {

	private String username;
	
	private String password;
	
	/**
	 * Not explicitly documented.
	 * @see javax.management.remote.JMXAuthenticator#authenticate(java.lang.Object)
	 */
	public Subject authenticate(Object credentials) {
		Subject subject = null;
		if(credentials instanceof String[]) {
			String[] stringCredentials = (String[])credentials;
			if(stringCredentials.length==2 &&
					username.equals(stringCredentials[0]) && 
					password.equals(stringCredentials[1])) {
				subject = new Subject();
			}
		}
		if(subject==null) {
			throw new SecurityException("Bad credentials");
		}
		return subject;	
	}

	/**
	 * Sets the attribute username.
	 * @param username The username to set.
	 */
	public void setUsername(String username) {
		this.username = username;
	}

	/**
	 * Sets the attribute password.
	 * @param password The password to set.
	 */
	public void setPassword(String password) {
		this.password = password;
	}

	/**
	 * Gets the attribute username.
	 * @return username.
	 */
	public String getUsername() {
		return username;
	}

	/**
	 * Gets the attribute password.
	 * @return password.
	 */
	public String getPassword() {
		return password;
	}
}
