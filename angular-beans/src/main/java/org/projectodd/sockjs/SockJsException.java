/**
 * Copyright (C) 2014 Red Hat, Inc, and individual contributors.
 */

package org.projectodd.sockjs;

public class SockJsException extends Exception {

    public SockJsException() {
        super();
    }

    public SockJsException(String message) {
        super(message);
    }

    public SockJsException(String message, Throwable cause) {
        super(message, cause);
    }

    public SockJsException(Throwable cause) {
        super(cause);
    }
}
