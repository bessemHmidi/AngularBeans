package angularBeans.io;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import angularBeans.context.NGSessionScoped;


@NGSessionScoped
public class FileUploadHandler {

	private Map<String, Call> uploadsActions=new HashMap<String, Call>();
	
	
	
	
	
	public Map<String, Call> getUploadsActions() {
		return uploadsActions;
	}





	public void handleUploads(List<Upload> uploads, String path) {
		
		Call call=uploadsActions.get(path);
		
		try {
			call.getMethod().invoke(call.getObject(), uploads);
		} catch (IllegalAccessException | IllegalArgumentException
				| InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	
	
}
