package angularBeans.io;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.SessionScoped;
import javax.ws.rs.GET;

@ApplicationScoped
public class ByteArrayCache implements Serializable{

	private Map<String,Call> cache = new HashMap<String, Call>();
	
	
	@PostConstruct
	public void init(){
		
	}


public Map<String, Call> getCache() {
	return cache;
}
}
