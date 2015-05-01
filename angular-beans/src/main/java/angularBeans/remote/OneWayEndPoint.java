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
package angularBeans.remote;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Serializable;

import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import angularBeans.context.BeanLocator;
import angularBeans.context.NGSessionScopeContext;
import angularBeans.util.AngularBeansUtil;

import com.google.gson.JsonObject;

@WebServlet(asyncSupported = false, urlPatterns = "/http/invoke/*")
public class OneWayEndPoint extends HttpServlet implements Serializable {

	@Inject
	InvocationHandler remoteInvoker;

	@Inject
	BeanLocator locator;

	@Inject
	AngularBeansUtil util;

	@Inject
	@DataReceivedEvent
	private Event<DataReceived> receiveEvents;

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		resp.getWriter().write(process(req).toString());
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		resp.getWriter().write(process(req).toString());
	}

	@Override
	protected void doPut(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		resp.getWriter().write(process(req).toString());
	}

	@Override
	protected void doDelete(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		resp.getWriter().write(process(req).toString());
	}

	private Object process(HttpServletRequest request) {

		String fullPath = request.getRequestURI();
		fullPath = (fullPath.substring(fullPath.indexOf("/service/") + 9));

		String parts[] = fullPath.split("/");

		String beanName = parts[0];
		String method = parts[1];

		String params = request.getParameter("params");

		if (request.getMethod().equals("POST")) {
			try {
				StringBuilder buffer = new StringBuilder();
				BufferedReader reader;

				reader = request.getReader();

				String line;

				while ((line = reader.readLine()) != null) {
					buffer.append(line);
				}

				params = buffer.toString();
				// System.out.println(params);
			} catch (Exception e) {
				// TODO: handle exception
			}
		}

		JsonObject paramsObj = util.parse(params);

		String UID = paramsObj.get("sessionUID").getAsString();
		NGSessionScopeContext.setCurrentContext(UID);

		receiveEvents.fire(new OneWayDataReceivedEvent(paramsObj));

		Object result = remoteInvoker.invoke(locator.lookup(beanName, UID),
				method, paramsObj, UID);

		String jsonResponse = util.getJson(result);

		// System.out.println(jsonResponse);

		return jsonResponse;
	}

}
