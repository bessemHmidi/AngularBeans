/*
 * AngularBeans, CDI-AngularJS bridge 
 *
 * Copyright (c) 2014, Bessem Hmidi. or third-party contributors as
 * indicated by the @author tags or express copyright attribution
 * statements applied by the authors.
 *
 * This copyrighted material is made available to anyone wishing to use, modify,
 * copy, or redistribute it subject to the terms and conditions of the GNU
 * Lesser General Public License, as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License
 * for more details.
 *
 */
package angularBeans.context;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * map a group of sockJS sessions to the current HTTP session.
 *
 * @author Bessem Hmidi
 *
 */
public class SessionMapper {

	private static final Map<String, Set<String>> sessionsMap = new HashMap<>();

	public static Map<String, Set<String>> getSessionsMap() {
		return sessionsMap;
	}

	public static String getHTTPSessionID(String sockJSSessionID) {

		for (String httpSession : sessionsMap.keySet()) {

			if (sessionsMap.get(httpSession).contains(sockJSSessionID)) {
				return httpSession;
			}

		}

		return null;
	}

}
