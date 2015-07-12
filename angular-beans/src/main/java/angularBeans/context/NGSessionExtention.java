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

import java.io.IOException;

import javax.enterprise.context.spi.Context;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.spi.AfterBeanDiscovery;
import javax.enterprise.inject.spi.AnnotatedType;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.BeforeBeanDiscovery;
import javax.enterprise.inject.spi.Extension;
import javax.enterprise.inject.spi.ProcessAnnotatedType;

import angularBeans.api.AngularBean;
import angularBeans.api.NGApp;
import angularBeans.boot.BeanRegistry;
import angularBeans.ngservices.NGExtention;
import angularBeans.ngservices.NGService;

public class NGSessionExtention implements Extension {

	public <T> void processAnnotatedType(
			@Observes ProcessAnnotatedType<T> processAnnotatedType) {

		AnnotatedType<T> annotatedType = processAnnotatedType
				.getAnnotatedType();

		if (annotatedType.isAnnotationPresent(AngularBean.class)) {
			{
				BeanRegistry.getInstance().registerBean(
						annotatedType.getJavaClass());
			}
			;

		}

		if (annotatedType.isAnnotationPresent(NGExtention.class)) {
			{
				try {

					BeanRegistry.getInstance().registerExtention(
							(NGService) annotatedType.getJavaClass()
									.newInstance());
				} catch (InstantiationException | IllegalAccessException e) {
				
					e.printStackTrace();
				}
			}
			;

		}

		if (annotatedType.isAnnotationPresent(NGApp.class)) {

			BeanRegistry.getInstance()
					.registerApp(annotatedType.getJavaClass());

		}

	}

	public void addScopes(@Observes final BeforeBeanDiscovery event) {

		// event.addScope(NGSessionScoped.class, false, false);
	}

	public void registerContext(@Observes final AfterBeanDiscovery event,
			BeanManager manager) {

		Context context = NGSessionScopeContext.getINSTANCE();
		//
		event.addContext(context);
		
		
//		try {
//			Process process=new ProcessBuilder("mkdir","toto").start();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}

	}

}
