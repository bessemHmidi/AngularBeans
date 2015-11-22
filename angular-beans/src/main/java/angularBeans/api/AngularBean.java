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

package angularBeans.api;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Stereotype;
import javax.inject.Qualifier;

/**  
* Declare a class as an AngularBean that will be proxied via an auto generated
* AngularJS service.
* <p>
* this stereotype define by default a RequestScoped CDI Bean
* <p>
* compatibles scopes :
* <li> javax.enterprise.context.RequestScoped
* <li> javax.enterprise.context.ApplicationScoped
* <li> javax.enterprise.context.NGSessionScoped
* 
* @author bessem hmidi
**/ 

@Retention(RUNTIME)
@Target({TYPE, FIELD,PARAMETER })
@Qualifier
@Documented
//@RequestScoped
//@Stereotype
public @interface AngularBean {

}
