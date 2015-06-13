/**
 * Copyright (C) 2014 Red Hat, Inc, and individual contributors.
 * Copyright (C) 2011-2012 VMware, Inc.
 */

package org.projectodd.sockjs;

import java.util.Arrays;
import java.util.List;

public class EventSourceReceiver extends ResponseReceiver {

    public EventSourceReceiver(SockJsRequest req, SockJsResponse res, SockJsServer.Options options) {
        super(req, res, options);
        protocol = "eventsource";
    }

    @Override
    public boolean doSendFrame(String payload) {
        String charsToEscape = new String(new char[] {'\r', '\n', 0});
        List<String> data = Arrays.asList("data: ", Utils.escapeSelected(payload, charsToEscape), "\r\n\r\n");
        return super.doSendFrame(Utils.join(data, ""));
    }
}
