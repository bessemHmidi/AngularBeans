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

package angularBeans.events;

import java.util.HashMap;
import java.util.Map;

/**
 * a RealTime message is a server to client message translated to an angularJS
 * event. (only with realTime context)
 * 
 * @author Bessem Hmidi
 *
 */
public class RealTimeMessage {

	private final Map<String, Object> data = new HashMap<>();

	public RealTimeMessage set(String modelName, Object value) {
		data.put(modelName, value);
		return this;
	}

	public Map<String, Object> build() {
		return data;
	}

	@Override
	public String toString() {
		return data.toString();
	}

}
