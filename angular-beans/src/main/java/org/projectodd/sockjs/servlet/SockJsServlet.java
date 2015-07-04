/**
 * Copyright (C) 2014 Red Hat, Inc, and individual contributors.
 */

package org.projectodd.sockjs.servlet;

import org.projectodd.sockjs.SockJsException;
import org.projectodd.sockjs.SockJsServer;

import javax.servlet.AsyncContext;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.websocket.DeploymentException;
import javax.websocket.HandshakeResponse;
import javax.websocket.server.HandshakeRequest;
import javax.websocket.server.ServerContainer;
import javax.websocket.server.ServerEndpointConfig;

import java.io.IOException;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SockJsServlet extends HttpServlet {

    public SockJsServlet() {

    }

    public SockJsServlet(SockJsServer sockJsServer) {
        this.sockJsServer = sockJsServer;
    }

    public void setServer(SockJsServer sockJsServer) {
        this.sockJsServer = sockJsServer;
    }

    public SockJsServer getServer() {
        return sockJsServer;
    }

    
    @Override
    public void init() throws ServletException {
   
    }
    
    
    
//    public void initJSR356() throws ServletException{
//        if (sockJsServer == null) {
//            sockJsServer = new SockJsServer();
//        }
//        sockJsServer.init();
//
//        if (sockJsServer.options.websocket) {
//            // Make sure we listen on all possible mappings of the servlet
//            for (String mapping : getServletContext().getServletRegistration(getServletName()).getMappings()) {
//                final String commonPrefix = extractPrefixFromMapping(mapping);
//
//                String websocketPath =  commonPrefix + "/{server}/{session}/websocket";
//                ServerEndpointConfig sockJsConfig = ServerEndpointConfig.Builder
//                        .create(SockJsEndpoint.class, websocketPath)
//                        .configurator(configuratorFor(commonPrefix, false))
//                        .build();
//
//                String rawWebsocketPath = commonPrefix + "/websocket";
//                ServerEndpointConfig rawWsConfig = ServerEndpointConfig.Builder
//                        .create(RawWebsocketEndpoint.class, rawWebsocketPath)
//                        .configurator(configuratorFor(commonPrefix, true))
//                        .build();
//
//                ServerContainer serverContainer = (ServerContainer) getServletContext().getAttribute("javax.websocket.server.ServerContainer");
//                try {
//                    serverContainer.addEndpoint(sockJsConfig);
//                    serverContainer.addEndpoint(rawWsConfig);
//                } catch (DeploymentException ex) {
//                    throw new ServletException("Error deploying websocket endpoint:", ex);
//                }
//            }
//        }
//    }

    private String extractPrefixFromMapping(String mapping) {
        if (mapping.endsWith("*")) {
            mapping = mapping.substring(0, mapping.length() - 1);
        }
        if (mapping.endsWith("/")) {
            mapping = mapping.substring(0, mapping.length() - 1);
        }
        return mapping;
    }

    private ServerEndpointConfig.Configurator configuratorFor(final String prefix, final boolean isRaw) {
        return new ServerEndpointConfig.Configurator() {
            @Override
            public <T> T getEndpointInstance(Class<T> endpointClass) throws InstantiationException {
                try {
                    return endpointClass.getConstructor(SockJsServer.class, String.class, String.class)
                            .newInstance(sockJsServer, getServletContext().getContextPath(), prefix);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }

            @Override
            public void modifyHandshake(ServerEndpointConfig sec, HandshakeRequest request, HandshakeResponse response) {
                if (isRaw) {
                    // We have no reliable key (like session id) to save
                    // headers with for raw websocket requests
                    return;
                }
                String path = request.getRequestURI().getPath();
                Matcher matcher = SESSION_PATTERN.matcher(path);
                if (matcher.matches()) {
                    String sessionId = matcher.group(1);
                    saveHeaders(sessionId, request.getHeaders());
                }
            }
        };
    }

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
       
    	res.setHeader("Access-Control-Allow-Origin", "true");
    	//if(sockJsServer==null)
    	//initJSR356();
    	log.log(Level.FINE, "SockJsServlet#service for {0} {1}", new Object[] {req.getMethod(), req.getPathInfo()});
        AsyncContext asyncContext = req.startAsync();
        asyncContext.setTimeout(0); // no timeout
        SockJsServletRequest sockJsReq = new SockJsServletRequest(req);
        SockJsServletResponse sockJsRes = new SockJsServletResponse(res, asyncContext);
        try {
            sockJsServer.dispatch(sockJsReq, sockJsRes);
        } catch (SockJsException ex) {
            throw new ServletException("Error during SockJS request:", ex);
        }
        if ("application/x-www-form-urlencoded".equals(req.getHeader("Content-Type"))) {
            // Let the servlet parse data and just pretend like we did
            sockJsReq.onAllDataRead();
        } else if (req.isAsyncStarted()) {
            req.getInputStream().setReadListener(sockJsReq);
        }
    }

    @Override
    public void destroy() {
        super.destroy();
        sockJsServer.destroy();
    }

    static void saveHeaders(String sessionId, Map<String, List<String>> headers) {
        savedHeaders.put(sessionId, headers);
    }

    static Map<String, List<String>> retrieveHeaders(String sessionId) {
        return savedHeaders.remove(sessionId);
    }

    private SockJsServer sockJsServer;

    private static final Pattern SESSION_PATTERN = Pattern.compile(".*/.+/(.+)/websocket$");

    private static final Logger log = Logger.getLogger(SockJsServlet.class.getName());

    /**
     * Store a map of SockJS sessionId to header values from the upgrade
     * request since JSR-356 gives us no way to access this from Endpoints
     * directly. The MAX_INFLIGHT_HEADERS and LinkedHashMap#removeEldestEntry
     * are used to make sure any really misbehaving clients don't cause
     * entries to accumulate in the map. Under normal circumstances, an entry
     * is removed very shortly after it's added since we don't add it until
     * the handshake process is complete and remove it as soon as the
     * Endpoint's onOpen gets called.
     */
    private static final int MAX_INFLIGHT_HEADERS = 100;
    private static final Map<String, Map<String, List<String>>> savedHeaders = Collections.synchronizedMap(new LinkedHashMap<String,Map<String, List<String>>>() {
        @Override
        protected boolean removeEldestEntry(Map.Entry eldest) {
            return size() > MAX_INFLIGHT_HEADERS;
        }
    });
}
