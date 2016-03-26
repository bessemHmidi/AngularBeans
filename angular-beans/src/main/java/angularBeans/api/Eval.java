package angularBeans.api;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import angularBeans.enums.Callback;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Eval {


	Callback value() default Callback.AFTER_SESSION_READY;


}
