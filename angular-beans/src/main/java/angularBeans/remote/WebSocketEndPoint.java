package angularBeans.remote;

import java.util.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import angularBeans.context.NGSessionScopeContext;
import angularBeans.realtime.GlobalConnectionHolder;
import angularBeans.realtime.RealTimeErrorEvent;
import angularBeans.realtime.RealTimeSessionCloseEvent;
import angularBeans.realtime.RealTimeSessionReadyEvent;
import angularBeans.util.AngularBeansUtil;

import com.google.gson.JsonObject;

@ServerEndpoint("/rt-service")
@ApplicationScoped
public class WebSocketEndPoint {


	@Inject
	GlobalConnectionHolder globalConnectionHolder;
	
	
	
    @OnOpen
        public void open(Session session) {
  
    }

    @OnClose
        public void close(Session session) {
    }

    @OnError
        public void onError(Throwable error) {
    }

    @OnMessage
        public void handleMessage(String message, Session session) {
    	
		JsonObject jObj = AngularBeansUtil.parse(message);
		String UID = jObj.get("session").getAsString();

		
		
		
//		RealTimeDataReceiveEvent ev = new RealTimeDataReceiveEvent(session,
//				jObj);
//
//		ev.setConnection(session);
		//ev.setSessionId(UID);
		NGSessionScopeContext.setCurrentContext(UID);

		String service = jObj.get("service").getAsString();

		if (service.equals("ping")) {

		//	sessionOpenEvent.fire(ev);
			Logger.getLogger("AngularBeans").info(
					"AngularBeans-client: " + UID);

		} else {

		//	receiveEvents.fire(ev);
		}

		// connection.write(message);
    	
    	
    	
    }
} 