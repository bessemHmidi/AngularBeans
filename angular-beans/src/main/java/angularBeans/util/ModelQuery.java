package angularBeans.util;

/**
 * 
 * @author bessem the interface define the methods of a modelQuery
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
	 * @key : represent the name of the property used to an equal() like test on
	 *      the JS side to remove a specific object from the array :
	 *      service.object.keyName.value===objectSentonTheRequest.keyName.
	 */
	ModelQuery removeFrom(String arrayName, Object value, String key);

}
