/**
 * Copyright (C) 2014 Red Hat, Inc, and individual contributors.
 */

package org.projectodd.sockjs;

public interface DispatchFunction {
    public Object handle(SockJsRequest req, SockJsResponse res, Object data) throws SockJsException;
}
