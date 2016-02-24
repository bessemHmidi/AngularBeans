package angularBeans.boot;

import java.util.HashSet;
import java.util.Set;

import angularBeans.ngservices.NGService;
import angularBeans.util.NGBean;

/**
 * <p>
 * A singleton that holds all the detected components (annotated by @AngularBean, @NGApp, @NGExtension) at deployment time
 * for javascript generation.
 * </p>
 *
 * @author Bessem Hmidi
 * @author Aymen Naili
 * @see angularBeans.context.AngularBeansCDIExtension
 * @see angularBeans.boot.ModuleGenerator
 * @see angularBeans.api.AngularBean
 * @see angularBeans.api.NGApp
 * @see angularBeans.ngservices.NGExtension
 */
public class BeanRegistry {

	private static BeanRegistry instance;
	
	private Set<NGBean> angularBeans;
	private Set<NGService> extentions;
	
	private Class<? extends Object> appClass;

	public static synchronized BeanRegistry getInstance() {
		if(instance == null)
			instance = new BeanRegistry();
		return instance;
	}
	
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
