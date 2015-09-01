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
package angularBeans.util;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.inject.Named;

import angularBeans.context.NGSessionScoped;

/**
 * Producer for the ModelQuery component
 * 
 * @author Bassem Hmidi
 */

@NGSessionScoped
@Named("ModelQueryFactory")
public class ModelQueryFactory implements Serializable {// PassivationCapable {

	private static final long serialVersionUID = 1L;

	private Map<Class, ModelQuery> allQueries = new HashMap<Class, ModelQuery>();

	RootScope rootScope = new RootScopeImpl();

	public ModelQuery get(Class clazz) {
		if (allQueries.get(clazz) == null)
			addQuery(clazz);
		ModelQueryImpl query = (ModelQueryImpl) allQueries.get(clazz);
		query.setOwner(clazz);
		return query;

	}

	@Produces
	public ModelQuery getModelQuery(InjectionPoint injectionPoint) {

		ModelQuery query = get(injectionPoint.getMember().getDeclaringClass());

		return query;

	}

	public void addQuery(Class clazz) {

		allQueries.put(clazz, new ModelQueryImpl());
	}

	@Produces
	public RootScopeImpl getRootScope() {
		return (RootScopeImpl) rootScope;
	}
}
