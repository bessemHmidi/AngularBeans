
/* AngularBeans, CDI-AngularJS bridge Copyright (c) 2014, Bessem Hmidi. or third-party contributors as indicated by
 * the @author tags or express copyright attribution statements applied by the authors. This copyrighted material is
 * made available to anyone wishing to use, modify, copy, or redistribute it subject to the terms and conditions of the
 * GNU Lesser General Public License, as published by the Free Software Foundation. This program is distributed in the
 * hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details. */
package angularBeans.realtime;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebListener;
import javax.websocket.DeploymentException;
import javax.websocket.HandshakeResponse;
import javax.websocket.server.HandshakeRequest;
import javax.websocket.server.ServerContainer;
import javax.websocket.server.ServerEndpointConfig;

import org.projectodd.sockjs.SockJsServer;
import org.projectodd.sockjs.servlet.RawWebsocketEndpoint;
import org.projectodd.sockjs.servlet.SockJsEndpoint;

import angularBeans.js.cache.DefaultStaticJsCacheLoader;
import angularBeans.js.cache.StaticJsCacheFactory;
import angularBeans.util.ClosureCompiler;

/**
 * this listener:
 * <p>
 * initialize the sockJs server end point
 * <p>
 * 
 * @author Bessem Hmidi
 */

@WebListener
public class AngularBeansServletContextListener implements ServletContextListener {

	public static SockJsServer sockJsServer;
	private static final Pattern SESSION_PATTERN = Pattern.compile(".*/.+/(.+)/websocket$");

	ClosureCompiler compiler = new ClosureCompiler();
	ServletContext context;

	@Override
	public void contextInitialized(ServletContextEvent servletContextEvent) {
		context = servletContextEvent.getServletContext();

		try {
			if (sockJsServer == null) {
				initJSR356();
			}
		} catch (ServletException e) {
			e.printStackTrace();
		}

		StaticJsCacheFactory jsCacheFactory = new StaticJsCacheFactory(DefaultStaticJsCacheLoader.class);
		jsCacheFactory.BuildStaticJsCache();
	}

	@Override
	public void contextDestroyed(ServletContextEvent servletContextEvent) { }

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
							.newInstance(sockJsServer, context.getContextPath(), prefix);
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			}

			@Override
			public void modifyHandshake(ServerEndpointConfig sec, HandshakeRequest request,
					HandshakeResponse response) {
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

	private static final int MAX_INFLIGHT_HEADERS = 100;
	private static final Map<String, Map<String, List<String>>> savedHeaders =
			Collections.synchronizedMap(new LinkedHashMap<String, Map<String, List<String>>>() {

				@Override
				protected boolean removeEldestEntry(Map.Entry eldest) {
					return size() > MAX_INFLIGHT_HEADERS;
				}
			});

	static void saveHeaders(String sessionId, Map<String, List<String>> headers) {
		savedHeaders.put(sessionId, headers);
	}

	public static Map<String, List<String>> retrieveHeaders(String sessionId) {
		return savedHeaders.remove(sessionId);
	}

	public void initJSR356() throws ServletException {
		sockJsServer = new SockJsServer();
		sockJsServer.init();

		if (sockJsServer.options.websocket) {

			// Make sure we listen on all possible mappings of the servlet
			// for (String mapping :
			// getServletContext().getServletRegistration(context.getServletName()).getMappings())
			// {
			final String commonPrefix = extractPrefixFromMapping("/rt-service/*");

			String websocketPath = commonPrefix + "/{server}/{session}/websocket";//
			ServerEndpointConfig sockJsConfig = ServerEndpointConfig.Builder.create(SockJsEndpoint.class, websocketPath)
					.configurator(configuratorFor(commonPrefix, false)).build();
			// rt-service/websocket
			String rawWebsocketPath = commonPrefix + "/websocket";

			ServerEndpointConfig rawWsConfig =
					ServerEndpointConfig.Builder.create(RawWebsocketEndpoint.class, rawWebsocketPath)
							.configurator(configuratorFor(commonPrefix, true)).build();

			ServerContainer serverContainer =
					(ServerContainer) context.getAttribute("javax.websocket.server.ServerContainer");
			try {
				serverContainer.addEndpoint(sockJsConfig);
				serverContainer.addEndpoint(rawWsConfig);

				Logger.getLogger(this.getClass().getSimpleName())
						.info("deployement of programmatic Web socket EndPoint :" + rawWebsocketPath);
			} catch (DeploymentException ex) {
				throw new ServletException("Error deploying websocket endpoint:", ex);
			}

		}
	}
}
