package angularBeans.context;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class SessionMapper {

	private static Map<String, Set<String>> sessionsMap=new HashMap<String, Set<String>>();
	
    public static Map<String, Set<String>> getSessionsMap() {
	return sessionsMap;	
}
	
	public static String getHTTPSessionID(String sockJSSessionID){
		
		for(String httpSession:sessionsMap.keySet()){
			
			if(sessionsMap.get(httpSession).contains(sockJSSessionID))return httpSession;
			
		}
		
		
		return sockJSSessionID;
	}
    
}
