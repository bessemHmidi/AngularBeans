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

/**
 @author Bessem Hmidi
 */
package angularBeans.realtime;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;


public class RealTimeMessage {

	private Map<String, Object> data = new HashMap<String, Object>();

	public RealTimeMessage setModel(String modelName, Object value) {
		data.put(modelName, value);
		return this;
	}

	public Map<String, Object> build() {
		return data;
	}

	public RealTimeMessage pushToArray(String arrayName, Object value) {
		
		Map<String, Set<Object>> params=null;
		if(!data.containsKey("arrays")){
			data.put("arrays", new HashMap<String, Set<Object>>());
		}
		
		 params=(Map<String, Set<Object>>) data.get("arrays");
		 
		if(!params.containsKey(arrayName))
		{
			params.put(arrayName, new HashSet<Object>());
		}
		
		params.get(arrayName).add(value);

		return this;
	}
	
	
public RealTimeMessage pushToSet(String objectName, Object value,String key) {
		
		Map<String, Set<Object>> params=null;
		if(!data.containsKey("sets")){
			data.put("sets", new HashMap<String, Set<Object>>());
		}
		
		 params=(Map<String,Set<Object>>) data.get("sets");
		 
		if(!params.containsKey(objectName))
		{
			params.put(objectName, new HashSet<Object>());
			params.get(objectName).add("equalsKey:"+key);
		}
		
		params.get(objectName).add(value);
		
		return this;
	}

public RealTimeMessage removeFromObject(String objectName, Object value,String key) {
		
		Map<String, Set<Object>> params=null;
		if(!data.containsKey("rm")){
			data.put("rm", new HashMap<String, Set<Object>>());
		}
		
		 params=(Map<String,Set<Object>>) data.get("rm");
		 
		if(!params.containsKey(objectName))
		{
			params.put(objectName, new HashSet<Object>());
			params.get(objectName).add("equalsKey:"+key);
		}
		
		params.get(objectName).add(value);
		
		return this;
}
	

	
	
}
