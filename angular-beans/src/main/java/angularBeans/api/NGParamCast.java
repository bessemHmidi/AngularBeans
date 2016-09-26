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

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * specify the class type that will be used to make the Cast in JSON for case of Generic Class Implements
 * <p>
 * 
 * param: array names where you can set the index of the parameter, if not defined, the index is zero. 
 * required: if not found NGParamType implemented in class, raises an error, if not cast to parameter defined in the method 
 *  Ex. 
 *  
 *  ...
 *  @NGParamCast(param = "ngEntity{0}", required = true)
 *  public <V extends Entity> List<V> getEntities(V entity) {
 *  ...
 *  
 * @author Osni Marin
 **/

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface NGParamCast {

   String[] param() default "";

   boolean required() default false;
}
