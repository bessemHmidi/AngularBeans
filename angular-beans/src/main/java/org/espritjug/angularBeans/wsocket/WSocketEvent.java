package org.espritjug.angularBeans.wsocket;

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
