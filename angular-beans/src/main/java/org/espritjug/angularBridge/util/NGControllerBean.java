package org.espritjug.angularBridge.util;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.espritjug.angularBridge.Util;

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
	

	setName(Util.getBeanName(targetClass));
	
	
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
