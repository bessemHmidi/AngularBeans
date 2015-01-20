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

import javax.enterprise.event.Observes;
import javax.enterprise.inject.spi.AfterBeanDiscovery;
import javax.enterprise.inject.spi.AnnotatedType;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.BeforeBeanDiscovery;
import javax.enterprise.inject.spi.Extension;
import javax.enterprise.inject.spi.ProcessAnnotatedType;

import angularBeans.api.NGController;

public class NGSessionExtention implements Extension {

	public <T> void processAnnotatedType(
			@Observes ProcessAnnotatedType<T> processAnnotatedType) {

		AnnotatedType<T> annotatedType = processAnnotatedType
				.getAnnotatedType();

		if (annotatedType.isAnnotationPresent(NGController.class)) {
			// System.out.println("---------"+annotatedType.getJavaClass());
		}
		;

	}

	public void addScope(@Observes final BeforeBeanDiscovery event) {
		event.addScope(NGSessionScoped.class, true, false);
	}

	public void registerContext(@Observes final AfterBeanDiscovery event) {

		NGSessionScopeContext context = new NGSessionScopeContext();
		//
		event.addContext(context);
	}

	<T> void pat(@Observes ProcessAnnotatedType<T> event, BeanManager bm) {

	}
}
