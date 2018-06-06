package snapdeal.mobileAutomation.master;

import java.io.File;
import java.io.FileInputStream;
import java.net.URL;
import java.util.Enumeration;
import java.util.Properties;

//import junit.runner.TestCaseClassLoader;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.WebDriverWait;

//import com.experitest.client.Client;
//import com.experitest.selenium.MobileWebDriver;
//import com.robotium.solo.Solo;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.ios.IOSDriver;
import io.appium.java_client.remote.AndroidMobileCapabilityType;

/**
 * It is a wrapper around a web driver and is responsible for generating a web driver of the specified flavour with the specified set of capabilities. 
 * It also contains the waits related info.
 * @author Khagesh Kapil
 * @version 0.1
 * @see	org.openqa.selenium.WebDriver
 */
public class DriverFactory {
	
	/** 
	 * Path to the file with all desired capabilities listed for a particular flavour
	 */
	private Properties masterProp = null;
	
	/**
	 * Reference to the current instance of WebDriver with the {@link #driverCapabilities} capabilities, {@link #masterProp} properties
	 * and {@link #driverWait} WebDriverWait object  
	 */
	private AppiumDriver<WebElement> appiumDriver = null;
	
	/**
	 * Reference to the current running instance of driver
	 * @See {@link WebDriver}
	 */
	private WebDriver driver = null;
	//private Solo robDriver = null;
	
	/**
	 * DesiredCapabilities Object for the current WebDriver - {@link #driver}
	 */
	private DesiredCapabilities driverCapabilities = null;
	
	/**
	 * WebDriverWait Object for the current WebDriver - {@link #driver}
	 */
	private WebDriverWait driverWait = null;
	
	/**
	 * Timestamp @ driver instantiation
	 */
	private Long driverTimeStamp = null;
	
	/**
	 * Flavor for the current WebDriver - {@link #driver}
	 */
	private String flavour = null; 
	
	/**
	 * Return a text description of the invoking DriverFactory Object
	 */
	@Override
	public String toString() {
		return "Flavour = "+flavour+"\n"+
				"Wait = "+driverWait+"\n"+
				"Capabilities = "+driverCapabilities+"\n"+
				"Driver = "+driver+"\n"+
				"Master Properties = "+masterProp+"\n";
	}
	
	/**
	 * Default Constructor
	 * @since 0.1
	 */
	DriverFactory() {
	}
	
	/**
	 * Constructor
	 * @param flavour name of the flavor for which driver is requested
	 * @since 0.1
	 */
	DriverFactory(ExecutionSession executionSession,String flavour) {
		this.flavour = flavour;
		loadMasterProperties(executionSession,flavour);
		updateCapabilities(executionSession);
		loadDesiredCapabilities(executionSession);
		initDriver(executionSession);
	}
	
