package angularBeans.util;

/**
 * Collected constants of general utility.
 * <P>All members are immutable.
 * 
 * @author melbehi
 *
 */
public class Constants {
	/*
	 * Both 0 and 1 is primitive values 
	 * and do not need to be declared in this class.
	 * 
	 * Otherwise, please add your immutable values below.
	 */
	
	/**
	 * Int value of 2.
	 */
	public static final int TWO = Integer.valueOf(2);
	
	/**
	 * Int value of 3.
	 */
	public static final int THREE = Integer.valueOf(3);
	
	/**
	 * The NG_SESSION identifier.
	 */
	public static final String NG_SESSION_ATTRIBUTE_NAME = "NG_SESSION_ID";
	
	/**
	 * Boolean getter method prefix.
	 */
	public static final String IS = "is";
	/**
	 * Setter method prefix.
	 */
	public static final String SET ="set";
	/**
	 * Getter method prefix.
	 * <b>or</b>
	 * Http GET method.
	 */
	public static final String GET = "get";
	
	/**
	 * Http POST method.
	 */
	public static final String POST = "post";
	
	/**
	 * Http PUT method.
	 */
	public static final String PUT = "put";
	
	/**
	 * Http DELETE method.
	 */
	public static final String DELETE = "delete";
	
	/**
	 * Http DELETE_TO_REPLACE_BY_JUST_DELETE method.
	 */
	public static final String DELETE_TO_REPLACE = "delete_to_replace_by_just_delete";

}
