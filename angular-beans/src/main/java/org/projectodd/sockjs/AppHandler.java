/**
 * Copyright (C) 2014 Red Hat, Inc, and individual contributors.
 * Copyright (C) 2011-2012 VMware, Inc.
 */

package org.projectodd.sockjs;

/**
 * Handlers from sockjs-node's sockjs.coffee Server class
 */
public class AppHandler {

    public DispatchFunction welcomeScreen = new DispatchFunction() {
        @Override
        public Object handle(SockJsRequest req, SockJsResponse res, Object data) throws SockJsException {
            res.setHeader("content-type", "text/plain; charset=UTF-8");
            res.writeHead(200);
            res.end("Welcome to SockJS!\n");
            return true;
        }
    };

    public DispatchFunction handle404 = new DispatchFunction() {
        @Override
        public Object handle(SockJsRequest req, SockJsResponse res, Object x) throws SockJsException {
            if (res.finished()) {
                return x;
            }
            res.setHeader("content-type", "text/plain; charset=UTF-8");
            res.writeHead(404);
            res.end("404 Error: Page not found\n");
            return true;
        }
    };

    public DispatchFunction disabledTransport = new DispatchFunction() {
        @Override
        public Object handle(SockJsRequest req, SockJsResponse res, Object data) throws SockJsException {
            return handle404.handle(req, res, data);
        }
    };

    public DispatchFunction hSid = new DispatchFunction() {
        @Override
        public Object handle(SockJsRequest req, SockJsResponse res, Object data) throws SockJsException {
            if (server.options.jsessionid) {
                String jsid = req.getCookie("JSESSIONID");
                if (jsid == null) {
                    jsid = "dummy";
                }
                res.setHeader("Set-Cookie", "JSESSIONID=" + jsid + "; path=/");
            }
            return data;
        }
    };

    public AppHandler(SockJsServer server) {
        this.server = server;
    }

    private SockJsServer server;
}
