/**
 * Copyright (C) 2014 Red Hat, Inc, and individual contributors.
 */

package org.projectodd.sockjs;

import java.nio.charset.Charset;
import java.util.logging.Level;
import java.util.logging.Logger;


public abstract class SockJsResponse {

    public abstract String getHeader(String name);

    public abstract void setHeader(String name, String value);

    public abstract void writeHead(int statusCode) throws SockJsException;

    protected abstract void write(byte[] bytes) throws Exception;

    protected abstract void flush() throws Exception;

    protected abstract void endResponse() throws SockJsException;

    public Integer cacheFor() {
        return cacheFor;
    }

    public void cacheFor(Integer cacheFor) {
        this.cacheFor = cacheFor;
    }

    public int statusCode() {
        return statusCode;
    }

    public void statusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public synchronized void write(String content) throws SockJsException {
        log.log(Level.FINE, "Writing {0}", content);
        byte[] bytes = content.getBytes(UTF8);
        try {
            write(bytes);
            flush();
        } catch (Exception ex) {
            throw new SockJsException("Error writing response:", ex);
        }
    }

    public void end() throws SockJsException {
        end(null);
    }

    public synchronized void end(String content) throws SockJsException {
        log.log(Level.FINER, "Ending with {0}", content);
        if (content != null) {
            write(content);
        }
        endResponse();
        finished = true;
    }

    public boolean finished() {
        return finished;
    }

    private Integer cacheFor;
    private int statusCode = 200;
    private boolean finished = false;

    private static final Charset UTF8 = Charset.forName("UTF-8");
    private static final Logger log = Logger.getLogger(SockJsResponse.class.getName());
}
