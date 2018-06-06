package snapdeal.mobileAutomation.master;

import io.appium.java_client.AppiumDriver;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import snapdeal.mobileAutomation.master.FlavourDictionary;

/**
 * This class maintains a session of a Driver Factory for the current execution 
 * @author Khagesh Kapil
 * @see DriverFactory
 */
public class DriverFactorySession extends Session{

	private FlavourDictionary flavourDictionary;
	
	private boolean isStale = false;
	
	/**
	 * Timestamp @ driver instantiation
	 */
	private Long driverTimeStamp = null;
	
	/**
	 * A reference to the driver used to execute last run test case
	 */
	private WebDriver lastExecutionDriver = null;
	
	/**
	 * A reference of appium driver
	 */
	private AppiumDriver<WebElement> appiumDriver = null;
	
	/**
	 * Flag to re-initialize the driver
	 */
	private boolean resetDriverFlag = true;
	
	public FlavourDictionary getFlavourDictionary() {
		return flavourDictionary;
	}

	public void setFlavourDictionary(FlavourDictionary flavourDictionary) {
		this.flavourDictionary = flavourDictionary;
	}

	/**
	 * @return the isStale
	 */
	public boolean isStale() {
		return isStale;
	}

	/**
	 * @param isStale the isStale to set
	 */
	public void setStale(boolean isStale) {
		this.isStale = isStale;
	}

	/**
	 * Setter method for {@link #lastExecutionDriver}
	 * @param lastExecutionDriver
	 */
	public void setLastExecutionDriver(WebDriver lastExecutionDriver) {
		this.lastExecutionDriver = lastExecutionDriver;
	}
	
	/**
	 * Getter method for {@link #lastExecutionDriver}
	 * @return {@link WebDriver}
	 */
	public WebDriver getLastExecutionDriver() {
		return lastExecutionDriver;
	}
	
	/**
	 * Setter method for {@link #resetDriverFlag}
	 * @param resetDriverFlag
	 */
	public void setResetDriverFlag(boolean resetDriverFlag) {
		this.resetDriverFlag = resetDriverFlag;
	}
	
	/**
	 * Getter method for {@link #resetDriverFlag}
	 * @return
	 */
	public boolean getResetDriverFlag() {
		return resetDriverFlag;
	}
	
	/**
	 * Getter method for {@link #appiumDriver}
	 * @return
	 */
	public AppiumDriver<WebElement> getAppiumDriver(){
		return appiumDriver;
	}
	
	/**
	 * Setter method for {@link #appiumDriver}
	 * @param appiumDriver
	 */
	public void setAppiumDriver(AppiumDriver<WebElement> appiumDriver){
		this.appiumDriver =  appiumDriver;
	}
	
	public Long getDriverTimeStamp() {
		return driverTimeStamp;
	}

	public void setDriverTimeStamp(Long driverTimeStamp) {
		this.driverTimeStamp = driverTimeStamp;
	}
	
}