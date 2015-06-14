/**
 * Copyright (C) 2014 Red Hat, Inc, and individual contributors.
 */

package org.projectodd.sockjs;

public class DispatchException extends RuntimeException {
    public DispatchException(int status) {
        this.status = status;
    }
    public DispatchException(int status, String message) {
        this.status = status;
        this.message = message;
    }
    public int status;
    public String message;
}
