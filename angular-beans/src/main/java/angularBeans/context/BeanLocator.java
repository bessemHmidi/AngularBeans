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
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
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

import angularBeans.util.AngularBeansUtil;


@ApplicationScoped
public class BeanLocator implements Serializable {

	@Inject
	private BeanManager beanManager;

	@Inject
	AngularBeansUtil util;
	
	public BeanLocator() {
		// TODO Auto-generated constructor stub
	}

	
	
//	public synchronized Object lookupHybrid(Class beanClass,String UID){
//		
//		if(!hybrideBeans.containsKey(UID)){
//			hybrideBeans.put(UID, new HashMap<Class, Object>());
//		}
//		
//		
//		if(!hybrideBeans.get(UID).containsKey(beanClass)){
//			
//			try {
//				registerHybridBean(beanClass.newInstance(), UID);
//			
//			
//			
//			} catch (InstantiationException | IllegalAccessException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//		}
//		return hybrideBeans.get(UID).get(beanClass);
//		
//	}
//	
//	private Map<String,Map<Class, Object>> hybrideBeans=new HashMap<String, Map<Class,Object>>();
//	
//	public void registerHybridBean(Object Bean,String UID){
//		
//	}
	
	public synchronized Object lookup(String beanName, String UID) {

	
		NGSessionScopeContext.setCurrentContext(UID);
		Object reference = null;

		Set<Bean<?>> beans = beanManager.getBeans(beanName);

		Class beanClass = util.beanNamesHolder.get(beanName);
		if (beans.size() == 0) {
			beans = beanManager.getBeans(beanClass,
					new AnnotationLiteral<Any>() {
					});
		}

		Bean bean = (Bean) beanManager.resolve(beans);

		Context context = null;

		Class scopeAnnotationClass = bean.getScope();
		
	
		if(scopeAnnotationClass.equals(RequestScoped.class)){
	 return bean.create(beanManager.createCreationalContext(bean));

		}
	
		context = beanManager.getContext(scopeAnnotationClass);
		CreationalContext	creationalContext =beanManager.createCreationalContext(bean);
		reference = context
				.get(bean, creationalContext);

		return reference;
	}

}
