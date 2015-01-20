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

import java.io.Serializable;

import javax.websocket.Session;

import com.google.gson.JsonObject;

public class WSocketEvent implements Serializable {

	
	private Session session;
	private JsonObject data;
	private WSocketClient client;

	public WSocketEvent(Session session, JsonObject data) {
		this.session = session;
		this.data = data;

	}

	public void setSession(Session session) {
		this.session = session;
	}

	public Session getSession() {
		return session;
	}

	
	public JsonObject getData() {
		return data;
	}

	public void setClient(WSocketClient wSocketClient) {
		this.client=wSocketClient;
		
	}

	
	public WSocketClient getClient() {
		return client;
	}
}
