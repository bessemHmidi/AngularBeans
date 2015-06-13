/**
 * Copyright (C) 2014 Red Hat, Inc, and individual contributors.
 * Copyright (C) 2011-2012 VMware, Inc.
 */

package org.projectodd.sockjs;

/**
 * Handlers from sockjs-node's trans-websocket.coffee
 */
public class WebsocketHandler {

    private void websocketCheck(SockJsRequest req) {
        String upgradeHeader = req.getHeader("upgrade");
        if (!"websocket".equalsIgnoreCase(upgradeHeader)) {
            throw new DispatchException(400, "Can \"Upgrade\" only to \"WebSocket\".");
        }
        String connectionHeader = req.getHeader("connection");
        if (connectionHeader == null) {
            connectionHeader = "";
        }
        boolean isUpgrade = false;
        for (String conn : connectionHeader.split("/, *")) {
            if (conn.equalsIgnoreCase("upgrade")) {
                isUpgrade = true;
                break;
            }
        }
        if (!isUpgrade) {
            throw new DispatchException(400, "\"Connection\" must be \"Upgrade\".");
        }
    }

    public DispatchFunction sockjsWebsocket = new DispatchFunction() {
        @Override
        public Object handle(SockJsRequest req, SockJsResponse res, Object data) throws SockJsException {
            websocketCheck(req);
            // Valid websocket requests supported by this server don't make it this far - they
            // get handled before being dispatched to the Servlet.
            return unsupported.handle(req, res, data);
        }
    };

    public DispatchFunction rawWebsocket = new DispatchFunction() {
        @Override
        public Object handle(SockJsRequest req, SockJsResponse res, Object data) throws SockJsException {
            websocketCheck(req);
            // Valid websocket requests supported by this server don't make it this far - they
            // get handled before being dispatched to the Servlet.
            return unsupported.handle(req, res, data);
        }
    };

    private DispatchFunction unsupported = new DispatchFunction() {
        @Override
        public Object handle(SockJsRequest req, SockJsResponse res, Object data) throws SockJsException {
            // Any valid websocket request the server supports will not be dispatched here
            // With Undertow, for example, Hixie-76 isn't supported so we hit this code instead
            // of doing the websocket handshake
            res.setHeader("connection", "close");
            res.writeHead(400);
            res.end("Server doesn't support the requested WebSocket variant\r\n");
            return null;
        }
    };

    public WebsocketHandler(SockJsServer server) {
        this.server = server;
    }

    private SockJsServer server;
}
