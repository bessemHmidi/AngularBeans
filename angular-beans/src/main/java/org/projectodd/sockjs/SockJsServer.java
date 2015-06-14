/**
 * Copyright (C) 2014 Red Hat, Inc, and individual contributors.
 * Copyright (C) 2011-2012 VMware, Inc.
 */

package org.projectodd.sockjs;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

/**
 * The main entry point for handling SockJS connections.
 *
 * Typically a SockJsServer is created, its {@link #options} set, an
 * {@link #onConnection} handler added, and the server is then passed to a
 * SockJsServlet to handle the routing of requests.
 */
public class SockJsServer {

    public SockJsServer() {
    }

    public void init() {
        // Set everything up here instead of the constructor so that we have a chance to set options
        appHandler = new AppHandler(this);
        webHandler = new WebHandler(this);
        iframeHandler = new IframeHandler(this);
        chunkingHandler = new ChunkingHandler(this);
        websocketHandler = new WebsocketHandler(this);
        jsonpHandler = new JsonpHandler(this);
        xhrHandler = new XhrHandler(this);
        eventsourceHandler = new EventsourceHandler(this);
        htmlfileHandler = new HtmlfileHandler(this);

        dispatcher = new Dispatcher(appHandler.handle404, webHandler.handle405, webHandler.handleError);
        dispatcher.push("GET", p(""), appHandler.welcomeScreen);
        dispatcher.push("GET", p("/iframe[0-9-.a-z_]*.html"), iframeHandler.iframe,
                webHandler.cacheFor, webHandler.expose);
        dispatcher.push("OPTIONS", p("/info"), optsFilters(chunkingHandler.infoOptions));
        dispatcher.push("GET", p("/info"), xhrHandler.xhrCors, webHandler.hNoCache,
                chunkingHandler.info, webHandler.expose);
        dispatcher.push("GET", p("/websocket"), websocketHandler.rawWebsocket);
        dispatcher.push("GET", t("/jsonp"), appHandler.hSid, webHandler.hNoCache, jsonpHandler.jsonp);
        dispatcher.push("POST", t("/jsonp_send"), appHandler.hSid, webHandler.hNoCache, webHandler.expectForm, jsonpHandler.jsonpSend);
        dispatcher.push("POST", t("/xhr"), appHandler.hSid, webHandler.hNoCache, xhrHandler.xhrCors, xhrHandler.xhrPoll);
        dispatcher.push("OPTIONS", t("/xhr"), optsFilters());
        dispatcher.push("POST", t("/xhr_send"), appHandler.hSid, webHandler.hNoCache, xhrHandler.xhrCors, webHandler.expectXhr, xhrHandler.xhrSend);
        dispatcher.push("OPTIONS", t("/xhr_send"), optsFilters());
        dispatcher.push("POST", t("/xhr_streaming"), appHandler.hSid, webHandler.hNoCache, xhrHandler.xhrCors, xhrHandler.xhrStreaming);
        dispatcher.push("OPTIONS", t("/xhr_streaming"), optsFilters());
        dispatcher.push("GET", t("/eventsource"), appHandler.hSid, webHandler.hNoCache, eventsourceHandler.eventsource);
        dispatcher.push("GET", t("/htmlfile"), appHandler.hSid, webHandler.hNoCache, htmlfileHandler.htmlfile);

        if (options.websocket) {
            dispatcher.push("GET", t("/websocket"), websocketHandler.sockjsWebsocket);
        } else {
            dispatcher.push("GET", t("/websocket"), webHandler.cacheFor, appHandler.disabledTransport);
        }

        scheduledExecutor = Executors.newScheduledThreadPool(Runtime.getRuntime().availableProcessors(), new ThreadFactory() {
            @Override
            public Thread newThread(Runnable r) {
                Thread thread = Executors.defaultThreadFactory().newThread(r);
                // Mark as a daemon thread so we never prevent shutdown
                thread.setDaemon(true);
                return thread;
            }
        });
    }

    public void destroy() {
        scheduledExecutor.shutdownNow();
    }

    public void dispatch(SockJsRequest req, SockJsResponse res) throws SockJsException {
        dispatcher.dispatch(req, res);
    }

    protected String p(String match) {
        return "^" + match + "[/]?$";
    }

    protected String[] t(String match) {
        String pattern = p("/([^/.]+)/([^/.]+)" + match);
        return new String[] { pattern, "server", "session" };
    }

