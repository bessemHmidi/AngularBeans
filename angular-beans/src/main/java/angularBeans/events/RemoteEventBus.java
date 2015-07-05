package angularBeans.events;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import com.google.gson.Gson;

import angularBeans.api.AngularBean;
import angularBeans.context.NGSessionScoped;
import angularBeans.realtime.RealTime;
import angularBeans.remote.DataReceived;
import angularBeans.remote.DataReceivedEvent;
import angularBeans.util.AngularBeansUtil;


@AngularBean
@ApplicationScoped
public class RemoteEventBus {
	
	@Inject
	@AngularEvent
	Event<Object> ngEventBus;
	
	@Inject
	AngularBeansUtil util;
	
	@RealTime 
	public void fire(NGEvent event) throws ClassNotFoundException{		
		Object o=(util.deserialise(Class.forName(event.getDataClass())
				, util.parse(event.getData())));
	
		ngEventBus.fire(o);
		
	}
	
	


}
