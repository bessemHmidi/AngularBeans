package org.espritjug.angularBeans.wsocket;

import java.io.Serializable;

import javax.annotation.PostConstruct;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.websocket.EndpointConfig;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import org.espritjug.angularBeans.Util;
import org.espritjug.angularBeans.context.NGSessionScopeContext;
import org.espritjug.angularBeans.wsocket.annotations.WSocketReceiveEvent;
import org.espritjug.angularBeans.wsocket.annotations.WSocketSessionCloseEvent;
import org.espritjug.angularBeans.wsocket.annotations.WSocketSessionReadyEvent;

import com.google.gson.JsonObject;

@ServerEndpoint(value = "/ws-service", configurator = GetHttpSessionConfigurator.class)
public class WSocketServcieEndPoint implements Serializable {

	@Inject
	@WSocketReceiveEvent
	private Event<WSocketEvent> receiveEvents;

	@Inject
	@WSocketSessionReadyEvent
	private Event<WSocketEvent> sessionOpenEvent;

	@Inject
	@WSocketSessionCloseEvent
	private Event<WSocketEvent> sessionCloseEvent;

	@Inject
	@WSocketErrorEvent
	private Event<WSocketEvent> errorEvent;

	@PostConstruct
	public void init() {

	}

	 
	
	
	@OnOpen
	public void onOpen(Session session, EndpointConfig conf) {
		//NGSessionScopeContext.changeHolder(UID);
		//sessionOpenEvent.fire(new WSocketEvent(session, null));

	}

	@OnMessage
	public void onMessage(Session session, String message) {
		
		
		
		JsonObject jObj =Util.parse(message);
		String UID = jObj.get("session").getAsString();
		
		WSocketEvent ev = new WSocketEvent(session, Util.parse(message));
		
		ev.setSession(session);
		NGSessionScopeContext.setCurrentContext(UID);
		
		String service=jObj.get("service").getAsString();

		if(service.equals("ping"))
		{
			System.out.println("wensock init ::"+message);
			sessionOpenEvent.fire(ev);
		}
		else{
			
			receiveEvents.fire(ev);
		}


	}

	@OnClose
	public void onclose(Session session) {
		sessionCloseEvent.fire(new WSocketEvent(session, null));
	}

	@OnError
	public void onError(Session session, Throwable error) {
		//errorEvent.fire(new WSocketEvent(session, Util.parse(Util.getJson(error))));
		error.printStackTrace();
	}

}