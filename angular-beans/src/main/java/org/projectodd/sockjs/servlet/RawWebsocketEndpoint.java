/**
 * Copyright (C) 2014 Red Hat, Inc, and individual contributors.
 * Copyright (C) 2011-2012 VMware, Inc.
 */

package org.projectodd.sockjs.servlet;

import org.projectodd.sockjs.SockJsRequest;
import org.projectodd.sockjs.SockJsServer;

import javax.websocket.CloseReason;
import javax.websocket.Endpoint;
import javax.websocket.EndpointConfig;
import javax.websocket.Session;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RawWebsocketEndpoint extends Endpoint {

    public RawWebsocketEndpoint(SockJsServer server, String contextPath, String prefix) {
        this.server = server;
        this.contextPath = contextPath;
        this.prefix = prefix;
    }

    @Override
    public void onOpen(Session session, EndpointConfig config) {
    	

    	
        log.log(Level.FINER, "onOpen for session {0}", session.getId());
      
        
        SockJsRequest req = new SockJsWebsocketRequest(session, contextPath, prefix, Collections.EMPTY_MAP);
        receivers.put(session.getId(), new RawWebsocketSessionReceiver(req, server, session));
    }

    @Override
    public void onClose(Session session, CloseReason closeReason) {
        log.log(Level.FINER, "onClose {0} for session {1}", new Object[] {closeReason, session.getId()});
        RawWebsocketSessionReceiver receiver = receivers.get(session.getId());
       
  
        if (receiver != null) {
            receiver.didClose();
        }
    }

    @Override
    public void onError(Session session, Throwable thr) {
        log.log(Level.FINE, "Error in raw WebSocket endpoint", thr);
    }

    private SockJsServer server;
    private String contextPath;
    private String prefix;

    private static final Map<String, RawWebsocketSessionReceiver> receivers = new ConcurrentHashMap<>();
    private static final Logger log = Logger.getLogger(RawWebsocketEndpoint.class.getName());
}
