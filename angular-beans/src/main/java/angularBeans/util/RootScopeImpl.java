/*
 * AngularBeans, CDI-AngularJS bridge 
 *
 * Copyright (c) 2014, Bessem Hmidi. or third-party contributors as
 * indicated by the @author tags or express copyright attribution
 * statements applied by the authors.
 *
 * This copyrighted material is made available to anyone wishing to use, modify,
 * copy, or redistribute it subject to the terms and conditions of the GNU
 * Lesser General Public License, as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License
 * for more details.
 *
 */
package angularBeans.util;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.enterprise.inject.Alternative;

/**
 * 
 * @author bessem an object representing the $rootScope when updated this give a
 *         server to client way update only
 */

@SuppressWarnings("serial")
@Alternative
public class RootScopeImpl implements RootScope, Serializable {

	private Map<String, Object> rootScopeMap = Collections.synchronizedMap(new HashMap<String, Object>());

	/**
	 * change the value of the model of the $rootScope
	 * 
	 * @param model
	 * @param value
	 */

	@Override
	public void setProperty(String model, Object value) {
		rootScopeMap.put(model, value);

	}

	public synchronized Object getProperty(String model) {

		Object value = rootScopeMap.get(model);
		rootScopeMap.remove(value);
		return value;

	}

	public Set<String> getProperties() {
		return rootScopeMap.keySet();
	}

	public synchronized Map<String, Object> getRootScopeMap() {
		return rootScopeMap;
	}
}