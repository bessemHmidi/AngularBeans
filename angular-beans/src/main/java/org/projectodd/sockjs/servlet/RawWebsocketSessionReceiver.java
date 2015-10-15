/**
 * Copyright (C) 2014 Red Hat, Inc, and individual contributors.
 * Copyright (C) 2011-2012 VMware, Inc.
 */

package org.projectodd.sockjs.servlet;

import org.projectodd.sockjs.GenericReceiver;
import org.projectodd.sockjs.Session;
import org.projectodd.sockjs.SockJsRequest;
import org.projectodd.sockjs.SockJsServer;
import org.projectodd.sockjs.Transport;

import javax.websocket.CloseReason;
import javax.websocket.MessageHandler;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * RawWebsocketSessionReceiver logic from sockjs-node's trans-websocket.coffee
 */
public class RawWebsocketSessionReceiver extends Session {

    public RawWebsocketSessionReceiver(SockJsRequest req, SockJsServer server, javax.websocket.Session ws) {
        super(null, server);
        this.ws = ws;
        this.ws.addMessageHandler(new MessageHandler.Whole<String>() {
            @Override
            public void onMessage(String message) {
                didMessage(message);
            }
        });

        readyState = Transport.READY_STATE.OPEN;
        recv = new GenericReceiver() {
            {
                protocol = "websocket-raw";
            }
            @Override
            public boolean doSendFrame(String payload) {
                return false; // never called
            }
            @Override
            public void checkAlive() {
                // no-op
            }
        };
        decorateConnection(req);
        server.emitConnection(connection);
    }

    @Override
    public void didMessage(String payload) {
        if (readyState == Transport.READY_STATE.OPEN) {
            connection.emitData(payload);
        }
    }

    @Override
    public boolean send(String payload,boolean async) {
    	
    	
        if (readyState != Transport.READY_STATE.OPEN) {
            return false;
        }
        try {
        	
        
        	
        	if(!async)
        	{
            ws.getBasicRemote().sendText(payload);
        	}
        	else{
        	ws.getAsyncRemote().sendText(payload);
        	}
        } catch (IOException ex) {
            log.log(Level.WARNING, "Error sending raw websocket data", ex);
        }
        return true;
    }

    @Override
    public boolean close(int status, String reason) {
        if (readyState != Transport.READY_STATE.OPEN) {
            return false;
        }
        readyState = Transport.READY_STATE.CLOSING;
        try {
            ws.close(new CloseReason(CloseReason.CloseCodes.getCloseCode(status), reason));
        } catch (IOException ex) {
            log.log(Level.FINE, "Error closing raw websocket", ex);
        }
        return true;
    }

    public void didClose() {
        if (ws == null) {
            return;
        }
        try {
            ws.close(new CloseReason(CloseReason.CloseCodes.NORMAL_CLOSURE, "Normal closure"));
        } catch (IOException x) {
            log.log(Level.FINE, "Error closing receiver", x);
        }
        ws = null;

        readyState = Transport.READY_STATE.CLOSED;
        connection.emitClose();
        connection = null;
    }

    private javax.websocket.Session ws;

    private static final Logger log = Logger.getLogger(RawWebsocketSessionReceiver.class.getName());
}
