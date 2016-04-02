package org.projectodd.sockjs.servlet;

import static angularBeans.util.Accessor.GET;

import java.util.List;
import java.util.Map;

import javax.websocket.Session;

import org.projectodd.sockjs.SockJsRequest;

public class SockJsWebsocketRequest extends SockJsRequest {

	public SockJsWebsocketRequest(Session session, String contextPath, String prefix,
			Map<String, List<String>> headers) {
		this.session = session;
		this.contextPath = contextPath;
		this.prefix = prefix;
		this.headers = headers;
	}

	@Override
	public String getMethod() {
		// Let's just pretend they're all GETs
		return GET.prefix();
	}

	@Override
	public String getUrl() {
		return session.getRequestURI().toString();
	}

	@Override
	public String getPath() {
		String path = session.getRequestURI().getPath();
		if (path == null) {
			return null;
		}
		if (contextPath.length() > 1 && path.startsWith(contextPath)) {
			path = path.substring(contextPath.length());
		}
		if (prefix.length() > 0 && path.startsWith(prefix)) {
			path = path.substring(prefix.length());
		}
		return path;
	}

	@Override
	public String getPrefix() {
		String sockjsPrefix = contextPath + prefix;
		return sockjsPrefix.equals("") ? "/" : sockjsPrefix;
	}

	@Override
	public String getRemoteAddr() {
		// TODO: grab this during the handshake process
		return null;
	}

	@Override
	public int getRemotePort() {
		return 0;
	}

	@Override
	public String getHeader(String name) {
		for (String header : headers.keySet()) {
			if (name.equalsIgnoreCase(header)) {
				List<String> values = headers.get(header);
				if (values != null && values.size() > 0) {
					return values.get(0);
				}
			}
		}
		return null;
	}

	@Override
	public String getContentType() {
		return null;
	}

	@Override
	public String getCookie(String name) {
		return null;
	}

	@Override
	public String getQueryParameter(String name) {
		List<String> paramValues = session.getRequestParameterMap().get(name);
		if (paramValues != null && paramValues.size() > 0) {
			return paramValues.get(0);
		}
		return null;
	}

	private final Session session;
	private final String contextPath;
	private final String prefix;
	private final Map<String, List<String>> headers;
}
