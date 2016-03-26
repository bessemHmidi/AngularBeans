
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

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.enterprise.context.spi.Context;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.spi.AfterBeanDiscovery;
import javax.enterprise.inject.spi.AnnotatedType;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.Extension;
import javax.enterprise.inject.spi.ProcessAnnotatedType;
import javax.enterprise.inject.spi.WithAnnotations;

import angularBeans.api.AngularBean;
import angularBeans.api.NGApp;
import angularBeans.boot.BeanRegistry;
import angularBeans.ngservices.NGExtension;
import angularBeans.ngservices.NGService;

/**
 * <p>
 * Scans and registers all components annotated with @AngluarBeans, @NGExtension and @NGApp
 * during application deployment.
 * </p>
 * 
 * @see javax.enterprise.inject.spi.Extension
 * @see <a href="https://docs.jboss.org/weld/reference/latest/en-US/html/extend.html">https://docs.jboss.org/weld/reference/latest/en-US/html/extend.html</a>
 * @author Bessem Hmidi
 * @author Aymen Naili
*/
public class AngularBeansCDIExtension implements Extension {

	/**
	 * Observes the ProcessAnnotatedType event and register scanned angularBeans specific
	 * CDI beans to the BeanRegistry.
	 * 
	 * @see BeanRegistry
	 * @param processAnnotatedType
	 */
	public <T> void processAnnotatedType(
			@Observes
			@WithAnnotations(value = { AngularBean.class, NGExtension.class, NGApp.class })
			ProcessAnnotatedType<T> processAnnotatedType) {

		AnnotatedType<T> annotatedType = processAnnotatedType.getAnnotatedType();
		Class<T> typeClass = annotatedType.getJavaClass();
		
		Logger.getLogger(this.getClass().getName()).log(Level.INFO, "Registering "+annotatedType.getJavaClass().getName());
		
		//Handle @AngluarBeans annotated components
		if (annotatedType.isAnnotationPresent(AngularBean.class)){
			BeanRegistry.INSTANCE.registerBean(typeClass);
			return;
		}

		//Handle @NGExtension annotated components
		if (annotatedType.isAnnotationPresent(NGExtension.class)){
			try {
				BeanRegistry.INSTANCE.registerExtention(
						(NGService) annotatedType.getJavaClass().newInstance());
				return;
			} catch (InstantiationException | IllegalAccessException e) {
				e.printStackTrace();
			}
		}

		//Handle @NGApp annotated components
		if (annotatedType.isAnnotationPresent(NGApp.class)) {
			BeanRegistry.INSTANCE.registerApp(typeClass);
		}

	}

	/**
	 * <p>
	 * Invoked by the container once all the annotated types has bean discovered, then registers
	 * the NGSessionScopeContext (and the NGSessionScoped custom CDI scope)
	 * </p>
	 * 
	 * @see javax.enterprise.inject.spi.AfterBeanDiscovery
	 * @see javax.enterprise.inject.spi.BeanManager
	 * @see angularBeans.context.NGSessionScoped
	 * @see angularBeans.context.NGSessionScopeContext
	 */
	public void registerContext(@Observes final AfterBeanDiscovery event, BeanManager manager) {
		Context context = NGSessionScopeContext.getINSTANCE();
		event.addContext(context);
	}

}
