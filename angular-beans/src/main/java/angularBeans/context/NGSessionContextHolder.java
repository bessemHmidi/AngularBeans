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
 * declare a "NGSession" beans store for 
 @author Bessem Hmidi
 */
package angularBeans.context;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.Bean;
/**
 * bean store for the NGSessionScopeContext
 * @author bessem
 *
 */
@SuppressWarnings("serial")
public class NGSessionContextHolder implements Serializable {

	// private static NGSessionContextHolder INSTANCE;
	private final Map<Class, NGSessionScopeInstance> beans;// we will have only one
													// instance of a type so the
													// key is a class
 
	public NGSessionContextHolder() {

		beans = Collections
				.synchronizedMap(new HashMap<Class, NGSessionScopeInstance>());
	}


	public Map<Class, NGSessionScopeInstance> getBeans() {
		

		return beans;

	}

	public NGSessionScopeInstance getBean(Class type) {
		return getBeans().get(type);
	}

	public void putBean(NGSessionScopeInstance customInstance) {

		getBeans().put(customInstance.bean.getBeanClass(), customInstance);
	}

	void destroyBean(NGSessionScopeInstance customScopeInstance) {
		getBeans().remove(customScopeInstance.bean.getBeanClass());
		customScopeInstance.bean.destroy(customScopeInstance.instance,
				customScopeInstance.ctx);
	}

	/**
	 * wrap necessary properties so we can destroy the bean later:
	 * 
	 * @see NGSessionContextHolder#destroyBean(angularBeans.context.NGSessionContextHolder.NGSessionScopeInstance)
	 */
	public static class NGSessionScopeInstance<T> {

		Bean<T> bean;
		CreationalContext<T> ctx;
		T instance;
	}

}
