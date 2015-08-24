package angularBeans.io;
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
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.Part;
import javax.ws.rs.core.MediaType;

import angularBeans.boot.ModuleGenerator;
import angularBeans.context.NGSessionScopeContext;
import angularBeans.log.NGLogger;

/**
 * 
 * This is the main uploadEndPoint for AngularBeans
 * @author Hmidi Bessem
 *
 */

@SuppressWarnings("serial")
@WebServlet(urlPatterns = { "/uploadEndPoint/*" }, asyncSupported = true)
@MultipartConfig()
public class UploadServlet extends HttpServlet {

	@Inject
	FileUploadHandler uploadHandler;

	@Inject
	NGLogger logger;

	@Inject
	ModuleGenerator generator;

	@Inject
	HttpSession httpSession;

	protected void processRequest(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		String sessionID = httpSession.getId();
		// request.getSession().getAttribute(AngularBeansUtil.NG_SESSION_ATTRIBUTE_NAME);

		// if(sessionID==null){
		// request.getSession(true);
		// request.getSession().setAttribute(AngularBeansUtil.NG_SESSION_ATTRIBUTE_NAME,
		// generator.getUID());
		// }
		NGSessionScopeContext.setCurrentContext(sessionID);

		String contextName = request.getContextPath();

		String fullURI = request.getRequestURI();
		String urlPrefix = contextName + "/uploadEndPoint/";

		String param = fullURI.substring(fullURI.indexOf(urlPrefix)
				+ urlPrefix.length() - 1);//

		// String param = request.getQueryString();

		response.setContentType(MediaType.APPLICATION_JSON);
		try (PrintWriter out = response.getWriter()) {

			List<Upload> uploads = new ArrayList<Upload>();

			for (Part part : request.getParts()) {
				Upload event = new Upload(part, param);
				uploads.add(event);
				// fileName = part.getSubmittedFileName();
				// part.write(fileName);
				uploadHandler.handleUploads(uploads, param);

			}

			out.write(" ");
		}

	}

	@Override
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		processRequest(request, response);
	}

	@Override
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		processRequest(request, response);
	}

	/**
	 * Returns a short description of the servlet.
	 *
	 * @return a String containing servlet description
	 */
	@Override
	public String getServletInfo() {
		return "Short description";
	}

}