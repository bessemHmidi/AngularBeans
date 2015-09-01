package angularBeans.demoApp.ngbeans;

import java.io.Serializable;

import javax.inject.Inject;

import angularBeans.api.AngularBean;
import angularBeans.context.NGSessionScoped;
import angularBeans.events.RealTimeMessage;
import angularBeans.realtime.RealTime;
import angularBeans.realtime.RealTimeClient;


@AngularBean
@NGSessionScoped
public class CanvasService implements Serializable{
	
	@Inject
	RealTimeClient client;
	
	@RealTime
	public void notifyAllCanvas(double lastX, double lastY, double currentX
			, double currentY,String color){
		
		RealTimeMessage message=
				new RealTimeMessage()
		.set("color", color)
		.set("lastX", lastX)
		.set("lastY", lastY)
		.set("currentX", currentX)
		.set("currentY", currentY);

		client.broadcast("drawEvent", message, true);
	
	} 
	

}
