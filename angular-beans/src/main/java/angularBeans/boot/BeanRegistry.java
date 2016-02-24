
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
 * The BeanRegistry is used to store CDI beans info detected at deployment
 * time to boost javascript generation performances later on the ModuleGenerator
 * (pre generated and compressed js)
 *<p>
 *it will store specific CDI beans definitions: 
 *'@AngularBeans' (as wrapped NGBean)
 *, angularBeans built-in angularJs services (NGService)
 *, the '@NGApp' definition
 *<p>
 *combined with specific beans dependent javascript part's (related to RPC methods call)
 *will produce the final "angular-beans.js" script.
 * @author bessem hmidi
 */
public enum BeanRegistry {

	INSTANCE;
	
	private Set<NGBean> angularBeans;
	private Set<NGService> extentions;
	
	private Class<? extends Object> appClass;

	private BeanRegistry(){
		if(angularBeans == null) angularBeans = new HashSet<>();
		if(extentions == null) extentions = new HashSet<>();
	}
	
	public void registerApp(Class<? extends Object> appClass) {
		this.appClass = appClass;
	}

	/**
	 * Registers the given @AngularBean for script generation. 
	 * @param targetClass
	 */
	public void registerBean(Class targetClass) {
		angularBeans.add(new NGBean(targetClass));
	}

	public void registerExtention(NGService extention) {
		extentions.add(extention);
	}

	public Set<NGBean> getAngularBeans() {
		return angularBeans;
	}

	public Set<NGService> getExtentions() {
		return extentions;
	}

	public Class<? extends Object> getAppClass() {
		return appClass;
	}

}

