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

package angularBeans.boot;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.servlet.ServletContext;

import angularBeans.util.AngularBeansUtil;
/**
 * 
 * The ResourcesCache is a cache for already requested resources (any 
 * properties files converted to JSON) to avoid redundant transformations.
 * used by ResourceServlet
 * 
 * 
@author Bessem Hmidi
*/

@ApplicationScoped
public class ResourcesCache {

	
	@Inject
	AngularBeansUtil util;
	
  
	private Map<String, String> cache=new HashMap<String, String>();
	
	public String get(String resourceName,ServletContext servletContext) {
		
	
		String json=null;
		
		if(!cache.containsKey(resourceName)){
		
		InputStream is = servletContext.getResourceAsStream(
				 "/META-INF" +resourceName+ ".properties");
		
		Properties properties = new Properties();

		try {
			properties.load(is);
			
			json=util.getJson(properties);
		} catch (IOException e) {
			
			e.printStackTrace();
		}
		
		}

		return json;
	}

}
