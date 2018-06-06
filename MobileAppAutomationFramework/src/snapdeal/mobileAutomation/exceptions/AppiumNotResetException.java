package snapdeal.mobileAutomation.exceptions;

/**
 * This Class defines an exception which is thrown when a script tries to reset appium by killing its previously running instance
 * But this process results in some error
 * @author Khagesh Kapil
 *
 */
public class AppiumNotResetException extends Exception{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * Default Constructor
	 */
	public AppiumNotResetException() {
		super();
	}
	
	/**
	 * Provides a description of the exception
	 */
	public String toString() {
		return "Could not close the already running instance of appium";
	}
}