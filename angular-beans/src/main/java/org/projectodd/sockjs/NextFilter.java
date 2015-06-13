/**
 * Copyright (C) 2014 Red Hat, Inc, and individual contributors.
 */

package org.projectodd.sockjs;

public interface NextFilter {
    public void handle(Object data) throws SockJsException;
}
