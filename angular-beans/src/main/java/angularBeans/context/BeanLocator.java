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
package angularBeans.context;

import java.io.Serializable;
import java.util.Set;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.spi.Context;
import javax.enterprise.inject.Any;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.util.AnnotationLiteral;
import javax.inject.Inject;

import angularBeans.Util;


@ApplicationScoped
public class BeanLocator implements Serializable {

	@Inject
	private BeanManager beanManager;

	
	public BeanLocator() {
		// TODO Auto-generated constructor stub
	}

	public synchronized Object lookup(String beanName, String UID) {

		
		//AbstractThreadLocalMapContext contextMap=(AbstractThreadLocalMapContext)beanManager;
		
		NGSessionScopeContext.setCurrentContext(UID);
		Object reference = null;

		Set<Bean<?>> beans = beanManager.getBeans(beanName);

		if (beans.size() == 0) {
			Class beanClass = Util.beanNamesHolder.get(beanName);
			beans = beanManager.getBeans(beanClass,
					new AnnotationLiteral<Any>() {
					});
		}

		Bean bean = (Bean) beanManager.resolve(beans);

		Class scopeAnnotationClass = bean.getScope();

		Context context = null;

		context = beanManager.getContext(scopeAnnotationClass);

		reference = context
				.get(bean, beanManager.createCreationalContext(bean));

		return reference;
	}

}
