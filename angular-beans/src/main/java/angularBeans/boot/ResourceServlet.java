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

import java.io.IOException;

import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 
 * The ResourceServlet is the resources end point
 * by resources we mean properties files that will be served as JSON data
 * (translation files for example)<br> consumed by "bundleService.loadBundle(bundle_prefix,aleas)".
 * 
 * 
@author Bessem Hmidi
*/

@SuppressWarnings("serial")
@WebServlet(urlPatterns = "/resources/*")
public class ResourceServlet extends HttpServlet {

	@Inject
	ResourcesCache resourcesCache;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		String requestURI = req.getRequestURI();

		int index = (requestURI.indexOf("/resources/")) + 10;

		String resourceName = (requestURI.substring(index));

		resp.setHeader("Access-Control-Allow-Origin", "*");
		
		resp.getWriter().write(
				resourcesCache.get(resourceName, getServletConfig()
						.getServletContext()));

	}

}
