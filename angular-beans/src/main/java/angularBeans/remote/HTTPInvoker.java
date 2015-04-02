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

import java.io.IOException;
import java.io.Serializable;

import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.security.auth.message.callback.PrivateKeyCallback.Request;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import angularBeans.context.BeanLocator;
import angularBeans.context.NGSessionScopeContext;
import angularBeans.remote.RemoteInvoker;
import angularBeans.util.AngularBeansUtil;

import com.google.gson.JsonObject;

@WebServlet(asyncSupported=false,urlPatterns="/http/invoke/*") 

public class HTTPInvoker extends HttpServlet  implements Serializable{

	@Inject
	RemoteInvoker remoteInvoker;

	@Inject
	BeanLocator locator;

	@Inject
	AngularBeansUtil util;

	
	@Override
	protected void service(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		
		
		String fullPath=req.getRequestURI();
		fullPath=(fullPath.substring(fullPath.indexOf("/service/")+9));
		
		String parts[]=fullPath.split("/");
		
		String bean=parts[0];
		String method=parts[1];
		
		String params=req.getParameter("params");
		
	
		resp.getWriter().write(process(bean, method, params).toString());
		
		
	}
	


	
		private Object process(String beanName, String method, String params) {
		
		
		JsonObject paramsObj =util.parse(params); 

		String UID = paramsObj.get("sessionUID").getAsString();
		NGSessionScopeContext.setCurrentContext(UID);

		Object result = remoteInvoker.invoke(locator.lookup(beanName, UID), method,
				paramsObj, UID);
		
		String jsonResponse=util.getJson(result);
		
		System.out.println(jsonResponse);
		
		return jsonResponse;
	}

}

