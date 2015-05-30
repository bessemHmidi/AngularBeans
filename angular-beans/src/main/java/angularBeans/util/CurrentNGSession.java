package angularBeans.util;

import java.io.Serializable;

import angularBeans.context.NGSessionScoped;


@NGSessionScoped
public class CurrentNGSession implements Serializable {
	
	private String sessionId;

	public String getSessionId() {
		return sessionId;
	}

	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}

}
