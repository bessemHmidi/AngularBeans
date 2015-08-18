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
import java.lang.annotation.Annotation;

import javax.enterprise.context.spi.Context;
import javax.enterprise.context.spi.Contextual;
import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.Bean;

import angularBeans.context.NGSessionContextHolder.NGSessionScopeInstance;

/**
 * 
 * @author Bessem Hmidi
 * A custom CDI context implementation bound to the NGSession (cross context between the Websockets session and HTTP Session)
 */
@SuppressWarnings("serial")
public class NGSessionScopeContext implements Context, Serializable {

	private static Context INSTANCE;

	public NGSessionScopeContext() {
		INSTANCE = this;
	}

	public static Context getINSTANCE() {
		if (INSTANCE == null)
			INSTANCE = new NGSessionScopeContext();
		return INSTANCE;
	}

	private static ThreadLocal<NGSessionContextHolder> holder = new ThreadLocal<>();

	public static void setCurrentContext(String sessionId) {

		NGSessionContextHolder selectedHolder = GlobalNGSessionContextsMapHolder
				.get(sessionId);

		holder.set(selectedHolder);

	}

	@Override
	public Class<? extends Annotation> getScope() {

		return NGSessionScoped.class;
	}

	@Override
	public <T> T get(Contextual<T> contextual,
			CreationalContext<T> creationalContext) {
		if (holder.get() == null)
			return null;

		Bean bean = (Bean) contextual;
		if (holder.get().getBeans().containsKey(bean.getBeanClass())) {
			return (T) holder.get().getBean(bean.getBeanClass()).instance;
		} else {

			T instance = (T) bean.create(creationalContext);
			NGSessionScopeInstance customInstance = new NGSessionScopeInstance();
			customInstance.bean = bean;
			customInstance.ctx = creationalContext;
			customInstance.instance = instance;
			holder.get().putBean(customInstance);
			return instance;
		}

	}

	@Override
	public <T> T get(Contextual<T> contextual) {

		Bean bean = (Bean) contextual;

		if (holder.get().getBeans().containsKey(bean.getBeanClass())) {
			return (T) holder.get().getBean(bean.getBeanClass()).instance;
		} else {
			return null;
		}
	}

	@Override
	public boolean isActive() {

		return true;
	}

}
