/**
 * Copyright (C) 2014 Red Hat, Inc, and individual contributors.
 * Copyright (C) 2011-2012 VMware, Inc.
 */

package org.projectodd.sockjs;

import java.util.Random;

/**
 * Handlers from sockjs-node's chunking-test.coffee
 */
public class ChunkingHandler {

    public DispatchFunction info = new DispatchFunction() {
        @Override
        public Object handle(SockJsRequest req, SockJsResponse res, Object data) throws SockJsException {
            SockJsServer.Options options = server.options;
            Random random = new Random();
            long entropy = random.nextInt(Integer.MAX_VALUE) + (long) random.nextInt(Integer.MAX_VALUE);
            StringBuilder json = new StringBuilder();
            json.append("{");
            if (options.baseUrl != null) {
                json.append("\"base_url\": \"").append(options.baseUrl).append("\", ");
            }
            json.append("\"websocket\": ").append(options.websocket).append(", ");
            json.append("\"origins\": [\"*:*\"], ");
            json.append("\"cookie_needed\": ").append(!!options.jsessionid).append(", ");
            json.append("\"entropy\": ").append(entropy);
            json.append("}");
            res.setHeader("Content-Type", "application/json; charset=UTF-8");
            res.writeHead(200);
            res.end(json.toString());
            return null;
        }
    };

    public DispatchFunction infoOptions = new DispatchFunction() {
        @Override
        public Object handle(SockJsRequest req, SockJsResponse res, Object data) throws SockJsException {
            res.statusCode(204);
            res.setHeader("Access-Control-Allow-Methods", "OPTIONS, GET");
            res.setHeader("Access-Control-Max-Age", "" + res.cacheFor());
            return "";
        }
    };

    public ChunkingHandler(SockJsServer server) {
        this.server = server;
    }

    private SockJsServer server;
}
