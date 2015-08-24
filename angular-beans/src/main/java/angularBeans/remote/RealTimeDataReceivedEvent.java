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
package angularBeans.remote;

import java.io.Serializable;

import org.projectodd.sockjs.SockJsConnection;

import angularBeans.realtime.RealTimeClient;

import com.google.gson.JsonObject;

/**
 * a RealTimeDataReceivedEvent concern data reception with the realtime sockjs
 * protocol
 * 
 * @author Bessem Hmidi
 *
 */

@SuppressWarnings("serial")
public class RealTimeDataReceivedEvent implements DataReceived, Serializable {

	private SockJsConnection connection;
	private JsonObject data;
	private RealTimeClient client;
	private String sessionId;

	public RealTimeDataReceivedEvent(SockJsConnection connection,
			JsonObject data) {
		this.connection = connection;
		this.data = data;

	}

	public void setConnection(SockJsConnection connection) {
		this.connection = connection;
	}

	public SockJsConnection getConnection() {
		return connection;
	}

	@Override
	public JsonObject getData() {
		return data;
	}

	public void setClient(RealTimeClient wSocketClient) {
		this.client = wSocketClient;

	}

	public RealTimeClient getClient() {
		return client;
	}

	public String getSessionId() {
		return sessionId;
	}

	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}
}