	/**
	 * Initialize driver with the properties in {@link #masterProp} and capabilities in {@link #driverCapabilities}
	 * @since 0.1
	 */
	@SuppressWarnings("rawtypes")
	void initDriver(ExecutionSession executionSession) {
		try {
			long defaultWaitTime = Long.parseLong((String)masterProp.getProperty("defaultWaitTime"));
			
			/*File f = new File(masterProp.getProperty("robotiumPrjPath"));
			URL dirUrl = new URL("file:/"+masterProp.getProperty("robotiumPrjPath")+"/");             // 1
			URLClassLoader cl = new URLClassLoader(new URL[] {dirUrl});  // 2
			Class<?> loadedClass = new TestCaseClassLoader(masterProp.getProperty("robotiumPrjPath")).loadClass("com.android.test.TestAutocomplete");
			Method[] m = loadedClass.getDeclaredMethods();
			for(Method meth : m) {
				if(meth.getName().equals("getResult")) {
					String resString = (String) meth.invoke(loadedClass.newInstance(), null);
					System.out.println(resString);
				}
			}
			cl.close();*/
			//obj.doSomething();
			if(flavour.equals("androidApp_robotium")) {
				throw new ArrayIndexOutOfBoundsException();
			}
			if(executionSession.getDriverFactorySession().getResetDriverFlag()) {
				if((executionSession.getDriverFactorySession().getLastExecutionDriver() != null) && (!executionSession.getDriverFactorySession().isStale())) {
					((AppiumDriver) executionSession.getDriverFactorySession().getLastExecutionDriver()).resetApp();
					executionSession.getDriverFactorySession().getLastExecutionDriver().quit();
				}
				if(flavour.equals("iosApp") || flavour.equals("androidApp") || flavour.equals("mobileWeb")){
					System.out.println("-----------++++++++================");
					if(flavour.equals("iosApp")){
						IOSDriver<WebElement> iosdriver = new IOSDriver<>(new URL(masterProp.getProperty("baseURL")), driverCapabilities);
						executionSession.getDriverFactorySession().setAppiumDriver(iosdriver);
					}
					else if(flavour.equals("androidApp")){
						AndroidDriver<WebElement> androidDriver = new AndroidDriver<>(new URL(masterProp.getProperty("baseURL")), driverCapabilities);
						executionSession.getDriverFactorySession().setAppiumDriver(androidDriver);
					}
//					else
//						executionSession.getDriverFactorySession().setAppiumDriver(new RemoteWebDriver(new URL(masterProp.getProperty("baseURL")),driverCapabilities));
					executionSession.getDriverFactorySession().setLastExecutionDriver(executionSession.getDriverFactorySession().getAppiumDriver());
				}/*else if(flavour.equals("windowsApp")) {
					invokeSeeTestServer();
					executionSession.getDriverFactorySession().setLastExecutionDriver(new MobileWebDriver(
																						masterProp.getProperty("host"),
																						Integer.parseInt(masterProp.getProperty("port")),
																						masterProp.getProperty("projectBaseDirectory"),
																						null,null,null));
					driver = executionSession.getDriverFactorySession().getLastExecutionDriver();
					getMobileDriverClient().setDevice(masterProp.getProperty("deviceName"));
					getMobileDriverClient().launch(masterProp.getProperty("appName"), true, executionSession.getDriverFactorySession().getResetDriverFlag());
				}*/
				else{
					executionSession.getDriverFactorySession().setLastExecutionDriver(new HtmlUnitDriver());
				}
				setDriverTimeStamp(System.currentTimeMillis());
				executionSession.getDriverFactorySession().setDriverTimeStamp(getDriverTimeStamp());
			}
			appiumDriver = executionSession.getDriverFactorySession().getAppiumDriver();
			driver = executionSession.getDriverFactorySession().getLastExecutionDriver();
			setDriverTimeStamp(executionSession.getDriverFactorySession().getDriverTimeStamp());
			driverWait = new WebDriverWait(driver,defaultWaitTime);
			executionSession.startReporting(driver);
		}
		catch(ArrayIndexOutOfBoundsException ae) {
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Loads the DesiredCapabilities in {@link #driverCapabilities} from capabilities mentioned in {@link #masterProp}
	 * @since 0.1
	 */
	void loadDesiredCapabilities(ExecutionSession executionSession) {
		driverCapabilities = new DesiredCapabilities();
		try {
			Enumeration<?> e = masterProp.propertyNames();
			while(e.hasMoreElements()) {
				String prop = (String) e.nextElement();
				System.out.println("property: "+prop);
				if(prop.startsWith("cap_")) {
					
					String capName = prop.replaceAll("cap_","");
					String capVal = masterProp.getProperty(prop);
					System.out.println("caps: "+capName);
					if(capName.equals("app")) {
						Properties p = new Properties();
						//p.load(new FileInputStream(new File(getClass().getProtectionDomain().getCodeSource().getLocation().getPath()+"snapdeal/mobileAutomation/master/masterConfig/flavourMap.properties")));
						p.load(new FileInputStream(new File(FlavourDictionary.flavourMapPath)));
						//System.out.println("1"+"\n"+executionSession.getDriverFactorySession().getFlavourDictionary().getValue().get("binariesPath").toString());
//						File appFile = new File(p.getProperty("binariesPath"));
						File appFile = new File(FlavourDictionary.binariesPath);
						System.out.println("2");
						capVal =  appFile.getParent()+ "/app/" + capVal;
						System.out.println("capsValue: "+capVal);
					}
					
					if(capVal.equals("<NULL>")) {
                        if(capName.equals("udid"))
                            continue;
                        capVal = "";
                    }
					
					driverCapabilities.setCapability(capName, capVal);
				}
//				driverCapabilities.setCapability(AndroidMobileCapabilityType.RECREATE_CHROME_DRIVER_SESSIONS, true);
//				driverCapabilities.setCapability("autoWebview", true);
//				driverCapabilities.setCapability("browserName", "Chrome");
//				if (System.getProperty("os.name").contains("MAC")){
//					driverCapabilities.setCapability("plateform", "MAC");
//				}
				
			}
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Loads all properties into {@link #masterProp} from the master configuration Properties file 
	 * @param flavour String representing the platform for which the driver is to be made <p>iosApp : for iOS APP</p><p>androidApp : for Android APP</p><p>windowsApp : for Windows APP</p><p>mobileWeb : for Mobile Web</p><p>mobAPI : for Mobile API</p>
	 * @since 0.1
	 */
	void loadMasterProperties(ExecutionSession executionSession, String flavour) {
		masterProp = new Properties();
		/*String fileLoc = executionSession.getDriverFactorySession().getFlavourDictionary().getValue().get("binariesPath")+
				executionSession.getDriverFactorySession().getFlavourDictionary().getValue().get(flavour).toString()+".properties";*/
		String fileLoc = UpdateMasterConfig.masterConfiguration.get(flavour);
		try {
			File f = new File(fileLoc);
			FileInputStream fis = new FileInputStream(f);
			masterProp.load(fis);
			
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * This method update the driver capabilities from the test suite config file
	 * @param executionSession Object of the current ExecutionSession
	 */
	void updateCapabilities(ExecutionSession executionSession) {
		masterProp.setProperty("cap_platformVersion", executionSession.getAppiumSession().getAppiumThread().getAppiumConfig().getProperty("platformVersion"));
		masterProp.setProperty("cap_platformName", executionSession.getAppiumSession().getAppiumThread().getAppiumConfig().getProperty("platformName"));
		masterProp.setProperty("cap_deviceName", executionSession.getAppiumSession().getAppiumThread().getAppiumConfig().getProperty("deviceName"));
		masterProp.setProperty("baseURL", "http://"
				+ executionSession.getAppiumSession().getAppiumThread().getAppiumConfig().getProperty("flag--address")+":"
				+ executionSession.getAppiumSession().getAppiumThread().getAppiumConfig().getProperty("flag--port")+"/wd/hub"
				);
		masterProp.setProperty("cap_app",executionSession.getAppiumSession().getAppiumThread().getAppiumConfig().getProperty("app"));
		masterProp.put("cap_udid", executionSession.getAppiumSession().getAppiumThread().getAppiumConfig().getProperty("udid"));
	}

	/**
	 * Returns the current running instance of WebDriver
	 * @return WebDriver
	 * @since 0.1
	 * @see #driverWait
	 */
	public WebDriver getDriver() {
		return driver;
	}

	/**
	 * Getter method for {@link #appiumDriver}
	 * @return {@link #appiumDriver}
	 */
	public AppiumDriver<WebElement> getAppiumDriver(){
		return appiumDriver;
	}
	
	/**
	 * Returns an instance of WebDriverWait associated with the current running instance of WebDriver
	 * @return WebDriverWait
	 * @since 0.1
	 * @see #driverWait
	 */
	public WebDriverWait getWait() {
		return driverWait;
	}
	
	/**
	 * Getter method for Windows platform Client
	 * @return Windows platform Client
	 */
//	public Client getMobileDriverClient() {
//		Field[] fld = driver.getClass().getFields();
//		Object clt = new Object();
//		try {
//			for(Field f : fld) {
//				//System.out.println("---"+f.getName());
//				if(f.get(driver) instanceof Client) {
//					clt = ((Client)f.get(driver)); 
//					break;
//				}
//			}
//		}
//		catch(Exception e) {
//			e.printStackTrace();
//		}
//		return (Client)clt;
//	}
	
	/**
	 * Method to invoke See Test Server
	 */
	public void invokeSeeTestServer() {
 		  try {
			Runtime.getRuntime().exec(masterProp.getProperty("seeTestStudioPath"));
			Thread.sleep(10000);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public long getUserWaitTime() {
		return Long.parseLong((String)masterProp.getProperty("defaultWaitTime"));
	}
	

	public Long getDriverTimeStamp() {
		return driverTimeStamp;
	}
	

	private void setDriverTimeStamp(Long driverTimeStamp) {
		this.driverTimeStamp = driverTimeStamp;
	}
	
}
