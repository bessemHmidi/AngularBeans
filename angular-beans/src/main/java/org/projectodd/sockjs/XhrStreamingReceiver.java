/**
 * Copyright (C) 2014 Red Hat, Inc, and individual contributors.
 * Copyright (C) 2011-2012 VMware, Inc.
 */

package org.projectodd.sockjs;

public class XhrStreamingReceiver extends ResponseReceiver {

    public XhrStreamingReceiver(SockJsRequest req, SockJsResponse res, SockJsServer.Options options) {
        super(req, res, options);
        protocol = "xhr-streaming";
    }

    @Override
    public boolean doSendFrame(String payload) {
        return super.doSendFrame(payload + "\n");
    }
}
