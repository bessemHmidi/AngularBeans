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
package angularBeans.context;

import static angularBeans.util.Constants.NG_SESSION_ATTRIBUTE_NAME;

import javax.servlet.annotation.WebListener;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;


/**
 * 
 * @author bessem
 *
 */
@WebListener
public class MainHTTPSessionListener implements HttpSessionListener {

	@Override
	public void sessionCreated(HttpSessionEvent se) {


		se.getSession().setAttribute(NG_SESSION_ATTRIBUTE_NAME,
				se.getSession().getId());
		
	}

	/**
	 * if the HTTP Session is destroyed, the NGSession will be destroyed too
	 */
	@Override
	public void sessionDestroyed(HttpSessionEvent se) {

		GlobalNGSessionContextsMapHolder.destroySession(String.valueOf(se
				.getSession().getAttribute(NG_SESSION_ATTRIBUTE_NAME)));
		SessionMapper.getSessionsMap().remove(se.getSession().getId());
		

	}
}
