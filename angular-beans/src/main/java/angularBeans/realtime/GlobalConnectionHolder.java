package angularBeans.realtime;

import java.util.HashSet;
import java.util.Set;

import javax.enterprise.context.ApplicationScoped;

import org.projectodd.sockjs.SockJsConnection;

@ApplicationScoped
public class GlobalConnectionHolder {
	
	
	Set <SockJsConnection> allConnections =new HashSet<SockJsConnection>();
	
	
	public Set<SockJsConnection> getAllConnections() {
		return allConnections;
	}

	

}
