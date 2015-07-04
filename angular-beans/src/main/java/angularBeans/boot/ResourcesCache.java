package angularBeans.boot;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.servlet.ServletContext;

import angularBeans.util.AngularBeansUtil;

@ApplicationScoped
public class ResourcesCache {

	@Inject
	AngularBeansUtil util;
	
	
	private Map<String, String> cache=new HashMap<String, String>();
	
	public String get(String resourceName,ServletContext servletContext) {
		
		String json=null;
		
		if(!cache.containsKey(resourceName)){
		
		InputStream is = servletContext.getResourceAsStream(
				 "/META-INF" +resourceName+ ".properties");
		
		Properties properties = new Properties();

		try {
			properties.load(is);
			
			json=util.getJson(properties);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		}

		return json;
	}

}
