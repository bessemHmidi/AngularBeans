package angularBeans.util;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.enterprise.inject.Default;

/**
 * 
 * @author bessem
 * an object representing the $rootScope
 */

public class RootScope implements Serializable {

	
	private Map<String, Object> rootScopeMap = Collections.synchronizedMap(new HashMap<String, Object>());

	
	
	/**
	 * change the value of the model of the $rootScope
	 * @param model
	 * @param value
	 */
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