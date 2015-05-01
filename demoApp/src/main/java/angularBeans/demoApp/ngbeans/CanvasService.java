package angularBeans.demoApp.ngbeans;

import javax.inject.Inject;

import angularBeans.api.AngularBean;
import angularBeans.context.NGSessionScoped;
import angularBeans.realtime.RealTime;
import angularBeans.realtime.RealTimeClient;
import angularBeans.realtime.RealTimeMessage;


@AngularBean
@NGSessionScoped
public class CanvasService {
	
	@Inject
	RealTimeClient client;
	
	@RealTime
	public void notifyAllCanvas(double lastX, double lastY, double currentX
			, double currentY){
		
		RealTimeMessage message=
				new RealTimeMessage()
		.set("lastX", lastX)
		.set("lastY", lastY)
		.set("currentX", currentX)
		.set("currentY", currentY);
		
		
		
		client.broadcast("drawEvent", message, true);
		
		//System.out.println("x: " +x+" y: "+y);
	} 
	

}
