package snapdeal.mobileAutomation.master;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import snapdeal.mobileAutomation.exceptions.PlatformNotSupportedException;
import snapdeal.mobileAutomation.master.TestSuiteTokenQueue;
import snapdeal.mobileAutomation.master.ExecutionSession;
import testCaseReporting.SuiteReporting;

/**
 * This class represents an abstract test suite
 * @author Khagesh Kapil
 */
public class TestSuite extends Thread{
	
	/**
	 * Filed to hold values from test suite properties file
	 */
	private Properties appiumConfiguration = new Properties();
	
	public static boolean isInternalRunMode=false; 
	
	protected boolean appiumRestarted = false;
	
	protected String mailingList = null;
	
	protected String autoDeployPath = null;
	
	/**
	 * A reference of {@link FlavourDictionary} for the current instance of {@link TestSuite}
	 */
	protected FlavourDictionary flavourDictionary;
	
	/**
	 * A reference of {@link ExecutionSession} for the current instance of {@link TestSuite}
	 */
	protected ExecutionSession executionSession;
	
	/**
	 * List of {@link TestRunParameterSet} for this suite
	 */
	protected List<TestRunParameterSet> testRunParameterSets = new ArrayList<TestRunParameterSet> ();
	
//	/**
//	 * Instance of {@link SuiteReporting} for this {@link TestSuite}
//	 */
//    protected SuiteReporting suiteReporting = null;
    
    /**
     * Field to hold flavour for this suite
     */
    private String suiteFlavour;
    
    private String apiEnv = "";
    
    private boolean loggingEnabled = false;
    
    /**
	 * @return the appiumRestarted
	 */
	public boolean isAppiumRestarted() {
		return appiumRestarted;
	}
	 
	/**
	 * @param appiumRestarted the appiumRestarted to set
	 */
	public void setAppiumRestarted(boolean appiumRestarted) {
		this.appiumRestarted = appiumRestarted;
	}

	/**
     * Default Constructor
     */
	public TestSuite(String[] args){
		if(!(args.length==0)) {
			if(!(args[0]==null) && !args[0].equals(""))
				autoDeployPath = args[0];
			if(!(args[1]==null) && !args[1].equals(""))
				mailingList = args[1];
		}
		if(TestSuiteTokenQueue.suiteSequence>-1)
			setTestCaseListInfo(TestSuiteTokenQueue.getQueue().get(TestSuiteTokenQueue.suiteSequence).getTokenSuiteConfig());
		else
			setTestCaseListInfo();
	}
	
	/**
	 * Constructor
	 * @param flavour Platform
	 * @param threadNum Sequence Number for device configuration in Test Suite properties file 
	 */
	protected TestSuite(TestSuite parentSuite, String flavour,int threadNum) {
		this.autoDeployPath = parentSuite.autoDeployPath;
		this.mailingList = parentSuite.mailingList;
//		if(!isInternalRunMode)
//			waitWhileSuiteQueuePopulates();
		suiteFlavour = flavour;
		if(TestSuiteTokenQueue.getQueue()==null)
			setTestCaseListInfo(TestSuiteTokenQueue.getQueue().get(threadNum-1).getTokenSuiteConfig());
		else
			setTestCaseListInfo();
		flavourDictionary = new FlavourDictionary(suiteFlavour);
		try {
			executionSession = new ExecutionSession(this.getClass().getName(), apiEnv, appiumConfiguration,threadNum,testRunParameterSets,this);
		} catch (UnknownHostException e1) {
			e1.printStackTrace();
		}
		executionSession.getDriverFactorySession().setFlavourDictionary(flavourDictionary);
		Runtime.getRuntime().addShutdownHook(new TestSuite.AppiumShutdownHook());
		while(true) {
			if(executionSession.getAppiumSession().getAppiumThread().isAppiumStarted())
				break;
		}
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
//		suiteReporting = new SuiteReporting(,executionSession.getReportingSession());
	}
	
	private void waitWhileSuiteQueuePopulates() {
		while(true) {
			if(TestSuiteTokenQueue.getQueue().size()<=0)
				continue;
			else
				break;
		}
	}
	
	/**
	 * Method to get the no. of concurrent threads
	 * @return
	 */
	public int getThreadsCount() {
		return Integer.parseInt(appiumConfiguration.getProperty("concurrentDevices"));
		/*for(int i=1;i<=concurrentThreads;i++) {
			this.
			threadNum = i;
			Thread.sleep(2000);
			this.run();
//			this.start();
		}*/
	}
	
