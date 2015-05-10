package angularBeans.demoApp.ngbeans;

import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.ws.rs.Path;

import angularBeans.api.AngularBean;
import angularBeans.context.NGSessionScoped;
import angularBeans.demoApp.domain.NotificationMessage;
import angularBeans.realtime.RealTimeClient;
import angularBeans.realtime.RealTimeMessage;
import angularBeans.util.ModelQueryFactory;

@AngularBean
@NGSessionScoped

public class NotificationsService {

	@Inject
	RealTimeClient client;

	@Inject
	ModelQueryFactory queryFactory;

	public void fireNotification(@Observes NotificationMessage message) {

		RealTimeMessage rtMessage = new RealTimeMessage().set("message",
				message);

		if (message.isBroadcast()) {
			client.broadcast("notificationChannel", rtMessage, false);
		} else {
			client.publish("notificationChannel", rtMessage);
		}

	}

}
