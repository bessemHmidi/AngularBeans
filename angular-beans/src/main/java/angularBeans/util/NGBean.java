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
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

import angularBeans.api.NGModel;

/**
 * a wrapper for an angularBean CDI bean class to provide utility methods for
 * reflection processing issues
 * 
 * @author Bassem Hmidi
 *
 */

@SuppressWarnings("serial")
public class NGBean implements Serializable {

	private Class<?> targetClass = null;
	private String name = null;

	public NGBean(Class beanclass) {

		targetClass = beanclass;
		scan();
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

	private Set<Method> setters = new HashSet<>();
	private Set<Method> getters = new HashSet<>();
	private Method[] methods;

	public void scan() {
		setName(AngularBeansUtil.getBeanName(targetClass));
		methods = targetClass.getMethods();

		for (Method m : methods) {
			if (AngularBeansUtil.isGetter(m)) {
				if (m.isAnnotationPresent(NGModel.class)) {
					getters.add(m);
				}
			}

		}

	}

	public Method[] getMethods() {
		return methods;
	}

	public Set<Method> getters() {

		return getters;
	}

	@Override
	public int hashCode() {

		return name.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		NGBean other = (NGBean) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (targetClass == null) {
			if (other.targetClass != null)
				return false;
		} else if (!targetClass.equals(other.targetClass))
			return false;
		return true;
	}

}
