package angularBeans.api.http;

/**
 * Enum class of Http methods.
 * 
 * @author melbehi
 */
public enum HttpMethod {

	/**
	 * Http GET method.
	 */
	GET("get"),

	/**
	 * Http POST method.
	 */
	POST("post"),

	/**
	 * Http PUT method.
	 */
	PUT("put"),

	/**
	 * Http DELETE method.
	 */
	DELETE("delete"),

	/**
	 * Http DELETE_TO_REPLACE_BY_JUST_DELETE method.
	 */
	DELETE_TO_REPLACE("delete_to_replace_by_just_delete");

	/**
	 * 
	 */
	private final String method;

	/**
	 * Constructor.
	 * 
	 * @param method
	 */
	HttpMethod(String method) {
		this.method = method;
	}

	/**
	 * Returns the method name.
	 * 
	 * @return method
	 */
	public String method() {
		return method;
	}
}
