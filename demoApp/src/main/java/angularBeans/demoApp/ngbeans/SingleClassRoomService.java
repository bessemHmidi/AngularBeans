package angularBeans.demoApp.ngbeans;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.POST;

import angularBeans.api.AngularBean;
import angularBeans.api.NGPostConstruct;
import angularBeans.api.NGReturn;
import angularBeans.context.NGSessionScoped;
import angularBeans.demoApp.domain.ClassRoom;
import angularBeans.demoApp.domain.User;
import angularBeans.demoApp.service.VirtualClassService;
import angularBeans.realtime.RealTimeClient;
import angularBeans.realtime.RealTimeMessage;
import angularBeans.util.ModelQuery;
import angularBeans.util.ModelQueryFactory;

@NGSessionScoped
@AngularBean
public class SingleClassRoomService implements Serializable {

	@Inject
	ModelQuery modelQuery;

	@Inject
	@AngularBean
	ClassRoomsService classRoomsService;

	@Inject
	VirtualClassService virtualClassService;

	@Inject
	RealTimeClient client;

	@Inject
	@AngularBean
	AuthenticationService authenticationService;

	//private ClassRoom actualClassRoom;

	@NGPostConstruct
	public void init() {

//		ModelQuery scope = modelQueryFactory.get(SingleClassRoomService.class);
//		scope.setProperty("users", classRoomsCtrl.getUsers(actualClassRoom));
//		scope.setProperty("actualClassRoom", getActualClassRoom());
	}

	
	@GET
	public Set<User> getUsers(String classRoomName){
		
		ClassRoom room=new ClassRoom();
		room.setName(classRoomName);
		
		return classRoomsService.getUsers(room);
	}
	
	@POST
	public String leave(ClassRoom classRoom) {
		User user = authenticationService.getConnectedUser();

		ModelQuery query=  modelQuery.removeFrom("users", user);
		
		client.broadcast(query,false);
		
		virtualClassService.getClassRoomsMap().get(classRoom)
				.remove(user);

		return "/listClassRooms";
	}

//	public ClassRoom getActualClassRoom() {
//		return actualClassRoom;
//	}
//
//	public void setActualClassRoom(ClassRoom actualClassRoom) {
//		this.actualClassRoom = actualClassRoom;
//	}

}
