package snapdeal.mobileAutomation.exceptions;

public class LldNotFoundException extends Exception{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public String toString() {
		return "The LLD mentioned for this test case could not be found";
	}
}
