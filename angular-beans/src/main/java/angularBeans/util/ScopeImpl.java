package angularBeans.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ScopeImpl implements Scope{

	private Map<String, Object> scopeMap = Collections.synchronizedMap(new HashMap<String, Object>());

	private Map<String, Set<Object>> arraysMap = Collections.synchronizedMap(new HashMap<String, Set<Object>>());
	
	@Override
	public  synchronized void  setProperty(String model, Object value) {
		scopeMap.put(model, value);

	}

//	@Override
//	public Object getProperty(String model) {
//
//		Object value=scopeMap.get(model);
//		scopeMap.remove(model);
//		return value;
//
//	}

	@Override
	public synchronized Map<String, Object> getScopeMap() {
		return scopeMap;
	}

	@Override
	public Map<String, Set<Object>> getArraysMap() {
		return arraysMap;
	}

	@Override
	public void pushToArray(String arrayName, Object value) {
		if(!arraysMap.containsKey(arrayName)) {arraysMap.put(arrayName, new HashSet<Object>());}
		arraysMap.get(arrayName).add(value);
	}
	
	
	
	
}
