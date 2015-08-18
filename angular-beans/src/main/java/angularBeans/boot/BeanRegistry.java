package angularBeans.boot;

import java.util.HashSet;
import java.util.Set;

import angularBeans.ngservices.NGService;
import angularBeans.util.NGBean;

/**
 * used by:
 * <p>-AngularBeansServletContextListenerAnnotated
 * <p>-ModuleGenerator
 * <p>-AngularBeansCDIExtention
 * <p>
 * The BeanRegistry is used to store CDI beans info detected at deployment
 * time to boost javascript generation performances later on the ModuleGenerator
 * (pre generated and compressed js)
 *<p>
 *it will store specific CDI beans definitions: 
 *@AngularBeans (as wrapped NGBean)
 *, angularBeans built-in angularJs services (NGService)
 *, the @NGApp definition
 *<p>
 *combined with specific beans dependent javascript part's (related to RPC methods call)
 *will produce the final "angular-beans.js" script.
 * @author bessem hmidi
 *
 */


public class BeanRegistry {

	private static BeanRegistry instance = new BeanRegistry();

	private Set<NGBean> angularBeans = new HashSet<>();

	private Set<NGService> extentions = new HashSet<>();

	private Class<? extends Object> appClass;

	/**
	 * AngularBeansCDIExtention wi
	 * @param appClass
	 */
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
