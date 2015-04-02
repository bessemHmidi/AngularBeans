package angularBeans.util;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.enterprise.context.SessionScoped;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.Annotated;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.inject.Named;

import angularBeans.context.NGSessionScoped;

@NGSessionScoped
public class ScopeUtils implements Serializable {

	private Map<Class, Scope> allScopes = new HashMap<Class, Scope>();

	RootScope rootScope=new RootScope();
	
	
	
	public Scope get(Class scope) {

		return allScopes.get(scope);

	}

	
//	public ScopeUtil(InjectionPoint ip){
		//
//        Annotated gtAnnotated = ip.getAnnotated();
//        System.out.println(ip);
//        System.out.println(gtAnnotated.getBaseType().getClass());
//		
//        return get(gtAnnotated.getClass());
//
//
//}
	
	
	
//  @Produces
//  public Scope getScope(InjectionPoint ip){
//	 
//	  System.out.println(ip.getAnnotated().getBaseType());
//	  return null;
//  }
	
	public void addScope(Class clazz) {
		allScopes.put(clazz, new ScopeImpl());
	}
	
	

	
	public RootScope getRootScope() {
		// TODO Auto-generated method stub
		return rootScope;
	}

}


 