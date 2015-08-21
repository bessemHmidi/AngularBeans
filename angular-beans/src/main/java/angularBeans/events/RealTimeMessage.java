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


package angularBeans.events;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * a RealTime message is a server to client message
 * translated to an angularJS event. (only with realTime context)
 * @author Bessem Hmidi
 *
 */
public class RealTimeMessage {

	private Map<String, Object> data = new HashMap<String, Object>();

	public RealTimeMessage set(String modelName, Object value) {
		data.put(modelName, value);
		return this;
	}

	
	public Map<String, Object> build() {
		return data;
	}

//	public RealTimeMessage pushToArray(String arrayName, Object value) {
//		
//		Map<String, Set<Object>> params=null;
//		if(!data.containsKey("arrays")){
//			data.put("arrays", new HashMap<String, Set<Object>>());
//		}
//		
//		 params=(Map<String, Set<Object>>) data.get("arrays");
//		 
//		if(!params.containsKey(arrayName))
//		{
//			params.put(arrayName, new HashSet<Object>());
//		}
//		
//		params.get(arrayName).add(value);
//
//		return this;
//	}
	

	@Override
	public String toString() {
		return data.toString();
	}
	
}
