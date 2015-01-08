package org.espritjug.angularBridge.context;

import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.util.logging.Logger;

import javax.enterprise.context.spi.Context;
import javax.enterprise.context.spi.Contextual;
import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.Bean;

import org.espritjug.angularBridge.context.NGSessionContextHolder.CustomScopeInstance;

public class NGSessionScopeContext implements Context, Serializable {

	private static ThreadLocal<NGSessionContextHolder> holder=new ThreadLocal<>();

	public static void setCurrentContext(String holderId) {

		
		
		holder.set(GlobalMapHolder.get(holderId));

	}

	private Logger log = Logger.getLogger(getClass().getSimpleName());

	public NGSessionScopeContext() {

	}

	@Override
	public Class<? extends Annotation> getScope() {
		// TODO Auto-generated method stub
		return NGSessionScoped.class;
	}

	@Override
	public <T> T get(Contextual<T> contextual,
			CreationalContext<T> creationalContext) {
		if (holder.get()==null)return null;

		Bean bean = (Bean) contextual;
		if (holder.get().getBeans().containsKey(bean.getBeanClass())) {
			return (T) holder.get().getBean(bean.getBeanClass()).instance;
		} else {
			T t = (T) bean.create(creationalContext);
			CustomScopeInstance customInstance = new CustomScopeInstance();
			customInstance.bean = bean;
			customInstance.ctx = creationalContext;
			customInstance.instance = t;
			holder.get().putBean(customInstance);
			return t;
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
		// TODO Auto-generated method stub
		return true;
	}

}
