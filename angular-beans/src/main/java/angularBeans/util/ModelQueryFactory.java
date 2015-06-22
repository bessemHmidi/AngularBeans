package angularBeans.util;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.Dependent;
import javax.enterprise.context.SessionScoped;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.inject.Named;
import javax.inject.Singleton;

import angularBeans.context.NGSessionScoped;


@NGSessionScoped
@Named("ModelQueryFactory")
public class ModelQueryFactory implements Serializable {// PassivationCapable {

	/**
	 * 
	 */
	
	private static final long serialVersionUID = 1L;

	private Map<Class, ModelQuery> allQueries = new HashMap<Class, ModelQuery>();

	RootScope rootScope = new RootScope();

	public ModelQuery get(Class clazz) {

		if (allQueries.get(clazz) == null)
			addQuery(clazz);
		ModelQueryImpl query = (ModelQueryImpl) allQueries.get(clazz);
		query.setOwner(clazz);
		return query;

	}

	@Produces
	public ModelQuery getModelQuery(InjectionPoint injectionPoint) {

		return get(injectionPoint.getMember().getDeclaringClass());

	}

	public void addQuery(Class clazz) {

		allQueries.put(clazz, new ModelQueryImpl());
	}

	@Produces
	public RootScope getRootScope() {
		return rootScope;
	}
}
