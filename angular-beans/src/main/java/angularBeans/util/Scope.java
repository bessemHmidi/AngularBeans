package angularBeans.util;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface Scope {

	public void setProperty(String model, Object value);

	// Object getProperty(String model);

	Map<String, Object> getScopeMap();

	public void pushToArray(String string, Object value);

	Map<String, Set<Object>> getArraysMap();

}