    protected DispatchFunction[] optsFilters() {
        return optsFilters(xhrHandler.xhrOptions);
    }
    protected DispatchFunction[] optsFilters(DispatchFunction optionsFilter) {
        return new DispatchFunction[] { appHandler.hSid, xhrHandler.xhrCors, webHandler.cacheFor, optionsFilter, webHandler.expose };
    }

    /**
     * Handle incoming connections - a SockJsServer isn't very useful
     * unless you set an OnConnectionHandler here.
     * 
     * @param handler The handler to call when a new connection is established
     */
    public void onConnection(OnConnectionHandler handler) {
        onConnectionHandler = handler;
    }

    public void emitConnection(SockJsConnection connection) {
        if (onConnectionHandler != null) {
            onConnectionHandler.handle(connection);
        }
    }

    public ScheduledFuture setTimeout(Runnable callback, long delay) {
        return scheduledExecutor.schedule(callback, delay, TimeUnit.MILLISECONDS);
    }

    public void clearTimeout(ScheduledFuture future) {
        future.cancel(false);
    }

    private Dispatcher dispatcher;
    private AppHandler appHandler;
    private WebHandler webHandler;
    private IframeHandler iframeHandler;
    private ChunkingHandler chunkingHandler;
    private WebsocketHandler websocketHandler;
    private JsonpHandler jsonpHandler;
    private XhrHandler xhrHandler;
    private EventsourceHandler eventsourceHandler;
    private HtmlfileHandler htmlfileHandler;
    private ScheduledExecutorService scheduledExecutor;
    private OnConnectionHandler onConnectionHandler;
    public Options options = new Options();

    public static class Options {
        /**
         * Transports which don't support cross-domain communication natively
         * ('eventsource' to name one) use an iframe trick. A simple page is
         * served from the SockJS server (using its foreign domain) and is
         * placed in an invisible iframe. Code run from this iframe doesn't
         * need to worry about cross-domain issues, as it's being run from a
         * domain local to the SockJS server. This iframe also needs to load
         * the SockJS javascript client library, and this option lets you
         * specify its url (if you're unsure, point it to the latest minified
         * SockJS client release, this is the default). You must explicitly
         * specify this url on the server side for security reasons - we don't
         * want the possibility of running any foreign javascript within the
         * SockJS domain (aka cross site scripting attack). Also, the sockjs
         * javascript library is probably already cached by the browser - it
         * makes sense to reuse the sockjs url you're normally using.
         */
        public String sockjsUrl = "http://cdn.sockjs.org/sockjs-0.3.min.js";

        /**
         * Most streaming transports save responses on the client side and
         * don't free memory used by delivered messages. Such transports need
         * to be garbage-collected once in a while. `responseLimit` sets a
         * maximum number of bytes that can be send over a single http
         * streaming request before it will be closed. After that client
         * needs to open new request. Setting this value to one effectively
         * disables streaming and will make streaming transports to behave
         * like polling transports. The default value is 128K.
         */
        public int responseLimit = 128 * 1024;

        /**
         * Some load balancers don't support websockets. This option can be
         * used to disable websockets support by the server. By default
         * websockets are enabled.
         */
        public boolean websocket = true;

        /**
         * Some hosting providers enable sticky sessions only to requests
         * that have a JSESSIONID cookie set. This setting controls if the
         * server should set this cookie to a dummy value. By default setting
         * of a JSESSIONID cookie is disabled.
         */
        public boolean jsessionid = false;

        /**
         * In order to keep proxies and load balancers from closing long
         * running http requests we need to pretend that the connection is
         * active and send a heartbeat packet once in a while. This setting
         * controls how often this is done. By default a heartbeat packet
         * is sent every 25 seconds.
         */
        public int heartbeatDelay = 25000;

        /**
         * The server sends a `close` event when a client receiving
         * connection has not been seen for a while. This delay is
         * configured by this setting. By default the `close` event will
         * be emitted when a receiving connection wasn't seen for 5 seconds.
         */
        public int disconnectDelay = 5000;

        /**
         * Users can specify a base URL which all client requests after an
         * initial info request will be made against. This probably is more
         * useful as a method and not a static string, but that's not
         * implemented yet.
         */
        public String baseUrl = null;

        // sockjs-node exposes some options as callback functions
        // TODO: Add jsessionidCallback
        // TODO: Add baseUrlCallback
    }

    public static interface OnConnectionHandler {
        public void handle(SockJsConnection connection);
    }
}
