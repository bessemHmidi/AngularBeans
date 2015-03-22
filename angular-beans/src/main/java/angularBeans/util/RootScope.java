package angularBeans.util;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.enterprise.context.SessionScoped;

@SessionScoped
public class RootScope implements Serializable {

	private Map<String, Object> rootScopeMap = new HashMap<String, Object>();

	public void setProperty(String model, Object value) {
		rootScopeMap.put(model, value);

	}

	public Object getProperty(String model) {

		return rootScopeMap.get(model);

	}

	public Set<String> getProperties() {
		// TODO Auto-generated method stub
		return rootScopeMap.keySet();
	}

	public Map<String, Object> getRootScopeMap() {
		return rootScopeMap;
	}

}
