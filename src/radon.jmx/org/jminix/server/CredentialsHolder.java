/* 
 * ------------------------------------------------------------------------------------------------
 * Copyright 2011 by Swiss Post, Information Technology Services
 * ------------------------------------------------------------------------------------------------
 * $Id: CredentialsHolder.java,v 1.1 2012/02/17 11:58:58 bleujin Exp $
 * ------------------------------------------------------------------------------------------------
 *
 */
package org.jminix.server;

/**
 * Provides authentication credentials.
 *
 * @author bovetl
 * @version $Revision: 1.1 $
 * @see <script>links('$HeadURL: https://jminix.googlecode.com/svn/tags/jminix-1.0.0/src/main/java/org/jminix/server/CredentialsHolder.java $');</script>
 */
public interface CredentialsHolder {

	public String getUsername();
	
	public String getPassword();
}
