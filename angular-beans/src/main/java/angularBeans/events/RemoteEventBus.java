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

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.ws.rs.POST;

import angularBeans.api.AngularBean;
import angularBeans.context.NGSessionScoped;
import angularBeans.realtime.RealTime;
import angularBeans.util.AngularBeansUtil;
import angularBeans.util.CurrentNGSession;

import com.google.gson.JsonElement;
/**
 * the RemoteEventBus is a service called by angularBeans
 * throw an angularJS service extend  angularJS event firing to the 
 * CDI container (server) side
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
	AngularBeansUtil util;

	
	@Inject
	CurrentNGSession session;
	
	@Inject
	BroadcastManager broadcastManager;
	
	@POST
	public void subscribe(String channel){
		
		broadcastManager.subscribe(session.getSessionId(),channel);
		
	}
	
	@POST
	public void unsubscribe(String channel){
		
		broadcastManager.unsubscribe(session.getSessionId(),channel);
	}
	
	@POST
	public void fire(NGEvent event) throws ClassNotFoundException {
		Object o = null;

		JsonElement element = util.parse(event.getData());

		JsonElement data = null;
		Class javaClass = null;

		try {
			data = element.getAsJsonObject();

			javaClass = Class.forName(event.getDataClass());
		} catch (Exception e) {
			data = element.getAsJsonPrimitive();
			if (event.getDataClass() == null)
				event.setDataClass("String");
			javaClass = Class.forName("java.lang." + event.getDataClass());

		}

		o = (util.deserialise(javaClass, data));

		ngEventBus.fire(o);

	}

}
