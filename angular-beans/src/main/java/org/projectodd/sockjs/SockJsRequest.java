/**
 * Copyright (C) 2014 Red Hat, Inc, and individual contributors.
 */

package org.projectodd.sockjs;

import java.util.HashMap;
import java.util.Map;


/**
 * In addition to the abstract methods, implementations are expected to call
 * onDataHandler.handle and onEndHandler.handle when there is request data
 * available and when the request has ended, respectively.
 */
public abstract class SockJsRequest {

    public abstract String getMethod();

    public abstract String getUrl();

    public abstract String getPath();

    public abstract String getPrefix();

    public abstract String getRemoteAddr();

    public abstract int getRemotePort();

    public abstract String getHeader(String name);

    public abstract String getContentType();

    public abstract String getCookie(String name);

    public abstract String getQueryParameter(String name);

    public void addMatch(String key, String value) {
        matches.put(key, value);
    }

    public String server() {
        return matches.get("server");
    }

    public String session() {
        return matches.get("session");
    }

    public void onData(OnDataHandler onDataHandler) {
        this.onDataHandler = onDataHandler;
    }

    public void onEnd(OnEndHandler onEndHandler) {
        this.onEndHandler = onEndHandler;
    }

    private Map<String, String> matches = new HashMap<>();
    public DispatchFunction lastFunction;
    public NextFilter nextFilter;
    protected OnDataHandler onDataHandler;
    protected OnEndHandler onEndHandler;


    public static interface OnDataHandler {
        public void handle(byte[] bytes) throws SockJsException;
    }

    public static interface OnEndHandler {
        public void handle() throws SockJsException;
    }
}
