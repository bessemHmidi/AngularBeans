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
package angularBeans.rest;

import java.io.Serializable;

import javax.inject.Inject;
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
import angularBeans.remote.RemoteInvoker;
import angularBeans.util.AngularBeansUtil;

import com.google.gson.JsonObject;

@Path("/invoke")
public class RestInvoker implements Serializable {

	@Inject
	RemoteInvoker remoteInvoker;

	@Inject
	BeanLocator locator;

	@Inject
	AngularBeansUtil util;

	@GET
	@Path("/service/{bean}/{method}/json")
	@Produces(MediaType.APPLICATION_JSON)
	public Object doServiceGet(@PathParam("bean") String beanName,
			@PathParam("method") String method,
			@QueryParam("params") String params) {

		return process(beanName, method, params);

	}

	
	@POST
	@Path("/service/{bean}/{method}/json")
	@Produces(MediaType.APPLICATION_JSON)
	
	public Object doServicePost(@PathParam("bean") String beanName,
			@PathParam("method") String method,
			 String params) {

		return process(beanName, method, params);

	}
	
	
	@PUT
	@Path("/service/{bean}/{method}/json")
	@Produces(MediaType.APPLICATION_JSON)
	public Object doServicePut(@PathParam("bean") String beanName,
			@PathParam("method") String method,
			@QueryParam("params") String params) {

		return process(beanName, method, params);

	}
	
	@DELETE
	@Path("/service/{bean}/{method}/json")
	@Produces(MediaType.APPLICATION_JSON)
	public Object doServiceDelete(@PathParam("bean") String beanName,
			@PathParam("method") String method,
			@QueryParam("params") String params) {

		return process(beanName, method, params);

	}
	

	
	private Object process(String beanName, String method, String params) {
		JsonObject paramsObj =util.parse(params); 

		String UID = paramsObj.get("sessionUID").getAsString();

		Object result = remoteInvoker.invoke(locator.lookup(beanName, UID), method,
				paramsObj, UID);
		
		
		return result;
	}

}

