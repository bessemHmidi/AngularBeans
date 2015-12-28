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
package angularBeans.util;

/**
 * This interface define the methods of a modelQuery
 * 
 * @author bessem
 */

public interface ModelQuery {

	/**
	 * 
	 * @param model
	 *            the front end service model to update
	 * @param value
	 *            : the value to update the model with
	 * @return the current ModelQuery object to set another query.
	 */
	public ModelQuery setProperty(String model, Object value);

	// Map<String, Object> getData();

	/**
	 * 
	 * @param arrayName
	 *            the front end service model array to update
	 * @param value
	 *            : the value to push to the array
	 * @return the current ModelQuery object to set another query.
	 */
	ModelQuery pushTo(String arrayName, Object value);

	/**
	 * 
	 * @param arrayName
	 *            the front end service model array to update
	 * @param value
	 *            : the value to remove from the array
	 * @return the current ModelQuery object to set another query.
	 */
	ModelQuery removeFrom(String arrayName, Object value);

	/**
	 * 
	 * @param arrayName
	 *            the front end service model array to update
	 * @param value
	 *            : the value to push to the array
	 * @return the current ModelQuery object to set another query.
	 * 'key' : represent the name of the property used to an equal() like test on
	 *      the JS side to remove a specific object from the array :
	 *      service.object.keyName.value===objectSentonTheRequest.keyName.
	 */
	ModelQuery removeFrom(String arrayName, Object value, String key);

	String getTargetServiceClass();

}
