/**
 * Copyright (C) 2014 Red Hat, Inc, and individual contributors.
 * Copyright (C) 2011-2012 VMware, Inc.
 */

package org.projectodd.sockjs;

public class HtmlFileReceiver extends ResponseReceiver {

    public HtmlFileReceiver(SockJsRequest req, SockJsResponse res, SockJsServer.Options options) {
        super(req, res, options);
        protocol = "htmlfile";
    }

    @Override
    public boolean doSendFrame(String payload) {
        return super.doSendFrame("<script>\np(" + Utils.jsonStringify(payload) + ");\n</script>\r\n");
    }
}
