package org.espritjug.angularBridge.context;

import java.util.HashMap;
import java.util.Map;

public class GlobalMapHolder {

	private static Map<String, NGSessionContextHolder> map = new HashMap<String, NGSessionContextHolder>();

	public static synchronized void destroySession(String holderId) {

		map.remove(holderId);

	}

	public static synchronized NGSessionContextHolder get(String holderId) {

		if (!map.containsKey(holderId)) {
			map.put(holderId, new NGSessionContextHolder());
		}
		return map.get(holderId);
	}

}
