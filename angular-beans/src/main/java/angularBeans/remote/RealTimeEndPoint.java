/*
 * AngularBeans, CDI-AngularJS bridge 
 *
 * Copyright (c) 2014, Bessem Hmidi. or third-party contributors as
 * indicated by the @author tags or express copyright attribution
 * statements applied by the authors.
 *
 * This copyrighted material is made available to anyone wishing to use, modify,
 * copy, or redistribute it subject to the terms and conditions of the GNU
 * Lesser General Public License, as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License
 * for more details.
 *
 */

/**
 @author Bessem Hmidi
 */
package angularBeans.remote;

import java.util.logging.Logger;

import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;

import org.projectodd.sockjs.SockJsConnection;
import org.projectodd.sockjs.SockJsServer;
import org.projectodd.sockjs.servlet.SockJsServlet;

import angularBeans.context.NGSessionScopeContext;
import angularBeans.realtime.GlobalConnectionHolder;
import angularBeans.realtime.RealTimeErrorEvent;
import angularBeans.realtime.RealTimeSessionCloseEvent;
import angularBeans.realtime.RealTimeSessionReadyEvent;
import angularBeans.util.AngularBeansUtil;

import com.google.gson.JsonObject;

@WebServlet(loadOnStartup = 1, asyncSupported = true, urlPatterns = "/rt-service/*")
public class RealTimeEndPoint extends SockJsServlet {

	@Inject
	@DataReceivedEvent
	private Event<DataReceived> receiveEvents;

	@Inject
	@RealTimeSessionReadyEvent
	private Event<RealTimeDataReceiveEvent> sessionOpenEvent;

	@Inject
	@RealTimeSessionCloseEvent
	private Event<RealTimeDataReceiveEvent> sessionCloseEvent;

	@Inject
	@RealTimeErrorEvent
	private Event<RealTimeDataReceiveEvent> errorEvent;

	@Inject
	GlobalConnectionHolder globalConnectionHolder;

	// @OnClose
	// public void onclose(Session session) {
	// sessionCloseEvent.fire(new WSocketEvent(session, null));
	// Logger.getLogger("AngularBeans").info("ws-channel closed");
	// }
	//
	// @OnError
	// public void onError(Session session, Throwable error) {
	// // errorEvent.fire(new WSocketEvent(session,
	// // Util.parse(Util.getJson(error))));
	// error.printStackTrace();
	// }
	//

	@Override
	public void init() throws ServletException {
		SockJsServer server = new SockJsServer();
		// Various options can be set on the server, such as:
		// echoServer.options.responseLimit = 4 * 1024;

		// onConnection is the main entry point for handling SockJS connections
		server.onConnection(new SockJsServer.OnConnectionHandler() {
			@Override
			public void handle(final SockJsConnection connection) {
				getServletContext().log("SockJS client connected");
				

				// onData gets called when a client sends data to the server
				connection.onData(new SockJsConnection.OnDataHandler() {
					@Override
					public void handle(String message) {

						JsonObject jObj = AngularBeansUtil.parse(message);
						String UID = jObj.get("session").getAsString();

						
						
						
						RealTimeDataReceiveEvent ev = new RealTimeDataReceiveEvent(connection,
								jObj);

						ev.setConnection(connection);
						ev.setSessionId(UID);
						NGSessionScopeContext.setCurrentContext(UID);

						String service = jObj.get("service").getAsString();

						if (service.equals("ping")) {

							sessionOpenEvent.fire(ev);
							Logger.getLogger("AngularBeans").info(
									"AngularBeans-client: " + UID);

						} else {

							receiveEvents.fire(ev);
						}

						// connection.write(message);
					}
				});

				// onClose gets called when a client disconnects
				connection.onClose(new SockJsConnection.OnCloseHandler() {
					@Override
					public void handle() {
						// globalConnectionHolder.getAllConnections().add(connection);
						getServletContext().log("SockJS client disconnected");
					}
				});
			}
		});

		setServer(server);
		// Don't forget to call super.init() to wire everything up
		super.init();

	}

}
