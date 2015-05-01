package angularBeans.demoApp.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.ejb.LocalBean;
import javax.ejb.Singleton;
import javax.ws.rs.PUT;

import angularBeans.demoApp.domain.ClassRoom;
import angularBeans.demoApp.domain.User;



@Singleton
@LocalBean
public class VirtualClassService {

	
	private List<User> users=new ArrayList<User>();
	public List<User> getUsers() {
		return users;
	}
	
	
	public void createAccount(User user) {
		users.add(user);
	}
	
	
	private Map<ClassRoom, Set<User>> classRoomsMap = new HashMap<ClassRoom, Set<User>>();
	
	
	public Map<ClassRoom, Set<User>> getClassRoomsMap() {
		return classRoomsMap;
	}
	
}
