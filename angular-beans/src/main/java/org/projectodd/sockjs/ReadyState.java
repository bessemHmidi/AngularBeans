package org.projectodd.sockjs;

import static angularBeans.util.Constants.THREE;
import static angularBeans.util.Constants.TWO;

/**
 * Enum class of connecting states.
 * 
 * @author melbehi
 *
 */
public enum ReadyState {
	
	/**
	 * CONNECTING state with code 0.
	 */
	CONNECTING(0),
    
	/**
     * OPEN state with code 1.
     */
    OPEN(1),
    
    /**
     * CLOSING state with code 2.
     */
    CLOSING(TWO),
   
    /**
     * CLOSED state with code 3.
     */
    CLOSED(THREE);
	
	/**
	 * Int value of status code.
	 */
	private final int code;
	
	/**
	 * Constructor.
	 * 
	 * @param code
	 */
	ReadyState(int code){
		this.code = code;
	}
	
	/**
	 * Returns the status code.
	 * 
	 * @return code.
	 */
	public int code(){
		return code;
	}
}
