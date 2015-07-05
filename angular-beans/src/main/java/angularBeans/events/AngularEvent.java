package angularBeans.events;

import javax.enterprise.inject.Stereotype;
import javax.inject.Qualifier;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.PARAMETER;

import java.lang.annotation.Target;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.ElementType.METHOD;



@Target({TYPE, METHOD, FIELD, PARAMETER})
@Retention(RUNTIME)
@Qualifier
public @interface AngularEvent {

}
