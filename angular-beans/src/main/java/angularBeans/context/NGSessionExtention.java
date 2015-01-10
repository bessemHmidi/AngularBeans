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
