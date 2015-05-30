package angularBeans.util;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ModelQueryImpl implements ModelQuery ,Serializable{

	private Map<String, Object> data = new HashMap<String, Object>();

	
	private Class owner;
	
	
	public Map<String, Object> getData() {
		return data;
	}

	@Override
	public ModelQuery setProperty(String model, Object value) {
		data.put(model, value);
		return this;
	}

	@Override
	public ModelQuery pushTo(String objectName, Object value) {

		Map<String, Set<Object>> params = null;
		if (!data.containsKey("add")) {
			data.put("add", new HashMap<String, Set<Object>>());
		}

		params = (Map<String, Set<Object>>) data.get("add");

		if (!params.containsKey(objectName)) {
			params.put(objectName, new HashSet<Object>());
		}

		params.get(objectName).add(value);

		return this;

	}

	@Override
	public ModelQuery removeFrom(String objectName, Object value) {

		return removeFrom(objectName, value, "NAN");

	}

	@Override
	public ModelQuery removeFrom(String objectName, Object value, String key) {

		Map<String, Set<Object>> params = null;
		if (!data.containsKey("rm")) {
			data.put("rm", new HashMap<String, Set<Object>>());
		}

		params = (Map<String, Set<Object>>) data.get("rm");

		if (!params.containsKey(objectName)) {
			params.put(objectName, new HashSet<Object>());
		}

		params.get(objectName).add(value);

		params.get(objectName).add("equalsKey:" + key);

		return this;
	}

	public Class getOwner() {
		return owner;
	}

	public void setOwner(Class owner) {
		this.owner = owner;
	}

}
