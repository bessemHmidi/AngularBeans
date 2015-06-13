/**
 * Copyright (C) 2014 Red Hat, Inc, and individual contributors.
 * Copyright (C) 2011-2012 VMware, Inc.
 */

package org.projectodd.sockjs;

/**
 * Roughly correlates to sockjs-node's iframe.coffee
 */
public class IframeHandler {

    private static final String IFRAME_TEMPLATE =
            "<!DOCTYPE html>\n" +
                    "<html>\n" +
                    "<head>\n" +
                    "  <meta http-equiv=\"X-UA-Compatible\" content=\"IE=edge\" />\n" +
                    "  <meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\" />\n" +
                    "  <script>\n" +
                    "    document.domain = document.domain;\n" +
                    "    _sockjs_onload = function(){SockJS.bootstrap_iframe();};\n" +
                    "  </script>\n" +
                    "  <script src=\"{{ sockjs_url }}\"></script>\n" +
                    "</head>\n" +
                    "<body>\n" +
                    "  <h2>Don't panic!</h2>\n" +
                    "  <p>This is a SockJS hidden iframe. It's used for cross domain magic.</p>\n" +
                    "</body>\n" +
                    "</html>";

    public DispatchFunction iframe = new DispatchFunction() {
        @Override
        public Object handle(SockJsRequest req, SockJsResponse res, Object data) throws SockJsException {
            String content = IFRAME_TEMPLATE.replace("{{ sockjs_url }}", server.options.sockjsUrl);
            String quotedMd5 = "\"" + Utils.md5Hex(content) + "\"";
            if (quotedMd5.equals(req.getHeader("if-none-match"))) {
                res.statusCode(304);
                return "";
            }
            res.setHeader("Content-Type", "text/html; charset=UTF-8");
            res.setHeader("ETag", quotedMd5);
            return content;
        }
    };

    public IframeHandler(SockJsServer server) {
        this.server = server;
    }

    private SockJsServer server;
}
