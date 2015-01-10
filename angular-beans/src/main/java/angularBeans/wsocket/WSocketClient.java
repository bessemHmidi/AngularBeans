package angularBeans.wsocket;

import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.websocket.Session;

import angularBeans.Util;
import angularBeans.context.NGSessionScoped;
import angularBeans.log.NGLogger;
import angularBeans.wsocket.annotations.WSocketReceiveEvent;
import angularBeans.wsocket.annotations.WSocketSessionCloseEvent;
import angularBeans.wsocket.annotations.WSocketSessionReadyEvent;

@NGSessionScoped
public class WSocketClient implements Serializable {

	private Session session;

	private static  Map<String, Session> channelsSubscribers=new HashMap<>();
	
	
	public void onSessionReady(@Observes @WSocketSessionReadyEvent WSocketEvent event) {
    session = event.getSession();
	event.setClient(this);
	
	}

	public void onClose(@Observes @WSocketSessionCloseEvent WSocketEvent event) {

	}

	public void onError(@Observes @WSocketErrorEvent WSocketEvent event) {
		throw new RuntimeException(event.getData().toString());
	}

	public void onSession(@Observes @WSocketReceiveEvent WSocketEvent event) {

		session = event.getSession();
		event.setClient(this);

	}

	@Inject
	NGLogger logger;

	public Session getSession() {
		return session;
	}

	public void publish(String channel, WSocketMessage message) {

		Map<String, Object> paramsToSend = new HashMap<String, Object>(
				message.build());
		paramsToSend.put("reqId", channel);
		 paramsToSend.put("log", logger.getLogPool());
		try {
			session.getBasicRemote().sendText(Util.getJson(paramsToSend));

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	
	
	public void publishToAll(String channel, WSocketMessage message) {
		try {
		Map<String, Object> paramsToSend = new HashMap<String, Object>(
				message.build());
		paramsToSend.put("reqId", channel);
       
		
		if (!(session == null)) {
			
				for (Session sess : session.getOpenSessions()) {
					sess.getBasicRemote().sendText(Util.getJson(paramsToSend));
				}

		}
		} catch (Exception e) {
		
			e.printStackTrace();
		}
	}

	public static synchronized Map<String, Session> getChannelsSubscribers() {
		return channelsSubscribers;
	}

	

}
