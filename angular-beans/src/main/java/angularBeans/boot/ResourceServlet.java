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

/**
 @author Bessem Hmidi
 */
package angularBeans.boot;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import angularBeans.util.AngularBeansUtil;

@WebServlet(urlPatterns = "/resources/*")
public class ResourceServlet extends HttpServlet {

	@Inject
	AngularBeansUtil util;
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		String requestURI = req.getRequestURI();

		int index = (requestURI.indexOf("/resources/")) + 10;

		InputStream is =null;
		try {
			 is = getServletConfig().getServletContext().getResourceAsStream(
					"/META-INF" + (requestURI.substring(index)) + ".properties");
		} catch (Exception e) {
			
			e.printStackTrace();
		}
		

	//	ServletContext application = getServletConfig().getServletContext();
//		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
//		InputStream input = classLoader.getResourceAsStream("WEB-INF/"+ (requestURI.substring(index)) + ".properties");
		
		
		
		Properties properties = new Properties();
		
		
		properties.load(is);

		
		
		resp.getWriter().write(util.getJson(properties));

	}

}
