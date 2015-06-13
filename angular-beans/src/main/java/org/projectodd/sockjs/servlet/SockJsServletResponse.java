/**
 * Copyright (C) 2014 Red Hat, Inc, and individual contributors.
 */

package org.projectodd.sockjs.servlet;

import org.projectodd.sockjs.SockJsException;
import org.projectodd.sockjs.SockJsResponse;

import javax.servlet.AsyncContext;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class SockJsServletResponse extends SockJsResponse {

    public SockJsServletResponse(HttpServletResponse res, AsyncContext asyncContext) {
        this.response = res;
        this.asyncContext = asyncContext;
    }

    @Override
    public String getHeader(String name) {
        return response.getHeader(name);
    }

    @Override
    public void setHeader(String name, String value) {
        if (name.equalsIgnoreCase("content-type")) {
            response.setContentType(value);
        } else {
            response.setHeader(name, value);
        }
    }

    @Override
    public void writeHead(int statusCode) throws SockJsException {
        response.setStatus(statusCode);
        try {
            response.flushBuffer();
        } catch (IOException ex) {
            throw new SockJsException("Error writing response head:", ex);
        }
    }

    @Override
    protected void write(byte[] bytes) throws IOException {
        response.getOutputStream().write(bytes);
    }

    @Override
    protected void flush() throws IOException {
        response.getOutputStream().flush();
    }

    @Override
    protected void endResponse() throws SockJsException {
        asyncContext.complete();
    }

    private final HttpServletResponse response;
    private final AsyncContext asyncContext;
}
