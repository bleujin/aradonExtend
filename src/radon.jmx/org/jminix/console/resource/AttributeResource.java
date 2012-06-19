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

package org.jminix.console.resource;

import java.io.IOException;
import java.util.Map;

import javax.management.Attribute;
import javax.management.AttributeNotFoundException;
import javax.management.InstanceNotFoundException;
import javax.management.IntrospectionException;
import javax.management.InvalidAttributeValueException;
import javax.management.MBeanAttributeInfo;
import javax.management.MBeanException;
import javax.management.MBeanServerConnection;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.ReflectionException;
import javax.management.RuntimeErrorException;
import javax.management.openmbean.CompositeData;
import javax.management.openmbean.TabularData;

import net.ion.framework.util.MapUtil;
import net.ion.radon.core.except.AradonRuntimeException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.restlet.resource.Post;
import org.restlet.resource.ResourceException;

public class AttributeResource extends AbstractTemplateResource {

	private static Log log = LogFactory.getLog(AttributeResource.class);

	public AttributeResource() {
	}

	private String templateName = "attribute";

	@Override
	protected Map<String, Object> getModel() {
		String domain = getRequest().getAttributes().get("domain").toString();

		String mbean = new EncoderBean().decode(getInnerRequest().getAttribute("mbean").toString());

		String attribute = new EncoderBean().decode(getInnerRequest().getAttribute("attribute").toString());

		Map<String, Object> model = MapUtil.newMap();

		try {
			MBeanServerConnection server = getServer();

			MBeanAttributeInfo info = null;
			for (MBeanAttributeInfo i : server.getMBeanInfo(new ObjectName(domain + ":" + mbean)).getAttributes()) {
				if (i.getName().equals(attribute)) {
					info = i;
				}
			}

			Object value = server.getAttribute(new ObjectName(domain + ":" + mbean), attribute);

			model.put("attribute", info);
			if (value == null) {
				model.put("value", "<null>");
			} else if (value.getClass().isArray()) {
				templateName = "array-attribute";
				model.put("items", value);
			} else if (value instanceof CompositeData) {
				templateName = "composite-attribute";
				model.put("attribute", value);
			} else if (value instanceof TabularData) {
				templateName = "tabular-attribute";
				model.put("attribute", value);
			} else {
				model.put("value", value);
			}

			return model;
		} catch (IOException e) {
			throw new RuntimeException(e);
		} catch (AttributeNotFoundException e) {
			throw new RuntimeException(e);
		} catch (InstanceNotFoundException e) {
			throw new RuntimeException(e);
		} catch (MalformedObjectNameException e) {
			throw new RuntimeException(e);
		} catch (MBeanException e) {
			Exception targetException = e.getTargetException();
			if (targetException instanceof RuntimeErrorException) {
				throw new RuntimeException(targetException.getCause());
			}
			log.warn("Error accessing attribute", e);
			model.put("value", e.getTargetException().getCause().getMessage());
			return model;
		} catch (ReflectionException e) {
			throw new RuntimeException(e);
		} catch (IntrospectionException e) {
			throw new RuntimeException(e);
		} catch (RuntimeException e) {
			model.put("value", e.getMessage());
			log.warn("Error accessing attribute", e);
			return model;
		}
	}

	@Post
	public void acceptRepresentation() throws ResourceException {
		String value = getInnerRequest().getParameter("value");

		String domain = getRequest().getAttributes().get("domain").toString();

		String mbean = new EncoderBean().decode(getInnerRequest().getAttribute("mbean").toString());

		String attributeName = new EncoderBean().decode(getInnerRequest().getAttribute("attribute").toString());

		MBeanServerConnection server = getServer();

		try {

			String type = "java.lang.String";
			for (MBeanAttributeInfo info : server.getMBeanInfo(new ObjectName(domain + ":" + mbean)).getAttributes()) {
				if (info.getName().equals(attributeName)) {
					type = info.getType();
				}
			}

			Object attribute = new ValueParser().parse(value, type);

			if (attribute != null) {
				server.setAttribute(new ObjectName(domain + ":" + mbean), new Attribute(attributeName, attribute));
			}

			String queryString = getQueryString();
			if (queryString == null) {
				queryString = "?";
			}
			queryString += "&ok=1";
			getResponse().redirectPermanent(new EncoderBean().encode(queryString) + queryString);
		} catch (InstanceNotFoundException e) {
			throw new AradonRuntimeException(e);
		} catch (AttributeNotFoundException e) {
			throw new AradonRuntimeException(e);
		} catch (InvalidAttributeValueException e) {
			throw new AradonRuntimeException(e);
		} catch (MalformedObjectNameException e) {
			throw new AradonRuntimeException(e);
		} catch (MBeanException e) {
			throw new AradonRuntimeException(e);
		} catch (ReflectionException e) {
			throw new AradonRuntimeException(e);
		} catch (IOException e) {
			throw new AradonRuntimeException(e);
		} catch (IntrospectionException e) {
			throw new AradonRuntimeException(e);
		}

	}

	public boolean allowPost() {
		return true;
	}

	@Override
	protected String getTemplateName() {
		return templateName;
	}

}
