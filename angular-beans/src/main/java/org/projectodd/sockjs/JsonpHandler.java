/**
 * Copyright (C) 2014 Red Hat, Inc, and individual contributors.
 * Copyright (C) 2011-2012 VMware, Inc.
 */

package org.projectodd.sockjs;

import java.util.List;

/**
 * Handlers from sockjs-node's trans-jsonp.coffee
 */
public class JsonpHandler {

    public DispatchFunction jsonp = new DispatchFunction() {
        @Override
        public Object handle(SockJsRequest req, SockJsResponse res, Object data) throws SockJsException {
            String callback = req.getQueryParameter("c");
            if (callback == null) {
                callback = req.getQueryParameter("callback");
            }
            if (callback == null) {
                throw new DispatchException(500, "\"callback\" parameter required");
            }
            if (callback.matches("[^a-zA-Z0-9-_.]")) {
                throw new DispatchException(500, "invalid \"callback\" parameter");
            }

            res.setHeader("Content-Type", "application/javascript; charset=UTF-8");
            res.writeHead(200);

            Transport.register(req, server, new JsonpReceiver(req, res, server.options, callback));
            return true;
        }
    };

    public DispatchFunction jsonpSend = new DispatchFunction() {
        @Override
        @SuppressWarnings("unchecked")
        public Object handle(SockJsRequest req, SockJsResponse res, Object query) throws SockJsException {
            if (query == null) {
                throw new DispatchException(500, "Payload expected.");
            }
            List<String> d = null;
            if (query instanceof String) {
                try {
                    d = Utils.parseJson((String) query, List.class);
                } catch (Exception x) {
                    throw new DispatchException(500, "Broken JSON encoding.");
                }
            } else {
                String queryParam = req.getQueryParameter("d");
                if (queryParam != null) {
                    try {
                        d = Utils.parseJson(queryParam, List.class);
                    } catch (Exception x) {
                        throw new DispatchException(500, "Broken JSON encoding.");
                    }
                }
            }
            if (d == null) {
                throw new DispatchException(500, "Payload expected.");
            }
            Session jsonp = Session.bySessionId(req.session());
            if (jsonp == null) {
                throw new DispatchException(404);
            }
            for (String message : d) {
                jsonp.didMessage(message);
            }

            res.setHeader("Content-Length", "2");
            res.setHeader("Content-Type", "text/plain; charset=UTF-8");
            res.writeHead(200);
            res.end("ok");
            return true;
        }
    };

    public JsonpHandler(SockJsServer server) {
        this.server = server;
    }

    private SockJsServer server;
}
