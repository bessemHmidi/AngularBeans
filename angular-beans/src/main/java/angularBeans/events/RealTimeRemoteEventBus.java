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

import javax.inject.Inject;

import angularBeans.api.AngularBean;
import angularBeans.api.Eval;
import angularBeans.context.NGSessionScoped;
import angularBeans.realtime.RealTime;

/**
 * the RealTimeRemoteEventBus is a service called by angularBeans throw an
 * angularJS service extend angularJS event firing to the CDI container (server)
 * side with RealTime protocoles (exp: WebSockets)
 * 
 * @author hmidi bessem
 *
 */

@AngularBean
@NGSessionScoped
public class RealTimeRemoteEventBus {

	
	@Eval
	 public String addOnReadyCallback(){
		
		String script="realTimeRemoteEventBus.onReadyState=function(fn){RTSrvc.onReadyState(fn);};";
		
		return script;
	} 
	
	@Inject
	@AngularBean
	RemoteEventBus remoteEventBus;

	@RealTime
	public void subscribe(String channel) {
		remoteEventBus.subscribe(channel);
	}

	@RealTime
	public void unsubscribe(String channel) {

		remoteEventBus.unsubscribe(channel);
	}

	@RealTime
	public void fire(NGEvent event) throws ClassNotFoundException {

		remoteEventBus.fire(event);

	}

}
