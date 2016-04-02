/**
 * Copyright (C) 2014 Red Hat, Inc, and individual contributors. Copyright (C) 2011-2012 VMware, Inc.
 */

package org.projectodd.sockjs.servlet;

import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.websocket.CloseReason;
import javax.websocket.MessageHandler;
import javax.websocket.Session;

import org.projectodd.sockjs.GenericReceiver;
import org.projectodd.sockjs.Utils;

/**
 * WebsocketReceiver logic from sockjs-node's trans-websocket.coffee
 */
public class WebsocketReceiver extends GenericReceiver {

	public WebsocketReceiver(Session ws) {
		protocol = "websocket";
		this.ws = ws;
		this.ws.addMessageHandler(new MessageHandler.Whole<String>() {

			@Override
			public void onMessage(String message) {
				didMessage(message);
			}
		});
	}

	@SuppressWarnings("unchecked")
	private void didMessage(String payload) {
		if (ws != null && session != null && payload.length() > 0) {
			if (payload.charAt(0) == '[') {
				List<String> messages;
				try {
					messages = Utils.parseJson(payload, List.class);
				} catch (Exception x) {
					try {
						ws.close(new CloseReason(CloseReason.CloseCodes.PROTOCOL_ERROR, "Broken framing"));
					} catch (IOException e) {
						//
					}
					return;
				}
				for (String message : messages) {
					session.didMessage(message);
				}
			} else {
				String message;
				try {
					message = Utils.parseJson(payload, String.class);
				} catch (Exception x) {
					try {
						ws.close(new CloseReason(CloseReason.CloseCodes.PROTOCOL_ERROR, "Broken framing"));
					} catch (IOException e) {
						//
					}
					return;
				}
				session.didMessage(message);
			}
		}
	}

	@Override
	public boolean doSendFrame(String payload) {
		if (ws != null) {
			try {
				ws.getBasicRemote().sendText(payload);
				return true;
			} catch (IOException x) {
				didClose();
			}
		}
		return false;
	}

	@Override
	public void checkAlive() {
		doSendFrame("h");
	}

	@Override
	protected void didClose() {
		super.didClose();
		try {
			ws.close(new CloseReason(CloseReason.CloseCodes.NORMAL_CLOSURE, "Normal closure"));
		} catch (IOException x) {
			log.log(Level.FINE, "Error closing receiver", x);
		}
		ws = null;
	}

	private Session ws;

	private static final Logger log = Logger.getLogger(WebsocketReceiver.class.getName());
}
