package angularBeans.util;

import angularBeans.context.NGSessionScoped;


@NGSessionScoped
public class CurrentNGSession {
	
	private String sessionId;

	public String getSessionId() {
		return sessionId;
	}

	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}

}
