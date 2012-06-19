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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.management.MBeanAttributeInfo;
import javax.management.MBeanServerConnection;

import net.ion.framework.parse.gson.JsonParser;
import net.ion.framework.util.MapUtil;
import net.ion.radon.core.let.AbstractServerResource;

import org.apache.velocity.Template;
import org.apache.velocity.app.VelocityEngine;
import org.jminix.console.radon.MyConstants;
import org.jminix.server.ServerConnectionProvider;
import org.jminix.type.HtmlContent;
import org.restlet.Request;
import org.restlet.data.MediaType;
import org.restlet.data.Preference;
import org.restlet.ext.velocity.TemplateRepresentation;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.representation.Variant;
import org.restlet.resource.Get;
import org.restlet.resource.ResourceException;

public abstract class AbstractTemplateResource extends AbstractServerResource {
	public String a;

	public AbstractTemplateResource() {

	}

	protected abstract String getTemplateName();

	protected abstract Map<String, Object> getModel();

	@Get
	public Representation view(Variant variant) throws ResourceException {
		List<Preference<MediaType>> acceptedMediaTypes = getRequest().getClientInfo().getAcceptedMediaTypes();
		
		MediaType mtype = findAcceptedMediaType(getRequest()) ;
		if (MediaType.APPLICATION_JSON.equals(mtype) || MediaType.APPLICATION_JAVASCRIPT.equals(mtype)){
			return json() ;
		} else if (MediaType.TEXT_ALL.equals(mtype)) {
			return text() ;
		} else {
			return html() ;
		}
	}
	
	private MediaType findAcceptedMediaType(Request request) {

		float maxQuality = 0f;
		MediaType maxMediaType = MediaType.ALL;
		for (Preference<MediaType> mtype : request.getClientInfo().getAcceptedMediaTypes()) {
			if (mtype.getQuality() > maxQuality) {
				maxQuality = mtype.getQuality() ;
				maxMediaType = mtype.getMetadata();
			}
		}
		return maxMediaType;
	}

	private Representation html() {
		Map<String, Object> enrichedModel = MapUtil.newMap();

		String templateName = getTemplateName();
		if (enrichedModel.get("value") instanceof HtmlContent) {
			templateName = "html-attribute";
		}

		VelocityEngine ve = getContext().getAttributeObject(MyConstants.VELOCITY_ENGINE_CONTEX_KEY, VelocityEngine.class);
		Template template = ve.getTemplate("jminix/templates/" + templateName + ".vm");

		String skin = getInnerRequest().getParameter("skin", "default") ;
		String desc = getInnerRequest().getParameter("desc", "on") ;
		String ok = getInnerRequest().getParameter("ok") ;
		
		enrichedModel.put("query", getQueryString());
		enrichedModel.put("ok", "1".equals(ok));
		enrichedModel.put("margin", "embedded".equals(skin) ? 0 : 5);
		enrichedModel.put("skin", skin);
		enrichedModel.put("desc", desc);
		enrichedModel.put("encoder", new EncoderBean());
		enrichedModel.put("request", getRequest());

		TemplateRepresentation result = new TemplateRepresentation(template, enrichedModel, MediaType.TEXT_HTML);
		return result;
	}

	private Representation json() {
		// Translate known models, needs a refactoring to embed that in each resource...
		Map<String, Object> result = MapUtil.newMap();

		result.put("label", getRequest().getOriginalRef().getLastSegment(true));

		String beforeLast = getRequest().getOriginalRef().getSegments().size() > 2 ? getRequest().getResourceRef().getSegments().get(getRequest().getOriginalRef().getSegments().size() - 3) : null;
		boolean leaf = "attributes".equals(beforeLast) || "operations".equals(beforeLast);

		if (getModel().containsKey("items") && !leaf) {
			Object items = getModel().get("items");

			Collection<Object> itemCollection = null;
			if (items instanceof Collection) {
				itemCollection = (Collection<Object>) items;
			} else {
				itemCollection = Arrays.asList(items);
			}
			List<Map<String, String>> children = new ArrayList<Map<String, String>>();
			for (Object item : itemCollection) {

				HashMap<String, String> ref = new HashMap<String, String>();

				if (item instanceof MBeanAttributeInfo) {
					ref.put("$ref", new EncoderBean().encode(((MBeanAttributeInfo) item).getName()) + "/");
				} else if (item instanceof Map && ((Map) item).containsKey("declaration")) {
					ref.put("$ref", ((Map) item).get("declaration").toString());
				} else {
					ref.put("$ref", item.toString() + "/");
				}
				children.add(ref);
			}
			result.put("children", children);
		} else {
			if (getModel().containsKey("value")) {
				if (getModel().get("value") instanceof HtmlContent) {
					result.put("value", "...");
				} else {
					result.put("value", getModel().get("value").toString());
				}
			} else if (getModel().containsKey("items")) {
				Object items = getModel().get("items");
				String value = null;
				if (items.getClass().isArray()) {
					try {
						value = Arrays.deepToString((Object[]) items);
					} catch (ClassCastException ex) {
						try {
							value = (String) Arrays.class.getMethod("toString", items.getClass()).invoke(null, items);
						} catch (Throwable e) {
							throw new IllegalArgumentException(e) ;
						}
					}
//					value = Arrays.deepToString((Object[]) items);
				} else {
					value = items.toString();
				}
				result.put("value", value);
			}
		}

		// Hack because root must be a list for dojo tree...
		if ("servers".equals(getRequest().getOriginalRef().getLastSegment(true))) {
			return new StringRepresentation(JsonParser.fromObject(new Object[] { result }).toString());
		} else {
			return new StringRepresentation(JsonParser.fromObject(result).toString());
		}
	}

	private Representation text() {
		Map<String, Object> enrichedModel = new HashMap<String, Object>(getModel());

		Template template;
		try {
			VelocityEngine ve = new VelocityEngine();

			Properties p = new Properties();
			p.setProperty("resource.loader", "class");
			p.setProperty("class.resource.loader.description", "Velocity Classpath Resource Loader");
			p.setProperty("class.resource.loader.class", "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");

			ve.init(p);

			template = ve.getTemplate("jminix/templates/" + getTemplateName() + "-plain.vm");
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

		enrichedModel.put("encoder", new EncoderBean());
		enrichedModel.put("request", getRequest());

		return new TemplateRepresentation(template, enrichedModel, MediaType.TEXT_PLAIN);
	}

	protected ServerConnectionProvider getServerProvider() {
		return getContext().getAttributeObject("serverProvider", ServerConnectionProvider.class);
	}

	protected MBeanServerConnection getServer() {
		return getServerProvider().getConnection(getRequest().getAttributes().get("server").toString());
	}

	protected String getQueryString() {
		String query = getRequest().getResourceRef().getQuery();
		return query != null ? "?" + query : "";
	}
}
