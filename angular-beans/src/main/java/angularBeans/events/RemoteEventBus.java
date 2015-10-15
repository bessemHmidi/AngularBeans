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
package angularBeans.events;

import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.ws.rs.POST;

import angularBeans.api.AngularBean;
import angularBeans.api.CORS;
import angularBeans.context.NGSessionScoped;
import angularBeans.realtime.RealTimeClient;
import angularBeans.util.AngularBeansUtils;
import angularBeans.util.CurrentNGSession;

/**
 * the RemoteEventBus is a service called by angularBeans throw an angularJS
 * service extend angularJS event firing to the CDI container (server) side
 * 
 * @author hmidi bessem
 *
 */

@AngularBean
@NGSessionScoped
public class RemoteEventBus {

	@Inject
	@AngularEvent
	Event<Object> ngEventBus;

	@Inject
	AngularBeansUtils util;

	@Inject
	CurrentNGSession session;

	@Inject
	BroadcastManager broadcastManager;

	
	@Inject RealTimeClient client;
	
	@CORS
	public void subscribe(String channel) {

		broadcastManager.subscribe(session.getSessionId(), channel);

		
	}
	
	


	@CORS
	public void unsubscribe(String channel) {

		broadcastManager.unsubscribe(session.getSessionId(), channel);
	}

	@CORS
	public void fire(NGEvent event) throws ClassNotFoundException {

		Object eventObject = util.convertEvent(event);
		ngEventBus.fire(eventObject);

	}
	
	@CORS
	public void broadcast(String channel,Map<String,Object> data,boolean withoutMe){
				
		
		RealTimeMessage realTimeMessage=new RealTimeMessage();
		
		for(Map.Entry<String, Object> entry:data.entrySet()){	
		realTimeMessage.set(entry.getKey(), entry.getValue());
		}		
		
		
		client.broadcast(channel, realTimeMessage, withoutMe);
		
	}
	
	
	

}
