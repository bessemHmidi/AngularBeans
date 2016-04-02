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
package angularBeans.js;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * A cache for the static (non beans instances dependent) angular-beans.js code
 *
 * @author bassem Hmidi
 * @author Michael Kulla <info@michael-kulla.com>
 * @see angularBeans.js.FileLoader
 *
 */
public class StaticJsCache {

	/**
	 * the angularBeansMainObject is the angularBeans object in the angularBeans
	 * javascript api
	 */
	public static String angularBeansMainObject;

	static {
		try {
			final FileLoader loader = new FileLoader();
			final String scriptDetection = loader.readFile("/js/script-detection.js");
			angularBeansMainObject = scriptDetection + loader.readFile("/js/angular-beans-main-object.js");
		} catch (IOException ex) {
			Logger.getLogger(StaticJsCache.class.getName()).log(Level.SEVERE, null, ex);
			throw new RuntimeException(ex);
		}

	}

	//TODO clean up dirty naming
	public static StringBuilder CORE_SCRIPT = new StringBuilder();
	public static Map<Class, StringBuffer> CACHED_BEAN_STATIC_PART = new HashMap<>();
	public static StringBuilder EXTENTIONS_SCRIPT = new StringBuilder();
	public static StringBuilder VALIDATION_SCRIPT = new StringBuilder();

}
