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
package angularBeans.wsocket;

import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.websocket.Session;

import angularBeans.AngularBeansUtil;
import angularBeans.context.NGSessionScoped;
import angularBeans.log.NGLogger;
import angularBeans.wsocket.annotations.WSocketReceiveEvent;
import angularBeans.wsocket.annotations.WSocketSessionCloseEvent;
import angularBeans.wsocket.annotations.WSocketSessionReadyEvent;

@NGSessionScoped
public class WSocketClient implements Serializable {

	private Session session;
	
	@Inject
	AngularBeansUtil util;
	
	@Inject
	NGLogger logger;

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

	public Session getSession() {
		return session;
	}

	public void publish(String channel, WSocketMessage message) {

		Map<String, Object> paramsToSend = new HashMap<String, Object>(
				message.build());
		paramsToSend.put("reqId", channel);
		 paramsToSend.put("log", logger.getLogPool());
		try {
			session.getBasicRemote().sendText(util.getJson(paramsToSend));

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
					String objectMessage=util.getJson(paramsToSend);
					
					sess.getBasicRemote().sendText(objectMessage);
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
