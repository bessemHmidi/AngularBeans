package angularBeans.events;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import angularBeans.api.AngularBean;
import angularBeans.context.NGSessionScoped;
import angularBeans.realtime.RealTime;
import angularBeans.util.AngularBeansUtil;

import com.google.gson.JsonElement;

@AngularBean
@NGSessionScoped
public class RemoteEventBus {

	@Inject
	@AngularEvent
	Event<Object> ngEventBus;

	@Inject
	AngularBeansUtil util;

	@RealTime
	public void fire(NGEvent event) throws ClassNotFoundException {
		Object o = null;
		
		JsonElement element = util.parse(event.getData());

		JsonElement data = null;
		Class javaClass = null;

		try {
			data = element.getAsJsonObject();
			
			
			javaClass = Class.forName(event.getDataClass());
		} catch (Exception e) {
			data = element.getAsJsonPrimitive();
			if (event.getDataClass() == null)
				event.setDataClass("String");
			javaClass = Class.forName("java.lang." + event.getDataClass());

		}

		o = (util.deserialise(javaClass, data));

		ngEventBus.fire(o);

	}

}
