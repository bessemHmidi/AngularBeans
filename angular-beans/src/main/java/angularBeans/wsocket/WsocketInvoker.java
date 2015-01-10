package angularBeans.wsocket;

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
public class WsocketInvoker implements Serializable{
	
	
	
	@PostConstruct
	public void init(){
		
		
		
	}
	
	
	

	
	@Inject
	RemoteInvoker remoteInvoker;

	@Inject
	
	BeanLocator locator;

	
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
