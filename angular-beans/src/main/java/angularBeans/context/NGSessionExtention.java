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

import java.lang.annotation.Annotation;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.context.SessionScoped;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.spi.AfterBeanDiscovery;
import javax.enterprise.inject.spi.AnnotatedType;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.BeforeBeanDiscovery;
import javax.enterprise.inject.spi.Extension;
import javax.enterprise.inject.spi.ProcessAnnotatedType;

import angularBeans.api.AngularBean;

public class NGSessionExtention implements Extension {

//	public <T> void processAnnotatedType(
//			@Observes ProcessAnnotatedType<T> processAnnotatedType) {
//
//		AnnotatedType<T> annotatedType = processAnnotatedType
//				.getAnnotatedType();
//
////		if (annotatedType.isAnnotationPresent(AngularBean.class)) {
////			System.out.println("------------PROCESSING");
////			
////			Class clazz=annotatedType.getJavaClass();
////			
////			if
////			(
////			(!clazz.isAnnotationPresent(RequestScoped.class))
////			&&
////			(!clazz.isAnnotationPresent(NGSessionScoped.class))
////			&&(!clazz.isAnnotationPresent(SessionScoped.class))
////			&&(!clazz.isAnnotationPresent(ApplicationScoped.class))
////			
////					)		
////					{
////				
////				Annotation requestScopedAnnotation = new Annotation() {
////			        @Override
////			        public Class<? extends Annotation> annotationType() {
////			          return RequestScoped.class;
////			        }
////			      };
////
////			      AnnotatedTypeWrapper<T> wrapper = new AnnotatedTypeWrapper<T>(
////			          annotatedType, annotatedType.getAnnotations());
////			      wrapper.addAnnotation(requestScopedAnnotation);
////
////			      processAnnotatedType.setAnnotatedType(wrapper);
////				
////				
////					}
////	
////		}
////		;
//
//	}

	
//	public void addScopes(@Observes  final BeforeBeanDiscovery event) {
//
//		event.addScope(NGSessionScoped.class, true, false);
//	}

	public void registerContext(@Observes final AfterBeanDiscovery event,BeanManager manager) {

		NGSessionScopeContext context = new NGSessionScopeContext();
		//
		event.addContext(context);
		
	}

//	<T> void pat(@Observes ProcessAnnotatedType<T> event, BeanManager bm) {
//
//	}
}
