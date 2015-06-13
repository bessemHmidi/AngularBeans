/**
 * Copyright (C) 2014 Red Hat, Inc, and individual contributors.
 */

package org.projectodd.sockjs.servlet;

import org.projectodd.sockjs.SockJsException;
import org.projectodd.sockjs.SockJsRequest;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SockJsServletRequest extends SockJsRequest implements ReadListener {

    public SockJsServletRequest(HttpServletRequest request) {
        this.request = request;
    }

    @Override
    public String getMethod() {
        return request.getMethod();
    }

    @Override
    public String getUrl() {
        String url = request.getRequestURI();
        if (request.getQueryString() != null) {
            url += "?" + request.getQueryString();
        }
        return url;
    }

    @Override
    public String getPath() {
        return request.getPathInfo();
    }

    @Override
    public String getPrefix() {
        String prefix = request.getContextPath() + request.getServletPath();
        return prefix.equals("") ? "/" : prefix;
    }

    @Override
    public String getRemoteAddr() {
        return request.getRemoteAddr();
    }

    @Override
    public int getRemotePort() {
        return request.getRemotePort();
    }

    @Override
    public String getHeader(String name) {
        return request.getHeader(name);
    }

    @Override
    public String getContentType() {
        return request.getContentType();
    }

    @Override
    public String getCookie(String name) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals(name)) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }

    @Override
    public String getQueryParameter(String name) {
        return request.getParameter(name);
    }

    @Override
    public void onDataAvailable() throws IOException {
        ServletInputStream inputStream = request.getInputStream();
        do {
            byte[] buffer = new byte[1024*4];
            int length = inputStream.read(buffer);
            if (length > 0) {
                if (onDataHandler != null) {
                    try {
                        onDataHandler.handle(Arrays.copyOf(buffer, length));
                    } catch (SockJsException e) {
                        throw new IOException(e);
                    }
                }
            }
        } while (inputStream.isReady());
    }

    @Override
    public void onAllDataRead() throws IOException {
        if (onEndHandler != null) {
            try {
                onEndHandler.handle();
            } catch (SockJsException e) {
                throw new IOException(e);
            }
        }
    }

    @Override
    public void onError(Throwable throwable) {
        log.log(Level.WARNING, "Error in SockJS request ReadListener", throwable);
    }

    private HttpServletRequest request;

    private static final Logger log = Logger.getLogger(SockJsServletRequest.class.getName());
}
