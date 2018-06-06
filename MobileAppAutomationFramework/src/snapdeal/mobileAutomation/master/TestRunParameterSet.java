package snapdeal.mobileAutomation.master;

/**
 * This class represents a set of parameters for execution of a test case script
 * @author Khagesh Kapil
 * @see TestSuite
 */
public class TestRunParameterSet {
	
	/**
	 * Specifies Test Case Name
	 */
	private String testCaseName;
	
	/**
	 * Specifies flavour for this {@link TestCase}
	 */
	private String flavour;
	
	/**
	 * Specifies driver reset flag
	 */
	private boolean driverResetFlag;
	
	private String lld;
	
	private String testDataId;
	
	private String snapshotStrategy; 
	
	private boolean loggingEnabled;
	
	/**
	 * Getter method for {@link #testCaseName}
	 * @return
	 */
	public String getTestCaseName() {
		return testCaseName;
	}
	
	/**
	 * Setter method for {@link #testCaseName}
	 * @param testCaseName
	 */
	public void setTestCaseName(String testCaseName) {
		this.testCaseName = testCaseName;
	}
	
	/**
	 * Getter method for {@link #flavour}
	 * @return
	 */
	public String getFlavour() {
		return flavour;
	}
	
	/**
	 * Setter method for {@link #flavour}
	 * @param flavour
	 */
	public void setFlavour(String flavour) {
		this.flavour = flavour;
	}
	
	/**
	 * Getter method for {@link #driverResetFlag}
	 * @return
	 */
	public boolean getDriverResetFlag() {
		return driverResetFlag;
	}
	
	/**
	 * Setter method for {@link #driverResetFlag}
	 * @param driverResetFlag
	 */
	public void setDriverResetFlag(boolean driverResetFlag) {
		this.driverResetFlag = driverResetFlag;
	}
	
	public String getLld() {
		return lld;
	}

	public void setLld(String lld) {
		this.lld = lld;
	}
	
	public String getTestDataId() {
		return testDataId;
	}

	public void setTestDataId(String testDataId) {
		this.testDataId = testDataId;
	}

	public String getSnapshotStrategy() {
		return snapshotStrategy;
	}

	public void setSnapshotStrategy(String snapshotStrategy) {
		this.snapshotStrategy = snapshotStrategy;
	}
	
	public boolean getLoggingEnabled() {
		return loggingEnabled;
	}

	public void setLoggingEnabled(boolean loggingEnabled) {
		this.loggingEnabled = loggingEnabled;
	}
	

	public String toString() {
		return "[Test Case Name : "+getTestCaseName()+
				" Flavour : "+getFlavour()+
				" LLD : "+getLld()+
				" Test Data Id : "+getTestDataId()+
				" Snapshot Strategy : "+getSnapshotStrategy()+
				" Driver Reset Flag : "+getDriverResetFlag()+"]";
	}
	
}
