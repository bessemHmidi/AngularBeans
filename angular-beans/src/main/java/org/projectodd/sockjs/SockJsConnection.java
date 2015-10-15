/**
 * Copyright (C) 2014 Red Hat, Inc, and individual contributors.
 * Copyright (C) 2011-2012 VMware, Inc.
 */

package org.projectodd.sockjs;

import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.Response.StatusType;

public class SockJsConnection {

    public SockJsConnection(Session session) {
        this.session = session;
        this.id = Utils.uuid();
    }

    @Override
    public String toString() {
        return "<SockJSConnection " + id + ">";
    }

    /**
     * Sends a message over the opened connection. A message must be a
     * non-empty string. It's illegal to send a message after the connection
     * was closed (either after 'close' or 'end' method or 'close' event).
     *
     * @param message the message to send
     * @return true on success, false on failure
     */
    public boolean write(String message,boolean async) {
        return session.send(message,async);
    }

    /**
     * Asks the remote client to disconnect with default 'code' and 'reason' values.
     */
    public void end() {
        session.close(0,"REFRESHING TAB");
    }

    /**
     * Write a message and then ask the remote client to disconnect with
     * default 'code' and 'reason' values.
     *
     * @param message message to write
     */
    public void end(String message) {
        if (message != null) {
        	boolean async=true;
            write(message,async);
        }
        end();
    }

    /**
     * Ask the remote client to disconnect with default 'code' and 'reason'
     * values
     *
     * @return false if the connection was already closed, true otherwise
     */
    public boolean close() {
        return session.close();
    }

    /**
     * Asks the remote client to disconnect with the given 'code' and 'reason' values
     * @param code The code, typically one of {@link javax.websocket.CloseReason.CloseCodes#getCode()}
     * @param reason A string describing the reason for closure
     * @return false if the connection was already closed, true otherwise
     */
    public boolean close(int code, String reason) {
        return session.close(code, reason);
    }

    public void destroy() {
        end();
    }

    /**
     * Called for every message received from a client
     *
     * @param onDataHandler The handler to call when messages arrive
     */
    public void onData(OnDataHandler onDataHandler) {
        this.onDataHandler = onDataHandler;
    }
    public void emitData(String message) {
        if (onDataHandler != null) {
            onDataHandler.handle(message);
        }
    }

    /**
     * Called when a connection to a client is closed. This is triggered
     * exactly once for every connection.
     *
     * @param onCloseHandler The handler to call when a connection is closed
     */
    public void onClose(OnCloseHandler onCloseHandler) {
        this.onCloseHandler = onCloseHandler;
    }
    public void emitClose() {
        if (onCloseHandler != null) {
            onCloseHandler.handle();
        }
    }

    private Session session;
    private OnDataHandler onDataHandler;
    private OnCloseHandler onCloseHandler;

    /**
     * Unique identifier of this connection
     */
    public String id;

    /**
     * Last known IP address of the client.
     */
    public String remoteAddress;

    /**
     * Last known port number of the client.
     */
    public int remotePort;

    /**
     * Hash containing various headers copied from last request received on
     * this connection. Exposed headers include: `origin`, `referer` and
     * `x-forwarded-for` (and friends), all lowercase. This explicitly does
     * not grant access to the `cookie` header, as using it may easily lead
     * to security issues (for details read the section "Authorisation" at
     * https://github.com/sockjs/sockjs-node).
     */
    public Map<String, String> headers = new HashMap<>();

    /**
     * The entire url string copied from the last request.
     */
    public String url;

    /**
     * `pathname` from parsed url, for convenience.
     */
    public String pathname;

    /**
     * Prefix of the url on which the request was handled. For SockJsServlet,
     * this is the servlet's context path
     */
    public String prefix;

    /**
     * Protocol used by the connection. Keep in mind that some protocols are
     * indistinguishable - for example "xhr-polling" and "xdr-polling".
     */
    public String protocol;

    /**
     * Current state of the connection
     */
    public Transport.READY_STATE getReadyState() {
        return session.readyState;
    }

    public static interface OnDataHandler {
        public void handle(String message);
    }

    public static interface OnCloseHandler {
        public void handle();
    }
}
