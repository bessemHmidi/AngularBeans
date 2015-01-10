package angularBeans.wsocket;

import java.util.HashMap;
import java.util.Map;

public class WSocketMessage {

	private Map<String, Object> data = new HashMap<String, Object>();

	public WSocketMessage add(String propertyName, Object value) {
		data.put(propertyName, value);
		return this;
	}

	public Map<String, Object> build() {
		return data;
	}

}
