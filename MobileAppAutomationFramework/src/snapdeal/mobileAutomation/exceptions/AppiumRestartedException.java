package snapdeal.mobileAutomation.exceptions;

public class AppiumRestartedException extends Exception{

	/**
	 * 
	 */
	private static final long serialVersionUID = -4149698325974649903L;

	public AppiumRestartedException() {
		super();
	}
	
	/**
	 * Provides a description of the exception
	 */
	public String toString() {
		return "Appium re-started";
	}
}
