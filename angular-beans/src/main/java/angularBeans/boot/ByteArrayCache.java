package angularBeans.boot;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.SessionScoped;
import javax.ws.rs.GET;

import angularBeans.io.LobSource;

@ApplicationScoped
public class ByteArrayCache implements Serializable{

	private Map<String,LobSource> cache = new HashMap<String, LobSource>();
	
	
	@PostConstruct
	public void init(){
		
	}


public Map<String, LobSource> getCache() {
	return cache;
}
}
