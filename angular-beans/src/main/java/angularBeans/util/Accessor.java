package angularBeans.util;

/**
 * Class enum for accessor prefix.
 * 
 * @author melbehi
 *
 */
public enum Accessor {

	/**
	 * Getter method prefix.
	 */
	GET("get"),

	/**
	 * Boolean Getter prefix.
	 */
	IS("is"),

	/**
	 * Setter prefix.
	 */
	SET("set");

	/**
	 * Prefix string.
	 */
	private final String prefix;

	/**
	 * Constructor.
	 * 
	 * @param prefix
	 */
	Accessor(String prefix) {
		this.prefix = prefix;
	}

	/**
	 * Returns the prefix string.
	 * 
	 * @return {@link String} prefix
	 */
	public String prefix() {
		return prefix;
	}
}
