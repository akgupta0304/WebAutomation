package snapdeal.mobileAutomation.exceptions;

import snapdeal.mobileAutomation.master.Platform;
import snapdeal.mobileAutomation.master.TestCase;

/**
 * This Class defines an exception which is thrown when a test case attempts to execute on a platform for which it has not been implemented 
 * @author Khagesh Kapil
 */
public class PlatformNotSupportedException extends Exception{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * Name of the test case which attempts execution
	 * @see TestCase
	 */
	String testCase;
	
	/**
	 * Name of the platform for which the test case attempts execution
	 * @see Platform
	 */
	String platform;
	
	/**
	 * Constructor for this Exception
	 * @param testCase Name of the testCase attempting execution
	 * @param platform Name of the platform for which the test case attempts execution
	 */
	public PlatformNotSupportedException(String testCase,String platform) {
		super();
		this.testCase = testCase;
		this.platform = platform;
	}
	
	/**
	 * Provides a description of the exception
	 */
	@Override
	public String toString() {
		return "The Testcase \""+testCase+"\" does not support the platform \""+platform+"\"";
	}
}