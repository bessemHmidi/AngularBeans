package angularBeans.util;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class ScopeImpl implements Scope{

	private Map<String, Object> scopeMap = Collections.synchronizedMap(new HashMap<String, Object>());

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


	public synchronized Map<String, Object> getScopeMap() {
		return scopeMap;
	}

	public void setScopeMap(Map<String, Object> scopeMap) {
		this.scopeMap = scopeMap;
	}
	
}
