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
package angularBeans.validation;

import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import angularBeans.context.NGSessionScoped;

@NGSessionScoped
public class BeanValidator {

	
	public Set validate(Object bean){
		
		ValidatorFactory vf=Validation.buildDefaultValidatorFactory();
				
		
		Validator validator = vf.getValidator();
	      Set<ConstraintViolation<Object>> errors = validator.validate(bean);
		
	      
	      for(ConstraintViolation<Object> violation : errors){
	    	  System.out.println((violation.getInvalidValue())+":"+violation.getMessage());
	         // System.out.println(violation.getClass());
	      }
	      
	     return errors;
		
	}
	
	
}
