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
 * This Servlet will return the "angularBeans" angularJS module as generated
 * javascript via the ModuleGenerator
 * 
 * @author Bessem Hmidi
 */

@WebServlet(urlPatterns = "/angular-beans.js")
public class BootServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7758329463070440974L;

	@Inject
	ModuleGenerator generator;

	@Inject
	Logger log;

	@Inject
	GlobalConnectionHolder globalConnectionHolder;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		long startTime = System.currentTimeMillis();

		globalConnectionHolder.removeConnection(req.getSession().getId());

		generator.setContextPath(req.getScheme() + "://" + req.getServerName() + ":" + req.getServerPort()
				+ req.getServletContext().getContextPath() + "/");

		resp.setContentType("text/javascript");
		StringBuffer stringBuffer = new StringBuffer();
		generator.getScript(stringBuffer);

		long endTime = System.currentTimeMillis();

		log.info("Module generated successfully in " + (endTime - startTime) + " ms");

		resp.getWriter().write(stringBuffer.toString());

		resp.getWriter().flush();
	}

}
