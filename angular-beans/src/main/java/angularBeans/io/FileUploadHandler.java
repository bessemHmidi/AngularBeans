package angularBeans.io;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.inject.Inject;

import angularBeans.context.NGSessionScoped;


@NGSessionScoped
public class FileUploadHandler {

	private Map<String, Call> uploadsActions=new HashMap<String, Call>();
	
	@Inject
	ByteArrayCache cache;
	
	
	public Map<String, Call> getUploadsActions() {
		return uploadsActions;
	}





	public void handleUploads(List<Upload> uploads, String path) {
		
		Call call=uploadsActions.get(path);
		
		
//		System.out.println("--> "+cache.getCache());
//		System.out.println(call);
//		for (String idf : (new HashSet<String>(cache.getCache().keySet()))) {
//			
//			Call ls = cache.getCache().get(idf);
//			if (ls.getObject()==call.getObject()){
//				
//				cache.getCache().remove(idf);
//				String id = String.valueOf(UUID.randomUUID());
//				cache.getCache().put(id, call);
//			
//				System.out.println("found old id");
//				
//				break;
//			}
//		}
		
		
		try {
			call.getMethod().invoke(call.getObject(), uploads);
		} catch (IllegalAccessException | IllegalArgumentException
				| InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	
	
}
