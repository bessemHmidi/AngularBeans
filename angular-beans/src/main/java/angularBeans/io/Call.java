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

import java.lang.reflect.Method;
/**
 * 
 * Wrapper for a method invocation
 * stored in a cache 
 * - ByteArrayCache <p>
 * - FileUploadHandler<p>
 * invoked when the DataServlet is requested.
 * that help to get a binary content in a lazy loading 
 * manner.
 * <p>
 * also used by the FileUploadHandler to map actions with upload url's
 * @author Bessem Hmidi
 *
 */
public class Call {

	
	private Object object;
	private Method method;
	
	
	public Call(Object object, Method method) {
		this.object=object;
		this.method=method;
	}
	public Object getObject() {
		return object;
	}
	public void setObject(Object object) {
		this.object = object;
	}
	public Method getMethod() {
		return method;
	}
	public void setMethod(Method method) {
		this.method = method;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((method == null) ? 0 : method.hashCode());
		result = prime * result + ((object == null) ? 0 : object.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Call other = (Call) obj;
		if (method == null) {
			if (other.method != null)
				return false;
		} else if (!method.equals(other.method))
			return false;
		if (object == null) {
			if (other.object != null)
				return false;
		} else if (!(object==(other.object)))
			return false;
		return true;
	}
	
	
	
	
}
