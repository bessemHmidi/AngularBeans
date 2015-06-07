package angularBeans.util;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.enterprise.inject.Default;


public class RootScope implements Serializable {

	
	private Map<String, Object> rootScopeMap = Collections.synchronizedMap(new HashMap<String, Object>());

	public void setProperty(String model, Object value) {
		rootScopeMap.put(model, value);

	}

	public synchronized Object getProperty(String model) {

		Object value=rootScopeMap.get(model);
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