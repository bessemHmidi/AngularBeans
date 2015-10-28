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
package angularBeans.realtime;

import java.util.HashSet;
import java.util.Set;

import javax.enterprise.context.ApplicationScoped;

import org.projectodd.sockjs.SockJsConnection;

import angularBeans.context.SessionMapper;

/**
 * this is a holder for all sockJs opened sessions
 * 
 * @author Bassem Hmidi
 *
 */

@ApplicationScoped
public class GlobalConnectionHolder {

	Set<SockJsConnection> allConnections = new HashSet<SockJsConnection>();

	public Set<SockJsConnection> getAllConnections() {
		return allConnections;
	}

	public void removeConnection(String id) {
		
		
		for(SockJsConnection connection:new HashSet<>(allConnections)){
			String httpSessionId=SessionMapper.getHTTPSessionID(connection.id);
			if(httpSessionId!=null){
			if(httpSessionId.equals(id)){
			SessionMapper.getSessionsMap().remove(id);
			
			try {
				connection.destroy();
			} catch (Exception e) {
			
			}
		
			
			
			allConnections.remove(connection);
			}
			}
		}
		
		
	}

}
