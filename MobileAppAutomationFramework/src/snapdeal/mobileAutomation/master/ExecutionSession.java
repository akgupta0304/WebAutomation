package snapdeal.mobileAutomation.master;

import java.net.UnknownHostException;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import org.openqa.selenium.WebDriver;

import snapdeal.mobileAutomation.master.TestRunParameterSet;
import snapdeal.mobileAutomation.master.TestSuite;

/**
 * This class is used to maintain various sessions related to the current test suite execution
 * @author Khagesh Kapil
 * @see TestSuite
 */
public class ExecutionSession extends Session{

	/**
	 * A reference to {@link DriverFactorySession} to maintain a session of Driver Factory Object for the current execution 
	 */
	private DriverFactorySession driverFactorySession;
	
	/**
	 * A reference to {@link ReportingSession} to maintain a session of Execution Driver Object for the current execution
	 */
	private ReportingSession reportingSession;
	
	/**
	 * A reference to {@link ServiceApiSession} to maintain a session of API execution parameters for the current execution
	 */
	private ServiceApiSession serviceApiSession;
	
	/**
	 * A reference to {@link ServiceApiSession} to maintain appium parameters for the current execution
	 */
	private AppiumSession appiumSession;
	
	/**
	 * A reference to List:TestRunParameterSet to maintain current test suite execution parameters
	 * {@link TestRunParameterSet}
	 * @see TestRunParameterSet 
	 */
	private List<TestRunParameterSet> testRunParameterSet;

	private int currentExecutingSeq = -1;
	
	public int getCurrentExecutingSeq() {
		return currentExecutingSeq;
	}

	public void setCurrentExecutingSeq(int currentExecutingSeq) {
		this.currentExecutingSeq = currentExecutingSeq;
	}
	
	/**
	 * Constructor
	 * @param masterAppiumOpts Appium properties for all the threads to be run
	 * @param filterSequence Sequence no. of the current thread configuration
	 * @param testRunParameterSet A reference to the original TestRunParameterSet of suite
	 * @throws UnknownHostException 
	 */
	public ExecutionSession(String suiteName, String env,
			Properties masterAppiumOpts,int filterSequence,List<TestRunParameterSet> testRunParameterSet,TestSuite testSuite) throws UnknownHostException {
		driverFactorySession = new DriverFactorySession();
		appiumSession = new AppiumSession(masterAppiumOpts, filterSequence,testSuite);
		
		reportingSession = new ReportingSession(suiteName,appiumSession,testSuite.autoDeployPath, testSuite.mailingList,(String) masterAppiumOpts.get("resultServerPort"), (String)masterAppiumOpts.get("insertReport"),masterAppiumOpts);   // added by ankit for Reporting
		serviceApiSession = new ServiceApiSession(env);
		this.testRunParameterSet = adaptParametersToAppium(appiumSession.getAppiumThread().getAppiumConfig().getProperty("flavour"),testRunParameterSet);
	}
	
	public void startReporting(WebDriver driver) {
		if(reportingSession.getSuiteReporting() == null)
			reportingSession.startSuiteReporting(driver);
		else
			reportingSession.setExecutionDriver(driver);
	}
	
	/**
	 * Method to create List:TestRunParameterSet Object for this thread from the original List:TestRunParameterSet Object
	 * @param flavour Flavour for the current thread execution
	 * @param testRunParameterSet 
	 * @return List:TestRunParameterSet Containing TestRunParameterSet for the current thread execution
	 */
	List<TestRunParameterSet> adaptParametersToAppium(String flavour,List<TestRunParameterSet> testRunParameterSet) {
		List<TestRunParameterSet> thisAppiumTrpSet = testRunParameterSet;
		try {
			Iterator<TestRunParameterSet> thisAppiumTrpSetItr = thisAppiumTrpSet.iterator();
			while(thisAppiumTrpSetItr.hasNext()) {
				TestRunParameterSet itrTrpSet = thisAppiumTrpSetItr.next();
				itrTrpSet.setFlavour(flavour);
			}
		}
		catch(Exception e) {
			
		}
		return thisAppiumTrpSet;
	}
	
	/**
	 * Getter method for List:TestRunParameterSet of the current thread
	 * @return List:TestRunParameterSet
	 */
	public List<TestRunParameterSet> getTestRunParameterSet() {
		return testRunParameterSet;
	}
	
	/**
	 * Getter method for the reference of DriverFactorySession Object for this Execution session
	 * @return
	 */
	public DriverFactorySession getDriverFactorySession() {
		return driverFactorySession;
	}
	
	/**
	 * Getter method for the reference of ReportingSession Object for this Execution session
	 * @return
	 */	
	public ReportingSession getReportingSession() {
		return reportingSession;
	}
	
	/**
	 * Getter method for the reference of ServiceApiSession Object for this Execution session
	 * @return
	 */	
	public ServiceApiSession getServiceApiSession() {
		return serviceApiSession;
	}
	
	/**
	 * Getter method for the reference of AppiumSession Object for this Execution session
	 * @return
	 */	
	public AppiumSession getAppiumSession() {
		return appiumSession;
	}
	
	/**
	 * Setter method for the reference of AppiumSession Object for this Execution session
	 * @return
	 */	
	public void setAppiumSession(AppiumSession appiumSession) {
		this.appiumSession = appiumSession;
	}
	
	/**
	 * Setter method for the reference of DriverFactorySession Object for this Execution session
	 * @return
	 */	
	public void setDriverFactorySession(DriverFactorySession driverFactorySession) {
		this.driverFactorySession = driverFactorySession;
	}
	
	/**
	 * Setter method for the reference of ReportingSession Object for this Execution session
	 * @return
	 */	
	public void setReportingSession(ReportingSession reportingSession) {
		this.reportingSession = reportingSession;
	}
	
	/**
	 * Setter method for the reference of ServiceApiSession Object for this Execution session
	 * @return
	 */	
	public void setServiceApiSession(ServiceApiSession serviceApiSession) {
		this.serviceApiSession = serviceApiSession;
	}
	
}