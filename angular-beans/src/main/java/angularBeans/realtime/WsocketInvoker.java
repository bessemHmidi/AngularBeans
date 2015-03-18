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
package angularBeans.realtime;

import java.io.Serializable;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import angularBeans.context.BeanLocator;
import angularBeans.context.NGSessionScopeContext;
import angularBeans.remote.RemoteInvoker;
import angularBeans.wsocket.annotations.WSocketReceiveEvent;

import com.google.gson.JsonObject;

@Dependent
public class WsocketInvoker implements Serializable {

	@PostConstruct
	public void init() {

	}

	@Inject
	RemoteInvoker remoteInvoker;

	@Inject
	BeanLocator locator;

	public void process(@Observes @WSocketReceiveEvent WSocketEvent event) {

		JsonObject jObj = event.getData();
		String UID = jObj.get("session").getAsString();
		String beanName = jObj.get("service").getAsString();
		String method = jObj.get("method").getAsString();
		long reqId = jObj.get("reqId").getAsLong();
		JsonObject paramsObj = jObj.get("params").getAsJsonObject();

		NGSessionScopeContext.setCurrentContext(UID);

		if (reqId == 0) {
			return;
		}

		Object bean = locator.lookup(beanName, UID);

		remoteInvoker.wsInvoke(bean, method, paramsObj, event, reqId, UID);

	}

}
