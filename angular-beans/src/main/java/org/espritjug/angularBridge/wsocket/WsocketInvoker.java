package org.espritjug.angularBridge.wsocket;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.espritjug.angularBridge.context.NGSessionScopeContext;
import org.espritjug.angularBridge.remote.RemoteInvoker;
import org.espritjug.angularBridge.wsocket.annotations.WSocketReceiveEvent;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;


@Dependent
public class WsocketInvoker implements Serializable{
	
	
	
	@PostConstruct
	public void init(){
		
		
		
	}
	
	
	

	
	@Inject
	RemoteInvoker remoteInvoker;

	@Inject
	
	org.espritjug.angularBridge.context.BeanLocator locator;

	
	public  void process(@Observes @WSocketReceiveEvent WSocketEvent event){
		
	
	
		JsonObject jObj=event.getData();
		String UID=jObj.get("session").getAsString();
		String beanName=jObj.get("service").getAsString();
		String method=jObj.get("method").getAsString();
		long reqId=jObj.get("reqId").getAsLong();
		JsonObject paramsObj=jObj.get("params").getAsJsonObject();
		
		
		
		
		NGSessionScopeContext.setCurrentContext(UID);
		
		if(reqId==0)	
		{
			return;	
		}
		
		Object bean=locator.lookup(beanName,UID);
		
		
		
		remoteInvoker.wsInvoke(bean, method,paramsObj, event,reqId,UID);
		 
	
	}



	
}
