package angularBeans.util;

import java.util.HashMap;
import java.util.Map;

public interface Scope {

	public  void setProperty(String model, Object value);

	//Object getProperty(String model);

	Map<String, Object> getScopeMap();
	
	
	

}
