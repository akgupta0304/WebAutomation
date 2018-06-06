package snapdeal.mobileAutomation.master;

public class TestSuiteToken {
	public int tokenSequence;
	public String tokenSuite;
	public String tokenSuiteConfig;
	
	@Override
	public String toString() {
		return "\nsequence ="+tokenSequence+
				"\nsuite = "+tokenSuite+
				"\nconfig = "+tokenSuiteConfig;
	}
	
	/**
	 * @return the tokenSequence
	 */
	public int getTokenSequence() {
		return tokenSequence;
	}
	
	/**
	 * @param tokenSequence the tokenSequence to set
	 */
	public void setTokenSequence(int tokenSequence) {
		this.tokenSequence = tokenSequence;
	}
	
	/**
	 * @return the tokenSuite
	 */
	public String getTokenSuite() {
		return tokenSuite;
	}
	
	/**
	 * @param tokenSuite the tokenSuite to set
	 */
	public void setTokenSuite(String tokenSuite) {
		this.tokenSuite = tokenSuite;
	}
	
	/**
	 * @return the tokenSuiteConfig
	 */
	public String getTokenSuiteConfig() {
		return tokenSuiteConfig;
	}
	
	/**
	 * @param tokenSuiteConfig the tokenSuiteConfig to set
	 */
	public void setTokenSuiteConfig(String tokenSuiteConfig) {
		this.tokenSuiteConfig = tokenSuiteConfig;
	}
}
