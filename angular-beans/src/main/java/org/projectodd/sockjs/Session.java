/**
 * Copyright (C) 2014 Red Hat, Inc, and individual contributors.
 * Copyright (C) 2011-2012 VMware, Inc.
 */

package org.projectodd.sockjs;

import static angularBeans.enums.ReadyState.CLOSED;
import static angularBeans.enums.ReadyState.CLOSING;
import static angularBeans.enums.ReadyState.CONNECTING;
import static angularBeans.enums.ReadyState.OPEN;
import static org.projectodd.sockjs.Transport.closeFrame;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;
import java.util.logging.Level;
import java.util.logging.Logger;

import angularBeans.enums.ReadyState;

public class Session {

    public Session(String sessionId, final SockJsServer server) {
        this.sessionId = sessionId;
        this.server = server;
        heartbeatDelay = server.options.heartbeatDelay;
        disconnectDelay = server.options.disconnectDelay;
        sendBuffer = new ArrayList<>();
        readyState = CONNECTING;
        if (sessionId != null && sessionId.length() > 0) {
            log.fine("Adding session " + sessionId);
            sessions.put(sessionId, this);
        }
        timeoutCb = new Runnable() {
            @Override
            public void run() {
                didTimeout();
            }
        };
        toTref = server.setTimeout(timeoutCb, disconnectDelay);
        connection = new SockJsConnection(this);
        emitOpen = new Runnable() {
            @Override
            public void run() {
                emitOpen = null;
                server.emitConnection(connection);
            }
        };
    }

    public void register(SockJsRequest req, GenericReceiver recv) {
        if (this.recv != null) {
            this.recv.checkAlive();
            if (this.recv != null) {
                recv.doSendFrame(closeFrame(2010, "Another connection still open"));
                recv.didClose();
                return;
            } else {
                // Looks like the client closed the connection on the previous
                // receiver so register this new receiver again now that we've
                // figured that out
                Transport.register(req, server, recv);
                return;
            }
        }
        if (toTref != null) {
            server.clearTimeout(toTref);
            toTref = null;
        }
        if (readyState.equals(CLOSING)) {
            flushToRecv(recv);
            recv.doSendFrame(closeFrame);
            recv.didClose();
            toTref = server.setTimeout(timeoutCb, disconnectDelay);
            return;
        }
        this.recv = recv;
        recv.session = this;

        decorateConnection(req);

        if (readyState.equals(CONNECTING)) {
            recv.doSendFrame("o");
            readyState = OPEN;
            // TODO: sockjs-node does this on process.nextTick
            emitOpen.run();
        }

        if (recv == null) {
            return;
        }
        tryFlush();
    }

    protected void decorateConnection(SockJsRequest req) {
        connection.remoteAddress = req.getRemoteAddr();
        connection.remotePort = req.getRemotePort();

        connection.url = req.getUrl();
        connection.pathname = req.getPath();
        connection.prefix = req.getPrefix();
        connection.protocol = recv.protocol;

        String[] headerNames = new String[] {"referer", "x-client-ip",
                "x-forwarded-for", "x-cluster-client-ip", "via", "x-real-ip",
                "host", "user-agent", "accept-language"};
        for (String headerName : headerNames) {
            String value = req.getHeader(headerName);
            if (value != null) {
                connection.headers.put(headerName.toLowerCase(), value);
            }
        }
    }

    public void unregister() {
        recv.session = null;
        recv = null;
        if (toTref != null) {
            server.clearTimeout(toTref);
        }
        toTref = server.setTimeout(timeoutCb, disconnectDelay);
    }

    private boolean flushToRecv(GenericReceiver receiver) {
        if (sendBuffer.size() > 0) {
            List<String> sb = new ArrayList<>(sendBuffer);
            sendBuffer = new ArrayList<>();
            recv.doSendBulk(sb);
            return true;
        }
        return false;
    }

    private void tryFlush() {
        if (!flushToRecv(recv)) {
            if (toTref != null) {
                server.clearTimeout(toTref);
            }
            Runnable x = new Runnable() {
                @Override
                public void run() {
                    if (recv != null) {
                        toTref = server.setTimeout(this, heartbeatDelay);
                        recv.doSendFrame("h");
                    }
                }
            };
            toTref = server.setTimeout(x, heartbeatDelay);
        }
    }

    public void didTimeout() {
        if (toTref != null) {
            server.clearTimeout(toTref);
            toTref = null;
        }
        if (!(readyState.equals(CONNECTING) && readyState.equals(OPEN)
        		&& readyState.equals(CLOSING))) {
            // TODO: Use some other exception class
            throw new RuntimeException("INVALID_STATE_ERR");
        }
        if (recv != null) {
            // TODO: Use some other exception class
            throw new RuntimeException("RECV_STILL_THERE");
        }
        readyState = CLOSED;
        connection.emitClose();
        connection = null;
        if (sessionId != null) {
            log.fine("Removing session " + sessionId);
            sessions.remove(sessionId);
            sessionId = null;
        }
    }

    public void didMessage(String payload) {
        log.finer("didMessage " + payload);
        if (readyState.equals(OPEN)) {
            connection.emitData(payload);
        }
    }

    public boolean send(String payload,boolean async) {
        if (!readyState.equals(OPEN)) {
            return false;
        }
        sendBuffer.add(payload);
        if (recv != null) {
            tryFlush();
        }
        return true;
    }

    public boolean close() {
        return close(1000, "Normal closure");
    }

    public boolean close(int status, String reason) {
        if (!readyState.equals(OPEN)) {
            return false;
        }
        readyState = CLOSING;
        closeFrame = closeFrame(status, reason);
        if (recv != null) {
            recv.doSendFrame(closeFrame);
            if (recv != null) {
                recv.didClose();
            }
            if (recv != null) {
                unregister();
            }
        }
        return true;
    }

    public static Session bySessionId(String sessionId) {
        if (sessionId == null) {
            return null;
        }
        return sessions.get(sessionId);
    }

    private String sessionId;
    private SockJsServer server;
    private int disconnectDelay;
    private int heartbeatDelay;
    private List<String> sendBuffer;
    protected ReadyState readyState;
    private Runnable timeoutCb;
    private ScheduledFuture toTref;
    protected SockJsConnection connection;
    private Runnable emitOpen;
    protected GenericReceiver recv;
    private String closeFrame;

    // TODO: Should this  be scoped to SockJsServer instances instead of across all apps?
    private static Map<String, Session> sessions = new ConcurrentHashMap<>();
    private static final Logger log = Logger.getLogger(Session.class.getName());

}
