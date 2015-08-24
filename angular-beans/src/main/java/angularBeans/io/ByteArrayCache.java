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
package angularBeans.io;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;

/**
 * this is a cache to store java methods calls that return the binary content
 * from AngularBeans components as LobWrapper (@Model properties) or byte[]
 * (remote method invocation result in a stateless way)
 * 
 * @see Call
 * @author Bessem Hmidi
 *
 */

@SuppressWarnings("serial")
@ApplicationScoped
public class ByteArrayCache implements Serializable {

	private Map<String, Call> cache = new HashMap<String, Call>();

	private Map<String, byte[]> tempCache = new HashMap<String, byte[]>();

	@PostConstruct
	public void init() {

	}

	public Map<String, Call> getCache() {
		return cache;
	}

	public Map<String, byte[]> getTempCache() {
		return tempCache;
	}
}
