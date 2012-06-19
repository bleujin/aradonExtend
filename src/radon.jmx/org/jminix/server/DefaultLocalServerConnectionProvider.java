/* 
 * Copyright 2009 Laurent Bovet, Swiss Post IT <lbovet@jminix.org>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 */

package org.jminix.server;

import java.lang.management.ManagementFactory;
import java.util.List;

import javax.management.MBeanServerConnection;
import javax.management.MBeanServerFactory;

import net.ion.framework.util.ListUtil;

import org.apache.commons.logging.LogFactory;

public class DefaultLocalServerConnectionProvider extends AbstractListServerConnectionProvider {

	public List<MBeanServerConnection> getConnections() {
		List<MBeanServerConnection> result = ListUtil.newList();

		// Make sure the platform server is instanciated
		try {
			ManagementFactory.getPlatformMBeanServer();
		} catch (SecurityException ex) {
			LogFactory.getLog(this.getClass()).warn("Not allowed to obtain the Java platform MBeanServer", ex);
		}

		try {
			List servers = MBeanServerFactory.findMBeanServer(null);

			LogFactory.getLog(this.getClass()).debug("Found " + servers.size() + " MBean servers");

			result.addAll(servers);
		} catch (Exception e) {
			LogFactory.getLog(this.getClass()).warn("Could not register the Java platform MBeanServer", e);
		}

		return result;
	}

}