/**
 * Copyright (C) 2014 Red Hat, Inc, and individual contributors.
 * Copyright (C) 2011-2012 VMware, Inc.
 */

package org.projectodd.sockjs;

/**
 * Roughly correlates to sockjs-node's trans-eventsource.coffee
 */
public class EventsourceHandler {

    public DispatchFunction eventsource = new DispatchFunction() {
        @Override
        public Object handle(SockJsRequest req, SockJsResponse res, Object data) throws SockJsException {
            res.setHeader("Content-Type", "text/event-stream; charset=UTF-8");
            res.writeHead(200);
            res.write("\r\n");

            Transport.register(req, server, new EventSourceReceiver(req, res, server.options));
            return true;
        }
    };

    public EventsourceHandler(SockJsServer server) {
        this.server = server;
    }

    private SockJsServer server;
}
