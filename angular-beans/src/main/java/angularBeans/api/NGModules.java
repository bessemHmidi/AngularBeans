package angularBeans.api;


import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**  
* Declare an NG dependency Module 
* <p> 
* the AngularJS Module "angularBeans"
* can need some extra module dependencies 
* so this annotation declare a table of modules
*  names (ofc they need to be also added as 
* js dependencies.
* <strong>used only with @NGAPP annotation
**/

@Retention(RUNTIME)
@Target({TYPE})
public @interface NGModules {
	String[] value() default {};
}