	/**
	 * Method to Initialize all parameter for a {@link TestCase}
	 * @param tc {@link TestCase} Object
	 * @param platform platform name
	 * @param environment environment
	 * @return
	 * @throws Throwable
	 */
	@SuppressWarnings("unused")
	public Object start(TestCase tc,String platform) throws Throwable {
		Class<?> cls=null;
		try {
			Constructor<?> cr = tc.getClass().getConstructor(new Class[]{ExecutionSession.class,String.class});
			//Will call repoint of service api session from here, if in case setting API environment is required for each test case script
			//executionSession.getServiceApiSession().setEnvironment(environment);
			tc = (TestCase) cr.newInstance(executionSession,platform);
		}
		catch(Exception e) {
			if(e.getCause() instanceof PlatformNotSupportedException)
				throw e.getCause();
			else
				e.printStackTrace();
		}
		return tc;
	}
	
	/**
	 * Method for annotation processing of a child class of {@link TestSuite}
	 */
	public void setTestCaseListInfo(String...strings) {
		String snapshotStrategy = "";
		
		for(Method meth : this.getClass().getDeclaredMethods()) {
			if(meth.isAnnotationPresent(SnapshotStrategy.class)) {
				SnapshotStrategy ss = meth.getAnnotation(SnapshotStrategy.class);
				snapshotStrategy = ss.value();
				System.out.println("snapshot Strategy is :"+snapshotStrategy);  // added by ankit
//				break;
			}
			if(meth.isAnnotationPresent(TestEnvironment.class)) {
				TestEnvironment testEnvironment = meth.getAnnotation(TestEnvironment.class);
				apiEnv = testEnvironment.value();
				System.out.println("API env is: "+apiEnv);   // added by ankit
			}
			if(meth.isAnnotationPresent(Logging.class)){
				Logging logging = meth.getAnnotation(Logging.class);
				loggingEnabled = logging.enabled();
				System.out.println("Logging is Enabled: " + loggingEnabled);
			}
		}
		for(Method meth : this.getClass().getDeclaredMethods()) {
			if(meth.isAnnotationPresent(TestCases.class)) {
				TestCases tcs = meth.getAnnotation(TestCases.class);
				for(Script itrScript : tcs.value()) {
					TestRunParameterSet testRunParameterSet = new TestRunParameterSet();
					testRunParameterSet.setDriverResetFlag(itrScript.driverResetFlag());
					
					testRunParameterSet.setFlavour(itrScript.flavour());
					testRunParameterSet.setTestCaseName(itrScript.testCaseName());
					testRunParameterSet.setLld(itrScript.lld());
					testRunParameterSet.setTestDataId(itrScript.testDataId());
					if(itrScript.SnapshotStrategy().value().equals(""))
						testRunParameterSet.setSnapshotStrategy(snapshotStrategy);
					else
						testRunParameterSet.setSnapshotStrategy(itrScript.SnapshotStrategy().value());
					
					testRunParameterSet.setLoggingEnabled(loggingEnabled);
					
					testRunParameterSets.add(testRunParameterSet);
					System.out.println("TestRunParameterSet are: "+testRunParameterSets.toString());  // added by ankit
				}
			}
			
			if(meth.isAnnotationPresent(AppiumConfig.class)) {
				AppiumConfig appiumConfig = meth.getAnnotation(AppiumConfig.class);
				String appiumConfigFile = "";
				if(strings.length==0)
					appiumConfigFile = this.getClass().getProtectionDomain().getCodeSource().getLocation().getPath()+appiumConfig.value()+".properties";
				else
					appiumConfigFile = strings[0];
				try {
					appiumConfiguration.load(new FileInputStream(new File(appiumConfigFile)));
				}
				catch(Exception e) {
					System.err.println("Couldn't load given appium configurations");
					e.printStackTrace();
				}
			}
		}
		//final Something oldAnnotation = (Something) Foobar.class.getAnnotations()[0];
	}

	/**
	 * 
	 */
	public void loadSuiteConfig(String filePath) {
		try {
			appiumConfiguration.load(new FileInputStream(new File(filePath)));
		}
		catch(Exception e) {
			System.err.println("Couldn't load given appium configurations");
			e.printStackTrace();
		}
	}
	
	/*@Override
	public void run() {
		
	}*/	

	private static class AppiumShutdownHook extends Thread {
		public void run() {
			try {
				String platform = System.getProperty("os.name");
				if (platform.startsWith("Windows"))
					Runtime.getRuntime().exec("taskkill /IM node.exe -F");
				else
					Runtime.getRuntime().exec("killall node");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

}