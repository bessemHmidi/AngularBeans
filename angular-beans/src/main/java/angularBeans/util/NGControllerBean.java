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
package angularBeans.util;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class NGControllerBean implements Serializable{

	Class<? extends Object> proxyClass;
	private Class<?> targetClass = null;
	private String name=null;
	
	private Object controller;
	public NGControllerBean(Object controller) {
		
	this.controller=controller;
	proxyClass=controller.getClass();
	
	Method m;
	//Method m2;
	Object targetInstance=null;
	try {
		m = proxyClass.getMethod("getTargetClass");
		
		targetClass=(Class) m.invoke(controller);

//		m2 = proxyClass.getMethod("getTargetInstance");
//		targetInstance=m.invoke(controller);
	} catch (NoSuchMethodException e1) {
		// TODO Auto-generated catch block
		e1.printStackTrace();
	} catch (SecurityException e1) {
		// TODO Auto-generated catch block
		e1.printStackTrace();
	} catch (IllegalAccessException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} catch (IllegalArgumentException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} catch (InvocationTargetException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	

	setName(AngularBeansUtil.getBeanName(targetClass));
	
	
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;

	}
	
	public Class getTargetClass() {
		return targetClass;
	}
	
	
	
}
