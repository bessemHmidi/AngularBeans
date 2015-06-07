/*
 * AngularBeans, CDI-AngularJS bridge 
 *
 * Copyright (c) 2014, Bessem Hmidi. or third-party contributors as
 * indicated by the @author tags or express copyright attribution
 * statements applied by the authors.
 *
 * This copyrighted material is made available to anyone wishing to use, modify,
 * copy, or redistribute it subject to the terms and conditions of the GNU
 * Lesser General Public License, as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License
 * for more details.
 *
 */

/**
 @author Bessem Hmidi
 */
package angularBeans.api;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
/**  
* Specify main return mapped name
* and models to update (back->front)
* <p> 
* model: main return name
* can be very useful with angularBeans.bind(..) method
* to access the return without handling 
* the promise (with .then(..) at the JS side )
* 
* updates : beside the main return you can specify 
* an array of names of other front end models that
* will be synchronized with 
* back ends models at the end of the method call
* (with ' updates={"*"} ' all models will be updated)
* 
* <strong>work only with @NGModel annotated properties
**/

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface NGReturn {

	String model() default "";
	String[] updates() default {};
}
