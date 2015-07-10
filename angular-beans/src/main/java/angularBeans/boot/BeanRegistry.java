package angularBeans.boot;

import java.util.HashSet;
import java.util.Set;

import angularBeans.ngservices.NGService;
import angularBeans.util.NGBean;

public class BeanRegistry {

	private static BeanRegistry instance = new BeanRegistry();

	private Set<NGBean> angularBeans = new HashSet<>();

	private Set<NGService> extentions = new HashSet<>();

	private Class<? extends Object> appClass;

	public void registerApp(Class appClass) {
		this.appClass = appClass;

	}

	public void registerBean(Class targetClass) {
		angularBeans.add(new NGBean(targetClass));

	}

	public void registerExtention(NGService extention) {

		extentions.add(extention);
	}

	public Set<NGBean> getAngularBeans() {
		return angularBeans;
	}

	public static synchronized BeanRegistry getInstance() {

		return instance;
	}

	public Set<NGService> getExtentions() {
		
		return extentions;
	}

	public Class<? extends Object> getAppClass() {
		
		return appClass;
	}

}
