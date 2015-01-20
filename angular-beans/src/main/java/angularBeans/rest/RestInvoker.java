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

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import angularBeans.Util;
import angularBeans.context.BeanLocator;
import angularBeans.remote.RemoteInvoker;

import com.google.gson.JsonObject;

@Path("/invoke")
@ApplicationScoped
public class RestInvoker implements Serializable {

	@Inject
	RemoteInvoker remoteInvoker;

	@Inject
	BeanLocator locator;

	@GET
	@Path("/service/{bean}/{method}/json")
	@Produces(MediaType.APPLICATION_JSON)
	public Object doSersvice(@PathParam("bean") String beanName,
			@PathParam("method") String method,
			@QueryParam("params") String params) {

		
		JsonObject paramsObj =Util.parse(params); 

		String UID = paramsObj.get("sessionUID").getAsString();

		Object o = remoteInvoker.invoke(locator.lookup(beanName, UID), method,
				paramsObj, UID);

		return o;

	}

}
