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

import java.util.List;
import java.util.Map;

import net.ion.framework.util.MapUtil;

public abstract class AbstractListResource extends AbstractTemplateResource {
	public AbstractListResource() {
	}

	protected String getTemplateName() {
		return "list";
	}

	@Override
	protected final Map<String, Object> getModel() {
		Map<String, Object> result = MapUtil.newMap();
		result.put("items", getList());
		return result;
	}

	protected abstract List<? extends Object> getList();

}
