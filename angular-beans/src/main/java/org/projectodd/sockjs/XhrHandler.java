/**
 * Copyright (C) 2014 Red Hat, Inc, and individual contributors.
 * Copyright (C) 2011-2012 VMware, Inc.
 */

package org.projectodd.sockjs;

import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Handlers from sockjs-node's trans-xhr.coffee
 */
public class XhrHandler {

    public DispatchFunction xhrOptions = new DispatchFunction() {
        @Override
        public Object handle(SockJsRequest req, SockJsResponse res, Object data) throws SockJsException {
            res.statusCode(204);
            res.setHeader("Access-Control-Allow-Methods", "OPTIONS, POST");
            res.setHeader("Access-Control-Max-Age", "" + res.cacheFor());
            return "";
        }
    };

    public DispatchFunction xhrSend = new DispatchFunction() {
        @Override
        @SuppressWarnings("unchecked")
        public Object handle(SockJsRequest req, SockJsResponse res, Object data) throws SockJsException {
            log.log(Level.FINE, "XHR send");
            if (data == null || data.toString().length() == 0) {
                throw new DispatchException(500, "Payload expected.");
            }
            List<String> d;
            try {
                d = Utils.parseJson(data.toString(), List.class);
            } catch (Exception e) {
                throw new DispatchException(500, "Broken JSON encoding.");
            }
            Session jsonp = Session.bySessionId(req.session());
            if (jsonp == null) {
                throw new DispatchException(404);
            }
            for (String message : d) {
                jsonp.didMessage(message);
            }
            res.setHeader("Content-Type", "text/plain; charset=UTF-8");
            res.writeHead(204);
            res.end();
            return true;
        }
    };

    public DispatchFunction xhrCors = new DispatchFunction() {
        @Override
        public Object handle(SockJsRequest req, SockJsResponse res, Object content) throws SockJsException {
            String origin = req.getHeader("origin");
            if (origin == null || origin.equals("null")) {
                origin = "*";
            }
            res.setHeader("Access-Control-Allow-Origin", origin);
            String headers = req.getHeader("access-control-request-headers");
            if (headers != null) {
                res.setHeader("Access-Control-Allow-Headers", headers);
            }
            res.setHeader("Access-Control-Allow-Credentials", "true");
            return content;
        }
    };

    public DispatchFunction xhrPoll = new DispatchFunction() {
        @Override
        public Object handle(SockJsRequest req, SockJsResponse res, Object data) throws SockJsException {
            res.setHeader("Content-Type", "application/javascript; charset=UTF-8");
            res.writeHead(200);

            Transport.register(req, server, new XhrPollingReceiver(req, res, server.options));
            return true;
        }
    };

    public DispatchFunction xhrStreaming = new DispatchFunction() {
        @Override
        public Object handle(SockJsRequest req, SockJsResponse res, Object data) throws SockJsException {
            res.setHeader("Content-Type", "application/javascript; charset=UTF-8");
            res.writeHead(200);

            char[] ieWorkaround = new char[2048];
            Arrays.fill(ieWorkaround, 'h');
            res.write(new String(ieWorkaround) + "\n");

            Transport.register(req, server, new XhrStreamingReceiver(req, res, server.options));
            return true;
        }
    };

    public XhrHandler(SockJsServer server) {
        this.server = server;
    }

    private SockJsServer server;

    private static final Logger log = Logger.getLogger(XhrHandler.class.getName());
}
