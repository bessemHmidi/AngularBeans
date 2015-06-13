/**
 * Copyright (C) 2014 Red Hat, Inc, and individual contributors.
 * Copyright (C) 2011-2012 VMware, Inc.
 */

package org.projectodd.sockjs;

import java.util.Arrays;

/**
 * Handlers from sockjs-node's trans-htmlfile.coffee
 */
public class HtmlfileHandler {

    private static final String IFRAME_TEMPLATE =
            "<!doctype html>\n" +
                    "<html><head>\n" +
                    "  <meta http-equiv=\"X-UA-Compatible\" content=\"IE=edge\" />\n" +
                    "  <meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\" />\n" +
                    "</head><body><h2>Don't panic!</h2>\n" +
                    "  <script>\n" +
                    "    document.domain = document.domain;\n" +
                    "    var c = parent.{{ callback }};\n" +
                    "    c.start();\n" +
                    "    function p(d) {c.message(d);};\n" +
                    "    window.onload = function() {c.stop();};\n" +
                    "  </script>";

    public DispatchFunction htmlfile = new DispatchFunction() {
        @Override
        public Object handle(SockJsRequest req, SockJsResponse res, Object data) throws SockJsException {
            String callback = req.getQueryParameter("c");
            if (callback == null) {
                callback = req.getQueryParameter("callback");
            }
            if (callback == null) {
                throw new DispatchException(500, "\"callback\" parameter required");
            }
            if (callback.matches("[^a-zA-Z0-9-_.]")) {
                throw new DispatchException(500, "invalid \"callback\" parameter");
            }

            res.setHeader("Content-Type", "text/html; charset=UTF-8");
            res.writeHead(200);

            char[] safariPadding = new char[1024 - IFRAME_TEMPLATE.length() + 14];
            Arrays.fill(safariPadding, ' ');
            String iframeTemplate = IFRAME_TEMPLATE + new String(safariPadding) + "\r\n\r\n";
            res.write(iframeTemplate.replace("{{ callback }}", callback));

            Transport.register(req, server, new HtmlFileReceiver(req, res, server.options));
            return true;
        }
    };

    public HtmlfileHandler(SockJsServer server) {
        this.server = server;
    }

    private SockJsServer server;
}
