package angularBeans.demoApp.ngbeans;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;

import angularBeans.api.AngularBean;
import angularBeans.api.NGModel;
import angularBeans.api.NGPostConstruct;
import angularBeans.context.NGSessionScoped;
import angularBeans.demoApp.domain.ClassRoom;
import angularBeans.demoApp.domain.NotificationMessage;
import angularBeans.demoApp.domain.User;
import angularBeans.demoApp.service.VirtualClassService;
import angularBeans.realtime.RealTime;
import angularBeans.realtime.RealTimeClient;
import angularBeans.realtime.RealTimeMessage;
import angularBeans.remote.DataReceived;
import angularBeans.remote.DataReceivedEvent;
import angularBeans.util.ModelQuery;
import angularBeans.util.ModelQueryFactory;

@NGSessionScoped
@AngularBean
public class ClassRoomsService implements Serializable {

	@Inject
	VirtualClassService virtualClassService;

	@Inject
	Event<NotificationMessage> notificationsBus;


	@Inject
	@AngularBean
	AuthenticationService authenticationService;

	@Inject
	ModelQueryFactory modelQueryFactory;

	@Inject
	RealTimeClient client;

//	public void tata(@Observes @DataReceivedEvent DataReceived event) {
//		System.out.println("RECEIVED: " + event.getData());
//
//	}

	@NGModel
	public Set<ClassRoom> getClassRooms() {
		return virtualClassService.getClassRoomsMap().keySet();

	}

	
	public Set<User> getUsers(ClassRoom classRoom) {

		return virtualClassService.getClassRoomsMap().get(classRoom);
	}

	@NGPostConstruct
	public void init() {

		
//		modelQueryFactory.get(ClassRoomsService.class).setProperty("classRooms",
//				getClassRooms());

	}

	@PUT
	public String join(ClassRoom classRoom) {
		
		//singleClassRoomsCtrl.setActualClassRoom(classRoom);

		User user=authenticationService.getConnectedUser();
		
		virtualClassService.getClassRoomsMap().get(classRoom).add(user);

		NotificationMessage notificationMessage = new NotificationMessage(
				"info", "new Member",
				user.getPseudo() + " has joined the class "
						+ classRoom.getName() + " !", true);
		
		notificationsBus.fire(notificationMessage); 

 ModelQuery query=modelQueryFactory.get(SingleClassRoomService.class).pushTo("users", user);
	
 	
 	
 client.broadcast(query, true);
 	
 // or #1
 
//		client.broadcast( "joinEvent",
//				
//				new RealTimeMessage()
//				.set("user", user)
//				.set("classRoom", classRoom)
//				
//				,true);

		return "/classRoom";
	}

	@RealTime
	public void addClassRoom(String classRoomName) {

		ClassRoom classRoom = new ClassRoom();
		classRoom.setName(classRoomName);
		virtualClassService.getClassRoomsMap().put(classRoom,
				new HashSet<User>());

		client.broadcast(modelQueryFactory.get(ClassRoomsService.class).pushTo("classRooms",classRoom),false);

	
		
	}

}
