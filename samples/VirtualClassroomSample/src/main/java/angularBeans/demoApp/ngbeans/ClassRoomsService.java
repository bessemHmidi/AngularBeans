package angularBeans.demoApp.ngbeans;

import java.util.HashSet;
import java.util.Set;

import javax.enterprise.event.Event;
import javax.inject.Inject;

import angularBeans.api.AngularBean;
import angularBeans.api.NGModel;
import angularBeans.api.http.Put;
import angularBeans.context.NGSessionScoped;
import angularBeans.demoApp.domain.ClassRoom;
import angularBeans.demoApp.domain.NotificationMessage;
import angularBeans.demoApp.domain.User;
import angularBeans.demoApp.service.VirtualClassService;
import angularBeans.realtime.RealTime;
import angularBeans.realtime.RealTimeClient;
import angularBeans.util.ModelQuery;
import angularBeans.util.ModelQueryFactory;

@NGSessionScoped
@AngularBean
public class ClassRoomsService {

	@Inject
	VirtualClassService virtualClassService;

	@Inject
	Event<NotificationMessage> notificationsBus;

	@Inject
	@AngularBean
	AuthenticationService authenticationService;

	@Inject
	ModelQuery modelQuery;

	@Inject
	ModelQueryFactory modelQueryFactory;

	@Inject
	RealTimeClient client;

	// public void tata(@Observes @DataReceivedEvent DataReceived event) {
	// System.out.println("RECEIVED: " + event.getData());
	//
	// }

	@NGModel
	public Set<ClassRoom> getClassRooms() {
		return virtualClassService.getClassRoomsMap().keySet();

	}

	public Set<User> getUsers(ClassRoom classRoom) {

		return virtualClassService.getClassRoomsMap().get(classRoom);
	}

	// @NGPostConstruct
	public void init() {

		// modelQueryFactory.get(ClassRoomsService.class).setProperty("classRooms",
		// getClassRooms());

	}

	@Put
	public String join(ClassRoom classRoom) {

		// singleClassRoomsCtrl.setActualClassRoom(classRoom);

		User user = authenticationService.getConnectedUser();

		if (!virtualClassService.getClassRoomsMap().get(classRoom)
				.contains(user))

		{
			virtualClassService.getClassRoomsMap().get(classRoom).add(user);

			NotificationMessage notificationMessage = new NotificationMessage(
					"info", "new Member", user.getPseudo()
							+ " has joined the class " + classRoom.getName()
							+ " !", true);
			notificationsBus.fire(notificationMessage);

			ModelQuery query = modelQueryFactory.get(
					SingleClassRoomService.class).pushTo("users", user);

			client.broadcast(query, true);

			// or #1

			// client.broadcast( "joinEvent",
			//
			// new RealTimeMessage()
			// .set("user", user)
			// .set("classRoom", classRoom)
			//
			// ,true);
		}

		return "/classRoom";
	}

	@RealTime
	public void addClassRoom(String classRoomName) {

		ClassRoom classRoom = new ClassRoom();
		classRoom.setName(classRoomName);
		virtualClassService.getClassRoomsMap().put(classRoom,
				new HashSet<User>());

		client.broadcast(modelQuery.pushTo("classRooms", classRoom), false);

	}

}
