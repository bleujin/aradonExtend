package net.ion.radon.impl.let.velocity;


import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import net.ion.framework.parse.gson.JsonObject;
import net.ion.framework.rest.IResponse;
import net.ion.radon.core.TreeContext;

import org.restlet.data.MediaType;

public class VLetHelper {

	static Map<String, Object> makeDataModel(Map<String, Object> params) {
		Map<String, Object> dataModel = new HashMap<String, Object>();
		for (Entry<String, Object> entry : params.entrySet()) {
			if (!(entry.getValue() instanceof String))
				continue;
			final String entryValue = (String) entry.getValue();
			if (entryValue.startsWith("{") ) {
				dataModel.put(entry.getKey(), JsonObject.fromObject(entryValue));
			} else {
				dataModel.put(entry.getKey(), entryValue);
			}
		}
		dataModel.put("request", params);
		dataModel.put("response", IResponse.EMPTY_RESPONSE);
		return dataModel;
	}
	
	static MediaType getMediaType(TreeContext context){
		return MediaType.valueOf(context.getAttributeObject("let.result.mediatype", "text/all", String.class)) ;	
	}

	static String getTemplateParamName(TreeContext context) {
		return context.getAttributeObject("template.param.name", "template", String.class);
	}

}
