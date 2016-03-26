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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.enterprise.context.ApplicationScoped;

import angularBeans.context.SessionMapper;
/**
 * when a broadcast operation is triggered
 * (ModelQuery broadcast or Event broadcast)
 * the RealTimeClient willUse the BroadcastManager
 * to now witch sessions's will be involved in the data broadcast 
 *  
 * @author Bessem Hmidi
 */

@ApplicationScoped
public class BroadcastManager {

	private final Map<String, Set<String>> subscriptions = new HashMap<>();

	/**
	 * check if a specific sockJs session is subscribed or not to 
	 * a specific channel
	 *  
	 * @param sockJSSessionID
	 * sockJs Session id
	 * @param channel
	 * the channel
	 * @return
	 * the session passed in sockJSSessionID parameter is
	 * subscribed (true)  or not (false) to the specified channel
	 */
	
	public boolean isSubscribed(String sockJSSessionID, String channel) {

		String httpSessionId = SessionMapper.getHTTPSessionID(sockJSSessionID);

		if (subscriptions.get(channel) == null) {
			subscriptions.put(channel, new HashSet<String>());
		}

		return subscriptions.get(channel).contains(httpSessionId);
	}

	public void subscribe(String httpSessionID, String channel) {

		if (subscriptions.get(channel) == null) {
			subscriptions.put(channel, new HashSet<String>());
		}

		subscriptions.get(channel).add(httpSessionID);

	}

	public void unsubscribe(String httpSessionID, String channel) {
		if (subscriptions.get(channel) != null)
			subscriptions.get(channel).remove(httpSessionID);

	}

	public Map<String, Set<String>> getSubscriptions() {
		return subscriptions;
	}

}
