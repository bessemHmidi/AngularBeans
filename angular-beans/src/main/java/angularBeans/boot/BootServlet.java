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
import java.util.logging.Logger;

import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import angularBeans.realtime.GlobalConnectionHolder;

/**
 * Returns a generated script for resource named "angularBeans.js". Using the ModuleGenerator,
 * the script will be lazily generated when accessing this resource. 
 * 
 * @author Bessem Hmidi
 * @author Aymen Naili
 */

@WebServlet(urlPatterns = "/angular-beans.js")
public class BootServlet extends HttpServlet {

	@Inject
	ModuleGenerator generator;

	@Inject
	Logger log;

	@Inject
	GlobalConnectionHolder globalConnectionHolder;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		globalConnectionHolder.removeConnection(req.getSession().getId());
		
		StringBuffer contextPathBuffer = new StringBuffer(req.getScheme());
		StringBuffer stringBuffer = new StringBuffer();
		
		contextPathBuffer.append("://");
		contextPathBuffer.append(req.getServerName());
		contextPathBuffer.append(":");
		contextPathBuffer.append(req.getServerPort());
		contextPathBuffer.append(req.getServletContext().getContextPath());
		contextPathBuffer.append("/");
		
		generator.setContextPath(contextPathBuffer.toString());
		generator.getScript(stringBuffer);
		
		resp.setContentType("text/javascript");
		resp.getWriter().write(stringBuffer.toString());
		resp.getWriter().flush();
	}
	/**
	 * 
	 */
	private static final long serialVersionUID = 7758329463070440974L;
}
