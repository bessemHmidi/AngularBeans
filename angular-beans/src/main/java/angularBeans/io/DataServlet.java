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


package angularBeans.io;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import javax.inject.Inject;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * This servlet is the AngularBeans Binary end point
 * 
 * that let the usage of a byte[] resource as an angular 
 * js model. (used in ng-src="{{myBinaryModel}}" for example)
 *  
 * @author Bessem Hmidi
 *
 */


@SuppressWarnings("serial")
@WebServlet(urlPatterns = "/lob/*")
public class DataServlet extends HttpServlet {

	@Inject
	private ByteArrayCache cache;

	public DataServlet() {

	}

	protected void doGet(HttpServletRequest req, HttpServletResponse response) {

		String requestURI = req.getRequestURI();

		int index = (requestURI.indexOf("/lob")) + 5;
		String resourceId = requestURI.substring(index);

		response.setHeader("Access-Control-Allow-Origin", "*");

		byte[] data = null;
	
		try (OutputStream o=response.getOutputStream();){
			if (cache.getCache().containsKey(resourceId)) {
				Call call = cache.getCache().get(resourceId);
				Method m = call.getMethod();
				Object container = call.getObject();

				Object result = m.invoke(container);

				if (result != null) {
					data = ((LobWrapper) result).getData();

				}

			} else {
				if (cache.getTempCache().containsKey(resourceId)) {
					data = cache.getTempCache().get(resourceId);
					cache.getTempCache().remove(resourceId);
				}
			}

			
			if (data == null) {
				data = "default".getBytes();
			}
			o.write(data);
			o.flush();
			o.close();

		} catch (IOException e) {

			e.printStackTrace();
		} catch (IllegalAccessException e) {

			e.printStackTrace();
		} catch (IllegalArgumentException e) {

			e.printStackTrace();
		} catch (InvocationTargetException e) {

			e.printStackTrace();
		}

	}

}
