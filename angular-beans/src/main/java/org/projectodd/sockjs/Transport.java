/**
 * Copyright (C) 2014 Red Hat, Inc, and individual contributors.
 * Copyright (C) 2011-2012 VMware, Inc.
 */

package org.projectodd.sockjs;

public class Transport {

    public static String closeFrame(int status, String reason) {
        return "c" + "[" + status + ",\"" + reason + "\"]";
    }

    public static Session register(SockJsRequest req, SockJsServer server, GenericReceiver receiver) {
        return register(req, server, req.session(), receiver);
    }

    public static Session registerNoSession(SockJsRequest req, SockJsServer server, GenericReceiver receiver) {
        return register(req, server, null, receiver);
    }

    private static Session register(SockJsRequest req, SockJsServer server, String sessionId, GenericReceiver receiver) {
        Session session = Session.bySessionId(sessionId);
        if (session == null) {
            session = new Session(sessionId, server);
        }
        session.register(req, receiver);
        return session;
    }
}
