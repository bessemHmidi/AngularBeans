/**
 * Copyright (C) 2014 Red Hat, Inc, and individual contributors.
 * Copyright (C) 2011-2012 VMware, Inc.
 */

package org.projectodd.sockjs.servlet;

import org.projectodd.sockjs.SockJsServer;
import org.projectodd.sockjs.SockJsRequest;
import org.projectodd.sockjs.Transport;

import javax.websocket.CloseReason;
import javax.websocket.Endpoint;
import javax.websocket.EndpointConfig;
import javax.websocket.Session; 
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;


public class SockJsEndpoint extends Endpoint {

    public SockJsEndpoint(SockJsServer server, String contextPath, String prefix) {
        this.server = server;
        this.contextPath = contextPath;
        this.prefix = prefix;
    }

    @Override
    public void onOpen(Session session, EndpointConfig config) {
        log.log(Level.FINER, "onOpen");
        String sessionId = session.getPathParameters().get("session");
        Map<String, List<String>> headers = SockJsServlet.retrieveHeaders(sessionId);
        SockJsRequest req = new SockJsWebsocketRequest(session, contextPath, prefix, headers);
        Transport.registerNoSession(req, server, new WebsocketReceiver(session));
    }

    @Override
    public void onClose(Session session, CloseReason closeReason) {
        log.log(Level.FINER, "onClose {0}", closeReason);
    }

    @Override
    public void onError(Session session, Throwable thr) {
        log.log(Level.FINE, "Error in SockJS WebSocket endpoint", thr);
    }

    private SockJsServer server;
    private String contextPath;
    private String prefix;

    private static final Logger log = Logger.getLogger(SockJsEndpoint.class.getName());
}
