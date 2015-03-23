package angularBeans.util;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import javax.enterprise.context.SessionScoped;

import angularBeans.context.NGSessionScoped;

@NGSessionScoped
public class Scopes implements Serializable {

	private Map<Class, Scope> allScopes = new HashMap<Class, Scope>();

	public Scope get(Class scope) {

		return allScopes.get(scope);

	}

	public void addScope(Class clazz) {
		allScopes.put(clazz, new Scope());
	}

}
