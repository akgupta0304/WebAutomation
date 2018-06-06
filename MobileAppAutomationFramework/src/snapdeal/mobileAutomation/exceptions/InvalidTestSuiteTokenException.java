package snapdeal.mobileAutomation.exceptions;

import snapdeal.mobileAutomation.master.TestSuiteToken;

public class InvalidTestSuiteTokenException extends Exception{

	/**
	 * 
	 */
	private static final long serialVersionUID = 4328696659081475710L;

	private TestSuiteToken errorToken;
	
	public InvalidTestSuiteTokenException(TestSuiteToken tst) {
		errorToken = tst;
	}
	
	@Override
	public String toString() {
		return "Error processing Token : "+errorToken;
	}
}