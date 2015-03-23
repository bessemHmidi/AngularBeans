package angularBeans.util;

import java.util.HashMap;
import java.util.Map;

public class Scope {
	
	
	private Map<String, Object> scopeMap = new HashMap<String, Object>();

	public void setProperty(String model, Object value) {
		scopeMap.put(model, value);

	}

	public Object getProperty(String model) {

		return scopeMap.get(model);

	}

	public Map<String, Object> getScopeMap() {
		return scopeMap;
	}

	public void setScopeMap(Map<String, Object> scopeMap) {
		this.scopeMap = scopeMap;
	}
	

}
