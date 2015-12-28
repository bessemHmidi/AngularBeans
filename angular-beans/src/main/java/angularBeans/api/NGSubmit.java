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
* specify 
* what other back end models  will be synchronized
* with front end models before the annotated method call
* <p> 
* backEndModels: array of models names or 
* ' backEndModels={"*"} ' to tell AngularBeans
* that all the @NGModel annotated properties
* of the back end bean will be updated.
* <p>
* <strong>work only with @NGModel annotated properties</strong>
* @author bessem hmidi
**/

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface NGSubmit {
	
	String[] backEndModels() default {};
	

}
