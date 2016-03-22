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

import static angularBeans.enums.ReadyState.OPEN;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.projectodd.sockjs.SockJsConnection;

import angularBeans.boot.BeanRegistry;
import angularBeans.context.NGSessionScoped;
import angularBeans.context.SessionMapper;
import angularBeans.events.BroadcastManager;
import angularBeans.events.RealTimeMessage;
import angularBeans.events.RealTimeSessionCloseEvent;
import angularBeans.events.RealTimeSessionReadyEvent;
import angularBeans.events.ServerEvent;
import angularBeans.log.NGLogger;
import angularBeans.remote.DataReceivedEvent;
import angularBeans.remote.RealTimeDataReceivedEvent;
import angularBeans.util.AngularBeansUtils;
import angularBeans.util.CommonUtils;
import angularBeans.util.ModelQuery;
import angularBeans.util.ModelQueryImpl;
import angularBeans.util.NGBean;

/**
 * 
 * @author bessem
 * 
 *         when injected, a realTime client represent the current real time
 *         session (websocket or fallback protocol)
 * 
 **/

@NGSessionScoped
public class RealTimeClient implements Serializable {

	private Set<SockJsConnection> sessions = new HashSet<SockJsConnection>();

	@Inject
	BroadcastManager broadcastManager;

	@Inject
	GlobalConnectionHolder connectionHolder;

	@Inject
	AngularBeansUtils util;

	@Inject
	NGLogger logger;

	private boolean async = false;

	/**
	 * the Realtime client will use will use AsyncRemote of the websocket api
	 * <p>
	 * usage: realTimeClient.async().*any-method()*.
	 */

	public RealTimeClient async() {

		async = true;
		return this;
	}

	public void onSessionReady(
			@Observes @RealTimeSessionReadyEvent RealTimeDataReceivedEvent event) {

		connectionHolder.getAllConnections().add(event.getConnection());
		sessions.add(event.getConnection());

		Set<NGBean> angularBeans = BeanRegistry.INSTANCE.getAngularBeans();

		for (NGBean bean : angularBeans) {

			String httpSessionId = SessionMapper.getHTTPSessionID(event
					.getConnection().id);

			broadcastManager.subscribe(httpSessionId, bean.getTargetClass()
					.getSimpleName());
		}

		event.setClient(this);

	}

	public void onClose(
			@Observes @RealTimeSessionCloseEvent RealTimeDataReceivedEvent event) {
		connectionHolder.getAllConnections().remove(event.getConnection());
	}

	// public void onError(
	// @Observes @RealTimeErrorEvent RealTimeDataReceivedEvent event) {
	//
	// throw new RuntimeException(event.getData().toString());
	// }

	public void onData(
			@Observes @DataReceivedEvent RealTimeDataReceivedEvent event) {

		// sessions.add(event.getConnection());
		event.setClient(this);

	}

	/**
	 * will close all current realTime sessions bound to the current HTTP
	 * session
	 */
	public void invalidateSession() {
		for (SockJsConnection connection : sessions) {
			connection.close(
					javax.websocket.CloseReason.CloseCodes.CANNOT_ACCEPT
							.getCode(), "CLOSED BY BACKEND");
		}
	}

	/**
	 * send a message to the current session front end
	 * 
	 * @param channel
	 *            : can be
	 * 
	 *            - The AngularBean class name OR A custom channel
	 * 
	 * @param message
	 *            : the RealTimeMessage to send
	 */
	public void publish(String channel, RealTimeMessage message) {

		Map<String, Object> paramsToSend = prepareData(channel, message);

		publish(paramsToSend);

	}

	/**
	 * send a ModelQuery to the current session front end AngularBean proxy to
	 * update his models
	 * 
	 * @param query
	 *            : the ModelQuery to send
	 */

	public void publish(ModelQuery query) {
		Map<String, Object> paramsToSend = prepareData(query);
		publish(paramsToSend);

	}

	/**
	 * send a message to all front end open sessions
	 * 
	 * @param channel
	 *            : can be
	 * 
	 *            - The AngularBean class name - A custom channel
	 * 
	 * @param message
	 *            : the RealTimeMessage to send
	 * @param withoutMe
	 *            : possible values:
	 *            <p>
	 *            true: the current session client will not receive the message.
	 *            <p>
	 *            false: the current session client will also receive the
	 *            message.
	 */

	public void broadcast(String channel, RealTimeMessage message,
			boolean withoutMe) {

		Map<String, Object> paramsToSend = prepareData(channel, message);

		broadcast(channel, withoutMe, paramsToSend);

	}

	/**
	 * send a ModelQuery to all front end open sessions
	 * 
	 * @param query
	 *            : the ModelQuery to send
	 * 
	 * @param withoutMe
	 *            : possible values:
	 *            <p>
	 *            true: the current session client will not receive the query.
	 *            <p>
	 *            false: the current session client will also receive the query.
	 */

	public void broadcast(ModelQuery query, boolean withoutMe) {

		Map<String, Object> paramsToSend = prepareData(query);

		broadcast(query.getTargetServiceClass(), withoutMe, paramsToSend);

	}

	private Map<String, Object> prepareData(ModelQuery query) {
		Map<String, Object> paramsToSend = new HashMap<String, Object>();

		ModelQueryImpl modelQuery = (ModelQueryImpl) query;

		ServerEvent ngEvent = new ServerEvent();
		ngEvent.setName("modelQuery");
		ngEvent.setData(CommonUtils.getBeanName(modelQuery.getOwner()));

		paramsToSend.putAll(modelQuery.getData());
		paramsToSend.put("ngEvent", ngEvent);
		paramsToSend.put("log", logger.getLogPool());
		paramsToSend.put("isRT", true);
		
		return paramsToSend;
	}

	private Map<String, Object> prepareData(String eventName,
			RealTimeMessage message) {
		Map<String, Object> paramsToSend = new HashMap<String, Object>(
				message.build());

		ServerEvent ngEvent = new ServerEvent();
		ngEvent.setName(eventName);
		ngEvent.setData(message.build());

		paramsToSend.put("ngEvent", ngEvent);
		paramsToSend.put("log", logger.getLogPool());
		paramsToSend.put("isRT", true);
	
		return paramsToSend;
	}

	private void broadcast(String channel, boolean withoutMe,
			Map<String, Object> paramsToSend) {

		for (SockJsConnection connection : connectionHolder.getAllConnections()) {

			if (!broadcastManager.isSubscribed(connection.id, channel)) {
				continue;
			}
			if (withoutMe) {
				if (sessions.contains(connection)) {
					continue;
				}
			}
			if (connection.getReadyState().equals(OPEN)) {

				String objectMessage = util.getJson(paramsToSend);
				connection.write(objectMessage, async);
				async = false;
			}
		}
	}

	private void publish(Map<String, Object> paramsToSend) {
		for (SockJsConnection session : new HashSet<SockJsConnection>(sessions)) {

			if (!session.getReadyState().equals(OPEN)) {
				sessions.remove(session);
			} else {

				session.write(util.getJson(paramsToSend), async);
				async = false;
			}
		}
	}
}
