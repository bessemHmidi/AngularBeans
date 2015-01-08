package org.espritjug.angularBeans.context;

import javax.servlet.annotation.WebListener;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import org.espritjug.angularBeans.Util;

@WebListener
public class HTTPSessionListener implements HttpSessionListener{

	@Override
	public void sessionCreated(HttpSessionEvent se) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void sessionDestroyed(HttpSessionEvent se) {
		GlobalMapHolder.destroySession(String.valueOf(se.getSession().getAttribute(Util.NG_SESSION_ATTRIBUTE_NAME)));
		
	}

}
