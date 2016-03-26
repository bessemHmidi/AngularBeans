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

package angularBeans.context;

import java.io.Serializable;
import java.util.Set;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.context.spi.Context;
import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.Any;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.util.AnnotationLiteral;
import javax.inject.Inject;

import angularBeans.util.AngularBeansUtils;
import angularBeans.util.CommonUtils;

/**
 * provide a lookup method to obtain an angularBean reference from an external
 * context to the HTTP Session context (useful with realTime methods calls)
 * 
 * @author Bessem Hmidi
 */
@SuppressWarnings("serial")
@ApplicationScoped
public class BeanLocator implements Serializable {

	@Inject
	private BeanManager beanManager;

	@Inject
	AngularBeansUtils util;

	public  Object lookup(String beanName, String sessionID) {

		NGSessionScopeContext.setCurrentContext(sessionID);

		Set<Bean<?>> beans = beanManager.getBeans(beanName);

		Class beanClass = CommonUtils.beanNamesHolder.get(beanName);
		if (beans.isEmpty()) {
			beans = beanManager.getBeans(beanClass,
					new AnnotationLiteral<Any>() {
					});
		}

		Bean bean = (Bean) beanManager.resolve(beans);


		Class scopeAnnotationClass = bean.getScope();
		Context context;

		if (scopeAnnotationClass.equals(RequestScoped.class)) {
			context = beanManager.getContext(scopeAnnotationClass);
			if (context==null)
		return bean.create(beanManager.createCreationalContext(bean));

		} else {

			if (scopeAnnotationClass.equals(NGSessionScopeContext.class)) {
				context = NGSessionScopeContext.getINSTANCE();
			} else {
				context = beanManager.getContext(scopeAnnotationClass);
			}

		}
		CreationalContext creationalContext = beanManager
				.createCreationalContext(bean);
		Object reference = context.get(bean, creationalContext);

		
//		if(reference==null && scopeAnnotationClass.equals(RequestScoped.class)){
//			reference= bean.create(beanManager.createCreationalContext(bean));
//		}
		
		return reference;
	}

}
