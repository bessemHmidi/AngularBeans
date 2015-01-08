package org.espritjug.angularBridge.validation;

import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import org.espritjug.angularBridge.context.NGSessionScoped;

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
