/* 
 * ------------------------------------------------------------------------------------------------
 * Copyright 2011 by Swiss Post, Information Technology Services
 * ------------------------------------------------------------------------------------------------
 * $Id: AbstractMapServerConnectionProvider.java,v 1.2 2012/04/06 01:24:50 bleujin Exp $
 * ------------------------------------------------------------------------------------------------
 *
 */
package org.jminix.server;

import java.util.ArrayList;
import java.util.List;

import javax.management.MBeanServerConnection;

/**
 * Superclass for connection providers providing maps.
 *
 * @author bovetl
 * @version $Revision: 1.2 $
 * @see <script>links('$HeadURL: https://jminix.googlecode.com/svn/tags/jminix-1.0.0/src/main/java/org/jminix/server/AbstractMapServerConnectionProvider.java $');</script>
 */
public abstract class AbstractMapServerConnectionProvider implements ServerConnectionProvider {

	/**
	 * Not explicitly documented.
	 * @see org.jminix.server.ServerConnectionProvider#getConnections()
	 */
	public List<MBeanServerConnection> getConnections() {
		List<MBeanServerConnection> result = new ArrayList<MBeanServerConnection>();
		for(String key: getConnectionKeys()) {
			result.add(getConnection(key));
		}
		return result;
	}

}
