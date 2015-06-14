package angularBeans.log;

import java.util.logging.Logger;

import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;

/**
 * <p>
 * Produces a CDI injectable Logger class
 * </p>
 * 
 * @author Aymen Naili
 */
public class LoggerProducer {
	
	@Produces
	public Logger produce(InjectionPoint injectionPoint){
		return Logger.getLogger(injectionPoint.getMember().getDeclaringClass().getSimpleName());
	}
	
}
