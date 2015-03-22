/*
 * AngularBeans, CDI-AngularJS bridge 
 *
 * Copyright (c) 2014, Bessem Hmidi. or third-party contributors as
 * indicated by the @author tags or express copyright attribution
 * statements applied by the authors.
 *
 * This copyrighted material is made available to anyone wishing to use, modify,
 * copy, or redistribute it subject to the terms and conditions of the GNU
 * Lesser General Public License, as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License
 * for more details.
 *
 */

/**
 @author Bessem Hmidi
 */
package angularBeans.realtime;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.websocket.Session;

import org.projectodd.sockjs.SockJsConnection;
import org.projectodd.sockjs.Transport.READY_STATE;

import angularBeans.context.NGSessionScoped;
import angularBeans.log.NGLogger;
import angularBeans.util.AngularBeansUtil;
import angularBeans.wsocket.annotations.WSocketReceiveEvent;
import angularBeans.wsocket.annotations.WSocketSessionCloseEvent;
import angularBeans.wsocket.annotations.WSocketSessionReadyEvent;

@NGSessionScoped
public class WSocketClient implements Serializable {

	private Set<SockJsConnection> sessions=new HashSet<SockJsConnection>();
	
	@Inject
	AngularBeansUtil util;
	
	@Inject
	NGLogger logger;

	private static  Map<String, Session> channelsSubscribers=new HashMap<>();
	
	
	public void onSessionReady(@Observes @WSocketSessionReadyEvent WSocketEvent event) {
    sessions.add(event.getConnection());
	event.setClient(this);
	
	}

	public void onClose(@Observes @WSocketSessionCloseEvent WSocketEvent event) {
		sessions.remove(event.getConnection());
	}

	public void onError(@Observes @WSocketErrorEvent WSocketEvent event) {
		throw new RuntimeException(event.getData().toString());
	}

	public void onSession(@Observes @WSocketReceiveEvent WSocketEvent event) {

		sessions.add(event.getConnection());
		event.setClient(this);

	}

	public Set<SockJsConnection> getSessions() {
		return sessions;
	}

	
	public void invalidateSession(){
		for(SockJsConnection connection:sessions){
			connection.close(javax.websocket.CloseReason.CloseCodes.CANNOT_ACCEPT.getCode(), "CLOSED BY BACKEND");
		}
	}
	
	public void publish(String channel, WSocketMessage message) {

		Map<String, Object> paramsToSend = new HashMap<String, Object>(
				message.build());
		paramsToSend.put("reqId", channel);
		 paramsToSend.put("log", logger.getLogPool());
		
		 for(SockJsConnection session:new HashSet<SockJsConnection>(sessions)){
			
	
			 if(!session.getReadyState().equals(READY_STATE.OPEN)){sessions.remove(session);}
			 else{
			session.write(util.getJson(paramsToSend));
			 }
	
	}
	}

	
	
	public void flushModel(Class controllerClass,String modelName,Object model ){
		
		publish(controllerClass.getSimpleName(), new WSocketMessage().add(modelName, model));
		
	}
	
	public void publishToAll(String channel, WSocketMessage message) {
	
		Map<String, Object> paramsToSend = new HashMap<String, Object>(
				message.build());
		paramsToSend.put("reqId", channel);
       
		
	
			
			
			for(SockJsConnection sess:connectionHolder.getAllConnections()){
				if(sess.getReadyState().equals(READY_STATE.OPEN)){
				
					String objectMessage=util.getJson(paramsToSend);
					
					sess.write(objectMessage);
					
				
			}
			}
				
		
		
	}

	@Inject
	GlobalConnectionHolder connectionHolder;
	
	public static synchronized Map<String, Session> getChannelsSubscribers() {
		return channelsSubscribers;
	}

	

}
