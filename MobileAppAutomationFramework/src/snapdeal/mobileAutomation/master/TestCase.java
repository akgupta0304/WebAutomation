package snapdeal.mobileAutomation.master;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Rotatable;
import org.openqa.selenium.ScreenOrientation;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.Augmenter;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

//import com.experitest.selenium.MobileWebDriver;
import com.gargoylesoftware.htmlunit.javascript.background.JavaScriptExecutor;
import com.google.common.base.Stopwatch;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.MobileBy;
import io.appium.java_client.NetworkConnectionSetting;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.ios.IOSDriver;
import snapdeal.mobileAutomation.exceptions.PlatformNotSupportedException;
import snapdeal.mobileAutomation.master.TestCase.TestCaseCache;
import testCaseReporting.TestCaseReporting;

/**
 * This class is an abstract TestCase
 * It maintains a TestCaseCache for Caching of objects during a test case execution
 * It also re-implements the basic {@link WebDriver} functions to operate on {@link WebElement} to work with {@link TestObj}
 * @author Khagesh Kapil
 *
 */
public class TestCase implements Cloneable{
	private String consoleOutput = "true";
	/**
	 * Field mentioning the name of the main method of a test case script
	 * Default value is "script"
	 */
	private final String TEST_CASE_SCRIPT_METHOD_NAME = "script";
	
	/**
	 * A reference of {@link DriverFactory} for the currently executing test case
	 */
	private DriverFactory driverFactory = null;

	/**
	 * A reference of {@link ExecutionSession} for the currently executing test case 
	 */
	private ExecutionSession executionSession =null;
	
	private static Stopwatch stopWatch = Stopwatch.createUnstarted();
	
	public ExecutionSession getExecutionSession() {
		return executionSession;
	}

	public void setExecutionSession(ExecutionSession executionSession) {
		this.executionSession = executionSession;
	}

	/**
	 * Field containing the Array of Method Objects for {@link By} class 
	 */
	private static Method[] byMeths = (By.class).getMethods();
	
	/**
	 * Field containing the Array of Method Objects for {@link MobileBy} class 
	 */
	private static Method[] mobByMeths = (MobileBy.class).getMethods();
	
	/**
	 * Field containing the Array of Method Objects for {@link ExpectedConditions} class
	 */
	private static volatile Method[] expConditionMeths = (ExpectedConditions.class)
			.getMethods();
//	public static Field[] testObjFields = (TestObj.class).getDeclaredFields();
	
	/**
	 * Field containing path to test data file
	 */
	private String testDataFilePath;
	
	/**
	 * An Instance of {@link TestCaseCache} for the currently executing test case
	 */
	protected TestCaseCache testCaseCache = new TestCaseCache();
	
	/**
	 * A reference of {@link FlavourDictionary} for the currently executing TestCase
	 */
	protected FlavourDictionary flavorDictionary;
	
	protected FlavourDictionary apiDictionary;
	
	/**
	 * Map containing all the test data values
	 */
	protected Map<String,String> testData = new HashMap<String,String>();
	
	/**
	 * Flag for the current reporting step status 
	 */
	public static Boolean snapdealMobileStepReportingStatus = false;
	
	/**
	 * Field containing environment for the currently executing test case
	 */
	protected String environment;
	
	protected List<String> llds = new ArrayList<String>();
	
	/**
	 * {@link TestCaseReporting} instance for the currently executing test case
	 */
	public TestCaseReporting report = null;
	
	public FlavourDictionary getApiDictionary() {
		return apiDictionary;
	}
	
	/**
	 * Method to invoke a method on current instance of {@link MobileWebDriver}
	 * @param webDriver reference to the {@link WebDriver} instance containing {@link MobileWebDriver} Object 
	 * @param method Name of the method to be invoked from {@link MobileWebDriver}
	 * @param params Array of Object type arguments for the method specified
	 * @return Object the object returned by the invoked method
	 * @throws Exception
	 */
	public Object invokeMobileDriverMethod(WebDriver webDriver,String method, Object...params) throws Exception {
    	Object o = null;
    	try {
    		Class<?> driverClass = webDriver.getClass();
    		Method[] driverMethod = driverClass.getMethods();
    		for(Method m : driverMethod) {
    			if(m.getName().equalsIgnoreCase(method)) {
    				if(m.isVarArgs()) {
    					Object[] obj = new Object[params.length];
    					for(int i=0;i<params.length;i++) {
    						obj[i] = params[i];
    					}
    					o = m.invoke(webDriver,new Object[]{obj});
    				}
    				else
    					o = m.invoke(webDriver,params);
    			}
    		}
    	}
    	catch(Exception e) {
    		e.printStackTrace();
    		throw new Exception("illegal functionality call");
    	}
    	return o;
    }
	
	/**
	 * Getter method for currently used Object of {@link TestCaseCache}
	 * @return {@link #testCaseCache}
	 */
	public TestCaseCache getTestCaseCache() {
		return testCaseCache;
	}
	
	/**
	 * Method to change the orientation of mobile screen form portrait to landscape
	 * @param testCase
	 */
	public void changeOrientation(TestCase testCase){
	try{
		WebDriver augmentedDriver = new Augmenter().augment((AppiumDriver<WebElement>)testCase.driverFactory.getDriver());
		ScreenOrientation sc=((Rotatable)augmentedDriver).getOrientation();
		System.out.println("screen orientaion is"+sc);
		((Rotatable)augmentedDriver).rotate(ScreenOrientation.LANDSCAPE);
		}
		catch(Exception ee)
		{
			System.out.println("exception is:"+ee);
		}
	}

	/**
	 * Method to click on the given {@link TestObj}
	 * @param testObj
	 * @see TestObj
	 */
	public static boolean click(TestObj testObj) {
		boolean isClicked = false;
		try {
			testObj.setMaxSequence();
			int i = 1;
			Iterator<ObjectEntry> entriesIterator = testObj.getEntriesSet().iterator();
			clicker:
				while (entriesIterator.hasNext()) {
					ObjectEntry objectEntry = entriesIterator.next();
					if(objectEntry.getSeqNum()==0)
						break; 
					Iterator<WebElement> listItr = (Iterator<WebElement>) objectEntry
							.getValue().iterator();
					while (listItr.hasNext()) {
						WebElement elem = listItr.next();
						try {
							elem.click();
							isClicked = true;
							snapdealMobileStepReportingStatus = true;
							break clicker;
						} catch (Exception e) {
							continue;
						}
					}
					if (i == testObj.getMaxSeq()) {
						snapdealMobileStepReportingStatus = false;
						throw new Exception("No Locator could be clicked for the element "+ testObj.ObjName);
					}
					i++;
				}
		} catch (Exception e) {
			e.printStackTrace();
			snapdealMobileStepReportingStatus = false;
		}
		return isClicked;
	}

	/**
	 * Method to pause execution till the given time
	 * @param time Number of milliseconds to wait
	 */
	public static void sleep(int time) {
		try {
			Thread.sleep(time);
		} catch (InterruptedException e) {
		}
	}

	/**
	 * Method to type given text to the given {@link TestObj}
	 * @param testObj TestObj Object to be typed on
	 * @param text Text to be typed
	 */
	public static boolean sendkey(TestObj testObj, String text) {
		try {
			testObj.setMaxSequence();
			int i = 1;
			Iterator<ObjectEntry> entriesIterator = testObj.getEntriesSet().iterator();
			clicker:
				while (entriesIterator.hasNext()) {
					ObjectEntry objectEntry = entriesIterator.next();
					if(objectEntry.getSeqNum()==0)
						break;
					Iterator<WebElement> listItr = (Iterator<WebElement>) objectEntry
							.getValue().iterator();
					while (listItr.hasNext()) {
						WebElement elem = listItr.next();
						try {
							stopStopWatch();
				            elem.clear();
				            startStopWatch();
							elem.sendKeys(text);
							snapdealMobileStepReportingStatus = true;
							break clicker;
						} catch (Exception e) {
							continue;
						}
					}
					if (i == testObj.getMaxSeq()) {
						snapdealMobileStepReportingStatus = false;
						throw new Exception("No Locator could be send text for the element "
								+ testObj.ObjName);
					}
					i++;
				}
		} catch (Exception e) {
			snapdealMobileStepReportingStatus = false;
			e.printStackTrace();
		}
		return snapdealMobileStepReportingStatus;
	}

	public static void hideKeyBoard(TestCase testObj){
		try{
			testObj.getDriverFactory().getAppiumDriver().hideKeyboard();
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	/**
	 * Getter method for the currently used instance of {@link DriverFactory}
	 * @return {@link #driverFactory}
	 * @see DriverFactory
	 */
	public DriverFactory getDriverFactory() {
		return driverFactory;
	}
	
	private static String executeShellScript(String parameter, String udid){
		if(parameter==null || parameter=="" || udid==null || udid=="")
			return null;
		Properties bashProfile = new Properties();
		String path = "/Users/" + System.getProperty("user.name") + "/.bash_profile";
		String lineOutBuffer = "";
		try {
			bashProfile.load(new FileInputStream(path));
			String sdkPath = bashProfile.getProperty("ANDROID_HOME");
			System.out.println("Sdk Path:" + sdkPath);

			String command = sdkPath + "/platform-tools/adb -s $1 shell ";
			command += parameter;

			PrintWriter writer = new PrintWriter("./myscript.sh", "UTF-8");
			writer.println("#!/bin/bash");
			writer.println(command);
			writer.close();

			
			try {
				Process p = Runtime.getRuntime().exec("chmod 777 ./myscript.sh");
				p.waitFor();
				BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
				String line = "";
				while ((line = reader.readLine()) != null) {
				}

			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
//			Process p = Runtime.getRuntime().exec(new String[]{"bash","-c","chomod 777 ./myscript.sh"});
			
			ProcessBuilder processBuilder = new ProcessBuilder("./myscript.sh", udid);
			Process process = processBuilder.start();
			BufferedReader proOut = new BufferedReader(new InputStreamReader(process.getInputStream()));
			for (String lineOut = proOut.readLine(); lineOut != null; lineOut = proOut.readLine()) {
				lineOutBuffer = lineOut;
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally {
			File file = new File("./myscript.sh");
			file.delete();
		}

		return lineOutBuffer;
	}

	public static String getIMEI(String udid){
	      String parameter = "service call iphonesubinfo 1 | awk -F \"'\" '{print $2}' | sed '1 d' | sed 's/[\\.| ]//g' | awk '{print}' ORS=''";
	      return executeShellScript(parameter, udid);
	}
	
	public static String getAndroidId(String udid){
		String parameter = "settings get secure android_id";
	    return executeShellScript(parameter, udid);
	}
	
	public static String getAppVersion(String udid){
		String parameter = "dumpsys package com.snapdeal.main | grep 'versionName' | awk -F '=' '{print $2}'";
	    return executeShellScript(parameter, udid);
	}
	
	public static String getDeviceName(String udid){
		String parameter = "getprop ro.product.model";
	    return executeShellScript(parameter, udid);
	}
	
	public static String getOSVersion(String udid){
		String parameter = "getprop ro.build.version.release";
		return executeShellScript(parameter, udid);
	}
	
	
	/**
	 * Method to click on an element on the basis of Object's text
	 * @param testObj {@link TestObj} Object
	 * @param text text of the elements to be clicked
	 */
	public static void clickUsingText(TestObj testObj, String text) {
		try {
			testObj.setMaxSequence();
			int i = 1;
			Iterator<ObjectEntry> entriesIterator = testObj.getEntriesSet().iterator();
			clicker:
				while (entriesIterator.hasNext()) {
					ObjectEntry objectEntry = entriesIterator.next();
					if(objectEntry.getSeqNum()==0)
						break;
					
					Iterator<WebElement> listItr = (Iterator<WebElement>) objectEntry
							.getValue().iterator();
					while (listItr.hasNext()) {
						WebElement elem = listItr.next();
						try {
							if (elem.getText().contains(text)) {
								elem.click();
								snapdealMobileStepReportingStatus = true;
								break clicker;
							}
						} catch (Exception e) {
							continue;
						}
					}
					
					if (i == testObj.getMaxSeq()) {
						snapdealMobileStepReportingStatus = false;;
						throw new Exception(
								"No Locator could be clicked for the element "
										+ testObj.ObjName);
					}
					i++;
				}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Method to click "Done" button on keyboard
	 * @param testCase Current {@link TestCase} instance
	 */
	public static void clickOnKeyboardDoneButton(TestCase testCase){
		try{
			((AndroidDriver<WebElement>)testCase.getDriverFactory().getDriver()).pressKeyCode((66));
			snapdealMobileStepReportingStatus = true;
		}catch(Exception ex){
			snapdealMobileStepReportingStatus = false;
			ex.printStackTrace();
		}
		
		
	}
	
	/**
	 * Method to invoke an event on android device using its key code
	 * @param keyCode The key code for the event to be invoked
	 * @param testCase Current {@link TestCase} instance
	 */
	public static void callAndroidEvent(int keyCode, TestCase testCase){
		try{
		((AndroidDriver<WebElement>)testCase.getDriverFactory().getDriver()).pressKeyCode(keyCode);
		snapdealMobileStepReportingStatus = true;
		}catch(Exception ex){
			snapdealMobileStepReportingStatus = true;
			ex.printStackTrace();
		}
	}
	
	/**
	 * Method to get the index no. of the {@link WebElement} in {@link TestObj} with the given text
	 * @param testObj The {@link TestObj} Object to be searched for {@link WebElement}
	 * @param text element text
	 * @return Number the index no. of the element
	 */
	public static int getObjIndexNumber(TestObj testObj, String text) {
		int index = 0;
		try {
			testObj.setMaxSequence();
			int i = 1;
			Iterator<ObjectEntry> entriesIterator = testObj.getEntriesSet().iterator();
			while (entriesIterator.hasNext()) {
				ObjectEntry objectEntry = entriesIterator.next();
				if(objectEntry.getSeqNum()==0)
					break;
				Iterator<WebElement> listItr = (Iterator<WebElement>) objectEntry
						.getValue().iterator();
				int count = 0;
				while (listItr.hasNext()) {
					WebElement elem = listItr.next();
					try {
						if (elem.getText().contains(text)) {
							index = count;
							snapdealMobileStepReportingStatus = true;
							return index;
						}
						count++;
					} catch (Exception e) {
						continue;
					}
				}
				if (i == testObj.getMaxSeq()) {
					snapdealMobileStepReportingStatus = false;
					throw new Exception(
							"No Locator could be clicked for the element "
									+ testObj.ObjName);
				}
				i++;
			}
		} catch (Exception e) {
			snapdealMobileStepReportingStatus = false;
			e.printStackTrace();
		}
		return 0;
	}

	/**
	 * Method to click on a {@link WebElement} using index
	 * @param testObj {@link TestObj} Object
	 * @param index index number
	 */
	public static void clickUsingIndexing(TestObj testObj, int index) {
		try {
			testObj.setMaxSequence();
			int i = 1;
			Iterator<ObjectEntry> entriesIterator = testObj.getEntriesSet().iterator();
			clicker: 
				while (entriesIterator.hasNext()) {
					ObjectEntry objectEntry = entriesIterator.next();
					if(objectEntry.getSeqNum()==0)
						break;
					List<WebElement> elems = objectEntry.getValue();
					try {
						elems.get(index).click();
						snapdealMobileStepReportingStatus = true;
						break clicker;
					} catch (Exception e) {
						
					}
					if (i == testObj.getMaxSeq()) {
						snapdealMobileStepReportingStatus = false;;
						throw new Exception(
								"No Locator could be clicked for the element "
										+ testObj.ObjName);
					}
					i++;
				}
		} catch (Exception e) {
			snapdealMobileStepReportingStatus = false;
			e.printStackTrace();
		}
	}
	
	/**
	 * Method to validate the ordering of prices of Objects on UI according to the order given order
	 * @param testObj {@link TestObj} Object
	 * @param order String description of the order to be verified
	 */
	public static void validatePriceOrder(TestObj testObj, String order) {
		String inOrder = "notpass";
		Boolean find = false;
		try {
			testObj.setMaxSequence();
			int i = 1;
			Iterator<ObjectEntry> entriesIterator = testObj.getEntriesSet().iterator();
			clicker: 
				while (entriesIterator.hasNext()) {
					ObjectEntry objectEntry = entriesIterator.next();
					if(objectEntry.getSeqNum()==0)
						break;
					List<WebElement> elems = objectEntry.getValue();
					try {
						if (elems.size() > 1) {
							find = true;
							for (int j = 1; j < elems.size(); j++) {
								if (order.equals("hightolow")) {
									if (Double.parseDouble(elems
											.get(j - 1).getText()
											.replaceAll("Rs. ", "")) >= Double
											.parseDouble(elems
													.get(j)
													.getText()
													.replaceAll("Rs. ",
															""))) {
													inOrder = "pass";
											}
								} else if (order.equalsIgnoreCase("lowtohigh")) {
									if (Double.parseDouble(elems.get(j - 1).getText()
											.replaceAll("Rs. ", "")) <= Double
											.parseDouble(elems
													.get(j)
													.getText()
													.replaceAll("Rs. ",
															""))) {
										inOrder = "pass";
									}
								}
							}
							if (find == true) {
								if (inOrder.equals("pass")) {
									snapdealMobileStepReportingStatus = true;
								} else {
									snapdealMobileStepReportingStatus = false;
								}
								break clicker;
							}
						}
					} catch (Exception ex) {
						continue;
					}
					if (i == testObj.getMaxSeq()) {
						snapdealMobileStepReportingStatus = false;
						throw new Exception(
								"No Locator could be clicked for the element "
										+ testObj.ObjName);
					}
					i++;
				}
		} catch (Exception e) {
			snapdealMobileStepReportingStatus = false;
			e.printStackTrace();
		}
	}

	/**
	 * Method to validate the ordering of discount of Objects on UI according to the order given order
	 * @param testObj {@link TestObj} Object
	 * @param order String description of the order to be verified
	 */
	public static void validateDiscountOrder(TestObj testObj, String order) {
		String inOrder = "notpass";
		Boolean find = false;
		try {
			testObj.setMaxSequence();
			int i = 1;
			Iterator<ObjectEntry> entriesIterator = testObj.getEntriesSet().iterator();
			clicker:
				while (entriesIterator.hasNext()) {
					ObjectEntry objectEntry = entriesIterator.next();
					List<WebElement> elems = objectEntry.getValue();
					try {
						if (elems.size() > 1) {
							find = true;
							for (int j = 1; j < elems.size(); j++) {
								if (order.equals("hightolow")) {
									if (Double.parseDouble(elems
											.get(j - 1).getText()
											.replaceAll("%", "")) <= Double
											.parseDouble(elems
													.get(j)
													.getText()
													.replaceAll("%", ""))) {
										inOrder = "pass";
									}
								} else if (order.equalsIgnoreCase("lowtohigh")) {
									if (Double.parseDouble(elems
											.get(j - 1).getText()
											.replaceAll("%", "")) >= Double
											.parseDouble(elems
													.get(j)
													.getText()
													.replaceAll("%", ""))) {
										inOrder = "pass";
									}
								}
							}
							if (find == true) {
								if (inOrder.equals("pass")) {
									snapdealMobileStepReportingStatus = true;
								} else {
									snapdealMobileStepReportingStatus = false;
								}
								break clicker;
							}
						}
					} catch (Exception ex) {
						continue;
					}
					if (i == testObj.getMaxSeq()) {
					snapdealMobileStepReportingStatus = false;
					throw new Exception(
							"No Locator could be clicked for the element "
									+ testObj.ObjName);
					}
					i++;
				}
		} catch (Exception e) {
			snapdealMobileStepReportingStatus = false;
			e.printStackTrace();
		}
	}

	/**
	 * Validate the prices for iOS as {@link #validatePriceOrder(TestObj, String)}
	 * @param testObj {@link TestObj} Object
	 * @param order String description of the order to be verified
	 */
	public static void validatePriceOrderForIos(TestObj testObj, String order) {
		String inOrder = "notpass";
		Boolean find = false;
		try {
			testObj.setMaxSequence();
			int i = 1;
			Iterator<ObjectEntry> entriesIterator = testObj.getEntriesSet().iterator();
			clicker:
				while (entriesIterator.hasNext()) {
					ObjectEntry objectEntry = entriesIterator.next();
					List<WebElement> elems = objectEntry.getValue();
					try {
						find = true;
						String[] priceValue = new String[30];
						int productCount = 0;
						for (int j = 0; j < elems.size(); j++) {
							String[] an = elems.get(j)
									.getAttribute("name").split(",");
							for (int k = 0; k < an.length; k++) {
								if (an[k].contains("Rs."))
									priceValue[productCount++] = (an[k]
											.replace("Rs.", ""))
											.replace(" ", "");
							}
						}
						for (int n = 1; n < productCount; n++) {
							if (order.equals("hightolow")) {
								if (Double
										.parseDouble(priceValue[n - 1]) >= Double
										.parseDouble(priceValue[n])) {
									inOrder = "pass";
								} else {
									inOrder = "notpass";
									break;
								}
							} else if (order.equals("lowtohigh")) {
								if (Double
										.parseDouble(priceValue[n - 1]) <= Double
										.parseDouble(priceValue[n])) {
									inOrder = "pass";
								} else {
									inOrder = "notpass";
									break;
								}
							}
						}
						if (find == true) {
									// report
							if (inOrder.equals("pass")) {
								snapdealMobileStepReportingStatus = true;	
							} else {
								snapdealMobileStepReportingStatus = false;
							}
							break clicker;
						}
					} catch (Exception ex) {
						continue;
					}
					if (i == testObj.getMaxSeq()) {
						snapdealMobileStepReportingStatus = false;
						throw new Exception(
								"No Locator could be clicked for the element "
										+ testObj.ObjName);
					}
					i++;
			}
		} catch (Exception e) {
			snapdealMobileStepReportingStatus = false;
			e.printStackTrace();
		}
	}

	/**
	 * Method to get the text of an element
	 * @param testObj {@link TestObj} object
	 * @param text text of element
	 * @return 
	 */
	public static String getElemntText(TestObj testObj, String text) {
		String elemText = "";
		try {
			testObj.setMaxSequence();
			int i = 1;
			Iterator<ObjectEntry> entriesIterator = testObj.getEntriesSet().iterator();
			clicker:
				while (entriesIterator.hasNext()) {
					ObjectEntry objectEntry = entriesIterator.next();
					Iterator<WebElement> listItr = (Iterator<WebElement>) objectEntry
							.getValue().iterator();
					while (listItr.hasNext()) {
						WebElement elem = listItr.next();
						try {
								if (elem.getAttribute("text").contains(text)) {
									elemText = elem.getAttribute("name");
									snapdealMobileStepReportingStatus = true;
									break clicker;
								
								}else if(elem.getText().contains(text)){
									elemText = elem.getText();	
									snapdealMobileStepReportingStatus = true;
									break clicker;
								}
								
							
						} catch (Exception e) {
							continue;
						}
					}
					if (i == testObj.getMaxSeq()) {
						snapdealMobileStepReportingStatus = false;
						throw new Exception("No Locator found for the element "
								+ testObj.ObjName);
					}
					i++;
				}
		} catch (Exception e) {
			snapdealMobileStepReportingStatus = false;
			e.printStackTrace();
		}
		return elemText;
	}

	/**
	 * Method to get the element's text using index
	 * @param testObj {@link TestObj} Object
	 * @param index index of the element
	 * @return text of the element
	 */
	public static String getElemntTextUsingIndex(TestObj testObj, int index) {
		String elemText = "";
		try {
			testObj.setMaxSequence();
			int i = 1;
			Iterator<ObjectEntry> entriesIterator = testObj.getEntriesSet().iterator();
			clicker: 
				while (entriesIterator.hasNext()) {
					ObjectEntry objectEntry = entriesIterator.next();
					List<WebElement> elems = objectEntry.getValue();
					
					try {
						elemText = elems.get(index).getText();
						snapdealMobileStepReportingStatus = true;
						break clicker;
					} catch (Exception e) {
						
					}
					if (i == testObj.getMaxSeq()) {
						snapdealMobileStepReportingStatus = false;
						throw new Exception("No Locator found for the element "
								+ testObj.ObjName);
					}
					i++;
				}
			
		} catch (Exception e) {
			snapdealMobileStepReportingStatus = false;
			e.printStackTrace();
		}
		return elemText;
	}

	/**
	 * Method to get text of a {@link TestObj} using its Object
	 * @param testObj {@link TestObj}
	 * @return text
	 */
	public static String getElementTextValue(TestObj testObj) {
		String elemText = "";
		try {
			testObj.setMaxSequence();
			int i = 1;
			Iterator<ObjectEntry> entriesIterator = testObj.getEntriesSet().iterator();
			clicker:
				while (entriesIterator.hasNext()) {
					ObjectEntry objectEntry = entriesIterator.next();
					Iterator<WebElement> listItr = (Iterator<WebElement>) objectEntry
							.getValue().iterator();
							while (listItr.hasNext()) {
								WebElement elem = listItr.next();
								try {
									elemText = elem.getAttribute("name");
									
									if(elemText.equals("")){
										elemText = elem.getText();
									}
									
									if(elemText.equals("")){
										snapdealMobileStepReportingStatus = false;
										break clicker;
									}else{
										snapdealMobileStepReportingStatus = true;
										break clicker;
									}
										
									
								} catch (Exception e) {
									continue;
								}
							}
							if (i == testObj.getMaxSeq()) {
								snapdealMobileStepReportingStatus = false;
								throw new Exception("No Locator found for the element "
										+ testObj.ObjName);
							}
							i++;
						}
					

		} catch (Exception e) {
			snapdealMobileStepReportingStatus = false;
			e.printStackTrace();
		}
		return elemText;
	}

	/**
	 * Method to get number from a {@link String} object
	 * @param src {@link String} Object
	 * @return 
	 */
	public static String extractNumberFromString(String src) {
		StringBuilder builder = new StringBuilder();
		boolean isDigitFound = false;
		for (int i = 0; i < src.length(); i++) {
			char c = src.charAt(i);
			if (c == '(') {
				return builder.toString();
			}
			if (Character.isDigit(c)) {
				builder.append(c);
				isDigitFound = true;
			}
			if((isDigitFound)&&(c == '.')){
				builder.append(c);
			}else if((isDigitFound)&& !Character.isDigit(c)){
				isDigitFound = false;
			}
			
		}
		return builder.toString();
	}

	/**
	 * Method to convert a {@link String} Object to {@link Integer}
	 * @param src {@link String} Object
	 * @return
	 */
	public static int convertStringToInt(String src){
		int intValue = 0;
		try{
			intValue =  Integer.parseInt(src);
		}catch(Exception ex){
			ex.printStackTrace();
		}
		return intValue;
	}
	
	public static void shakeScreen(TestCase testCase){
		((IOSDriver)testCase.getDriverFactory().getDriver()).shake();
	}
	
	/**
	 * Method to check prices of UI Object after applying a filter
	 * @param type filter type
	 * @param Range range of this filter
	 * @param testObj {@link TestObj} Object
	 */
	public static void checkFilterPrice(String type, String Range, TestObj testObj) {
		Boolean find = false;
		try {
			boolean inPrizeFilter = false;
			testObj.setMaxSequence();
			int i = 1;
			Iterator<ObjectEntry> entriesIterator = testObj.getEntriesSet().iterator();
			clicker:
				while (entriesIterator.hasNext()) {
					ObjectEntry objectEntry = entriesIterator.next();
					Iterator<WebElement> listItr = (Iterator<WebElement>) objectEntry
							.getValue().iterator();
					while (listItr.hasNext()) {
						WebElement elem = listItr.next();
						try {
							find = true;
							if (type.equalsIgnoreCase("below")) {
								if (Double.parseDouble(elem.getText()
										.replace("Rs. ", "")) < Double
										.parseDouble(Range.replace(
												"Rs. ", ""))) {
									inPrizeFilter = true;	
								} else {
									inPrizeFilter = false;
									break;
								}
							}
						} catch (Exception e) {
							continue;
						}
					}
					if (find == true) {
						if (find == true) {
							if (inPrizeFilter == true) {
								snapdealMobileStepReportingStatus = true;
							} else {
								snapdealMobileStepReportingStatus = false;
							}
							break clicker;
						}
					}
					if (i == testObj.getMaxSeq()) {
						snapdealMobileStepReportingStatus = false;
						throw new Exception(
								"No Locator could be clicked for the element "
										+ testObj.ObjName);
					}
					i++;
				}
		} catch (Exception e) {
			snapdealMobileStepReportingStatus = false;
			e.printStackTrace();
		}
	}

	/**
	 * To check filters for iOS as {@link #checkFilterPrice(String, String, TestObj)}
	 * @param type Type of filter
	 * @param Rang range of filter
	 * @param testObj {@link TestObj} Object
	 */
	public static void checkFilterPriceForIos(String type, String Rang, TestObj testObj) {
		
		String inOrder = "notpass";
		Boolean find = false;
		try {
			testObj.setMaxSequence();
			int i = 1;
			Iterator<ObjectEntry> entriesIterator = testObj.getEntriesSet().iterator();
			clicker: 
				while (entriesIterator.hasNext()) {
					ObjectEntry objectEntry = entriesIterator.next();
					List<WebElement> elems = objectEntry.getValue();
							try {

								find = true;
								String[] priceValue = new String[30];
								int productCount = 0;
								for (int j = 0; j < elems.size(); j++) {
									String[] an = elems.get(j)
											.getAttribute("label").split(",");
									for (int k = 0; k < an.length; k++) {
										if (an[k].contains("Rs."))
											priceValue[productCount++] = (an[k]
													.replace("Rs.", ""))
													.replace(" ", "");
									}
								}

								for (int n = 0; n < productCount; n++) {

									if (type.equals("below")) {
										if (Double
												.parseDouble(Rang) >= Double
												.parseDouble(priceValue[n])) {
											inOrder = "pass";
										} else {
											inOrder = "notpass";
											break;
										}
									} 
									
								}

								if (find == true) {
									// report
									if (inOrder.equals("pass")) {
										snapdealMobileStepReportingStatus = true;

									} else {
										snapdealMobileStepReportingStatus = false;
									}
									break clicker;
								}

							} catch (Exception ex) {

								continue;
							}
						}
			

				if (i == testObj.getMaxSeq()) {
					snapdealMobileStepReportingStatus = false;
					throw new Exception(
							"No Locator could be clicked for the element "
									+ testObj.ObjName);
				}
		
		} catch (Exception e) {
			snapdealMobileStepReportingStatus = false;
			e.printStackTrace();
		}
	}
	
	/**
	 * Method to type text using {@link JavaScriptExecutor} in iOS
	 * @param text text to be typed
	 * @param testCase {@link TestCase} Object
	 */
	public static void typeTextOnIos(String text, TestCase testCase) {
		try {
			JavascriptExecutor jsExecutor = (JavascriptExecutor) testCase.getDriverFactory().getDriver();

			String script = "var vKeyboard = target.frontMostApp().keyboard();"

			+ "vKeyboard.setInterKeyDelay(0.1);"

			+ "vKeyboard.typeString(\"" + text + "\");";

			jsExecutor.executeScript(script);
			snapdealMobileStepReportingStatus = true;
		} catch (Exception e) {
			snapdealMobileStepReportingStatus = false;
			e.printStackTrace();
		}
	}

	/**
	 * Method to verify if the {@link TestObj} with the given text is present
	 * @param to {@link TestObj} Object
	 * @param text text of the element
	 */
	public static void verifyElementPresent(TestObj to, String text) {
		String elemText = "";
		elemText = getElemntText(to, text);;
		if (elemText.contains(text)) {
			snapdealMobileStepReportingStatus = true;
		} else {
			snapdealMobileStepReportingStatus = false;
		}
	}

	/**
	 * Method to verify if the {@link TestObj} is displayed
	 * @param testObj
	 * @throws Exception 
	 */
	public static void elementExistOrNot(TestObj testObj) throws Exception {
		try {
			testObj.setMaxSequence();
			int i = 1;
			Iterator<ObjectEntry> entriesIterator = testObj.getEntriesSet().iterator();
			clicker:
				while (entriesIterator.hasNext()) {
					ObjectEntry objectEntry = entriesIterator.next();
					Iterator<WebElement> listItr = (Iterator<WebElement>) objectEntry
							.getValue().iterator();
					while (listItr.hasNext()) {
						WebElement elem = listItr.next();
						try {
							if (elem.isDisplayed()) {
								snapdealMobileStepReportingStatus = true;
								break clicker;
							}
						} catch (Exception e) {
							continue;
						}
					}
					if (i == testObj.getMaxSeq()) {
						snapdealMobileStepReportingStatus = false;
						throw new Exception(
								"No Locator could be found for the element "
										+ testObj.ObjName);
					}
					i++;
				}
		} catch (Exception e) {
			snapdealMobileStepReportingStatus = false;
			e.printStackTrace();
			throw new Exception("");
		}
	}
	
	/**
	 * Method to verify if the {@link TestObj} is displayed
	 * @param testObj
	 * @throws Exception 
	 */
	public static boolean elementPresentOrNot(TestObj testObj){
		boolean flag = false;
		try {
			testObj.setMaxSequence();
			int i = 1;
			Iterator<ObjectEntry> entriesIterator = testObj.getEntriesSet().iterator();
			clicker:
				while (entriesIterator.hasNext()) {
					ObjectEntry objectEntry = entriesIterator.next();
					Iterator<WebElement> listItr = (Iterator<WebElement>) objectEntry
							.getValue().iterator();
					while (listItr.hasNext()) {
						WebElement elem = listItr.next();
						try {
							if (elem.isDisplayed()) {
								snapdealMobileStepReportingStatus = true;
								flag = true;
								break clicker;
							}
						} catch (Exception e) {
							continue;
						}
					}
					if (i == testObj.getMaxSeq()) {
						snapdealMobileStepReportingStatus = false;
                        flag = false;
					}
					i++;
				}
		} catch (Exception e) {
			snapdealMobileStepReportingStatus = false;
			e.printStackTrace();
			flag = false;
		}
		return flag;
	}
	
	/**
	 * This method will verify the presence of passed webElement 
	 * @author ankit.gupta04
	 * @param we
	 * @return
	 */
	public static boolean verifyPresenceOfElement(WebElement we){
		boolean flag = false;
		try{
			if (we.isDisplayed()){
				if(we.isEnabled()){
					flag = true;
				}
			}
		}catch(Exception e){
//          e.printStackTrace();
		}
		return flag;
	}
	
	/**
	 * This method will click the passed element path if visible otherwise throw exception
	 * @author ankit.gupta04
	 * @param testCaseObj
	 * @param elementPath
	 * @param wait
	 * @param parms
	 * @throws Exception
	 */
	public static boolean clickOnElement(TestCase testCaseObj, String elementPath, int wait, String...parms ) throws Exception{
		boolean flag = false;
		WebElement we = testCaseObj.getTestCaseCache().getWebElement(elementPath, wait, parms);
		if(verifyPresenceOfElement(we)){
			try{
				we.click();
				flag = true;
				sleep(200);
				testCaseObj.report.teststepreporting("Clikced on: "+elementPath+":"+flag, "PASS", "INFO");
			}catch(Exception e){
				testCaseObj.report.teststepreporting("Exception occurred while clicking on: "+elementPath+" "+e.getMessage(), "FAIL", "INFO");
			}
		}
		else{
			testCaseObj.report.teststepreporting("Unable to click on: "+elementPath, "FAIL", "INFO");
			throw new Exception("Unable to click on: "+elementPath);
		}
		return flag;
	}
	
	/**
	 * This method will enter the text on the given element path
	 * @author ankit.gupta04
	 * @param testCaseObj
	 * @param text
	 * @param elementPath
	 * @param wait
	 * @param parms
	 * @throws Exception
	 */
	public static boolean enterTextOnElement(TestCase testCaseObj, String text, String elementPath, int wait, String...parms) throws Exception{
		boolean flag = false;
		WebElement we = testCaseObj.getTestCaseCache().getWebElement(elementPath, wait, parms);
		if(verifyPresenceOfElement(we)){
			try{
			we.click();
			we.clear();
			if(!we.getText().isEmpty()){
				we.click();
				we.clear();
			}
			we.click();
			we.sendKeys(text);
			TestCase.hideKeyBoard(testCaseObj);
			flag = true;
				testCaseObj.report.teststepreporting("Entered "+text+" on "+elementPath+" : "+flag, "PASS", "INFO");	
			}catch(Exception e){
				try{
				we.sendKeys(text);
				TestCase.hideKeyBoard(testCaseObj);
				flag = true;
				testCaseObj.report.teststepreporting("Entered "+text+" on "+elementPath+" : "+flag, "PASS", "INFO");
				}
				catch(Exception e1){
				testCaseObj.report.teststepreporting("Exception occurred while entering text into: "+elementPath+" "+e1.getMessage(), "FAIL", "INFO");
				TestCase.hideKeyBoard(testCaseObj);
				}
			}
		}
		else{
			testCaseObj.report.teststepreporting("Unable to enter text on: "+elementPath, "FAIL", "INFO");
			throw new Exception("Unable to enter Text on: "+elementPath);
		}
		return flag;
	}
	
	
	/**
	 * This method will return the text of passed element path
	 * @author ankit.gupta04
	 * @param testCaseObj
	 * @param elementPath
	 * @param wait
	 * @param parms
	 * @return
	 * @throws Exception
	 */
	public static String getText(TestCase testCaseObj, String elementPath, int wait, String...parms ) throws Exception{
		String text = "";
		WebElement we = testCaseObj.getTestCaseCache().getWebElement(elementPath, wait, parms);
		if(verifyPresenceOfElement(we)){
			try{
			text = we.getText();
			if(text.isEmpty()){
				text = we.getAttribute("text");
			}
			}catch(Exception e){
				testCaseObj.report.teststepreporting("Exception occurred while geting text of: "+elementPath+" "+e.getMessage(), "FAIL", "INFO");
			}
		}
		else{
			testCaseObj.report.teststepreporting("Unable to get text of: "+elementPath, "FAIL", "INFO");
			throw new Exception("Unable to get text of : "+elementPath);
		}
		return text;
	}
	
	public static void elementSelectedOrNot(TestObj testObj) throws Exception {
		try {
			testObj.setMaxSequence();
			int i = 1;
			Iterator<ObjectEntry> entriesIterator = testObj.getEntriesSet().iterator();
			clicker:
				while (entriesIterator.hasNext()) {
					ObjectEntry objectEntry = entriesIterator.next();
					Iterator<WebElement> listItr = (Iterator<WebElement>) objectEntry
							.getValue().iterator();
					while (listItr.hasNext()) {
						WebElement elem = listItr.next();
						try {
							if (elem.isSelected()) {
								snapdealMobileStepReportingStatus = true;
								break clicker;
							}
						} catch (Exception e) {
							continue;
						}
					}
					if (i == testObj.getMaxSeq()) {
						snapdealMobileStepReportingStatus = false;
						throw new Exception(
								"No Locator could be clicked for the element "
										+ testObj.ObjName);
					}
					i++;
				}
		} catch (Exception e) {
			snapdealMobileStepReportingStatus = false;
			e.printStackTrace();
			throw new Exception("");
		}
	}

	public static void elementEnabledOrNot(TestObj testObj) throws Exception {
		try {
			testObj.setMaxSequence();
			int i = 1;
			Iterator<ObjectEntry> entriesIterator = testObj.getEntriesSet().iterator();
			clicker:
				while (entriesIterator.hasNext()) {
					ObjectEntry objectEntry = entriesIterator.next();
					Iterator<WebElement> listItr = (Iterator<WebElement>) objectEntry
							.getValue().iterator();
					while (listItr.hasNext()) {
						WebElement elem = listItr.next();
						try {
							if (elem.isEnabled()) {
								snapdealMobileStepReportingStatus = true;
								break clicker;
							}
						} catch (Exception e) {
							continue;
						}
					}
					if (i == testObj.getMaxSeq()) {
						snapdealMobileStepReportingStatus = false;
						throw new Exception(
								"No Locator could be clicked for the element "
										+ testObj.ObjName);
					}
					i++;
				}
		} catch (Exception e) {
			snapdealMobileStepReportingStatus = false;
			e.printStackTrace();
			throw new Exception("");
		}
	}

	/**
	 * Method to navigate back
	 * @param testCase {@link TestCase} Object
	 */
	public static void backButton(TestCase testCase) {
		try {
			testCase.getDriverFactory().getDriver().navigate().back();
			snapdealMobileStepReportingStatus = true;
		} catch (Exception ex) {
			snapdealMobileStepReportingStatus = false;
		}
	}

	public static void swipeHorizontally(int startX, int startY, int endX, int endY, TestCase testCase){
		testCase.getDriverFactory().getAppiumDriver().swipe( startX, startY, endX, endY,2000);
		
	}
	
	/**
	 * @author ankit.gupta04
	 * @param startX
	 * @param startY
	 * @param endX
	 * @param endY
	 * @param testCaseObj
	 * @param elementPath
	 */
	public static boolean swipeBottomTillElement(TestCase testCaseObj, String elementPath, String...parms ){
		boolean flag = false;
		int counter = 0;
		boolean swipe = false;
		Dimension dimensions = testCaseObj.getDriverFactory().getAppiumDriver().manage().window().getSize();
		//Find starty point which is at bottom side of screen.
		int starty = (int) (dimensions.height * 0.50);
		//Find endy point which is at top side of screen.
		int endy = (int) (dimensions.height * 0.30);
		//Find horizontal point where you wants to swipe. It is in middle of screen width.
		int startx = dimensions.width / 2;
		System.out.println("starty = " + starty + " ,endy = " + endy + " , startx = " + startx);
		do{
			TestObj objElement = testCaseObj.getTestCaseCache().lookUp(elementPath,400, parms);
			try {
				objElement = testCaseObj.getTestCaseCache().updateElementAfterWait(objElement,400, parms);
				TestCase.elementExistOrNot(objElement);
				Thread.sleep(100);
				if (!TestCase.elementPresentOrNot(objElement)){
					swipe  = true;
					TestCase.elementExistOrNot(objElement);
				}
				objElement = testCaseObj.getTestCaseCache().updateElementAfterWait(objElement,400, parms);
				flag=true;
				List<WebElement> eleList = TestCase.getListOfElement(objElement);
				WebElement we = eleList.get(0);
				int webStartY = we.getLocation().getY();
				System.out.println("webStartY = " + webStartY * (.88) + " ,endy = " + dimensions.height*(0.63) + " , startx = " + startx);
//				TestCase.swipeHorizontally((int) startx, (int) (webStartY * (.86)), (int) startx, (int) ((dimensions.height)*(0.63)), testCaseObj);
				testCaseObj.getDriverFactory().getAppiumDriver().swipe( (int) startx, (int) (webStartY * (.86)), (int) startx, (int) ((dimensions.height)*(0.63)),1000);
				swipeTillUpperSeventyPercent(testCaseObj, elementPath, parms);
				TestCase.sleep(1000);
			} catch (Exception e) {
				if (swipe)
//					TestCase.swipeHorizontally((int) startx, (int) (dimensions.height * 0.55), (int) startx, (int) (dimensions.height * 0.42), testCaseObj);
				testCaseObj.getDriverFactory().getAppiumDriver().swipe( (int) startx, (int) (dimensions.height * 0.55), (int) startx, (int) (dimensions.height * 0.42),1000);
				else
//					TestCase.swipeHorizontally((int) startx, (int) starty, (int) startx, (int) endy, testCaseObj);
				testCaseObj.getDriverFactory().getAppiumDriver().swipe( (int) startx, (int) starty, (int) startx, (int) endy,1000);
//				sleep(100);
				counter++;
			}
		} while(!flag && counter<30);
		if (counter == 30 && !flag){
			scrollToTop(testCaseObj);
			scrollToTop(testCaseObj);
			scrollToTop(testCaseObj);
			scrollToTop(testCaseObj);
			scrollToTop(testCaseObj);
			scrollToTop(testCaseObj);
		}
		return flag;
	}

	/**
	 * @author ankit.gupta04
	 * @param startX
	 * @param startY
	 * @param endX
	 * @param endY
	 * @param testCaseObj
	 * @param elementPath
	 */
	public static boolean swipeBottomTillLastGivenElement(TestCase testCaseObj, boolean ifFailedScroolToTop, String lastElemntPath, String elementPath, String...parms ){
		boolean flag = false;
		int counter = 0;
		boolean swipe = false;
		Dimension dimensions = testCaseObj.getDriverFactory().getAppiumDriver().manage().window().getSize();
		//Find starty point which is at bottom side of screen.
		int starty = (int) (dimensions.height * 0.50);
		//Find endy point which is at top side of screen.
		int endy = (int) (dimensions.height * 0.30);
		//Find horizontal point where you wants to swipe. It is in middle of screen width.
		int startx = dimensions.width / 2;
		System.out.println("starty = " + starty + " ,endy = " + endy + " , startx = " + startx);
		do{
			TestObj objElement = testCaseObj.getTestCaseCache().lookUp(elementPath,400, parms);
			try {
				objElement = testCaseObj.getTestCaseCache().updateElementAfterWait(objElement,400, parms);
				TestCase.elementExistOrNot(objElement);
				Thread.sleep(100);
				if (!TestCase.elementPresentOrNot(objElement)){
					swipe  = true;
					TestCase.elementExistOrNot(objElement);
				}
				objElement = testCaseObj.getTestCaseCache().updateElementAfterWait(objElement,400, parms);
				flag=true;
				List<WebElement> eleList = TestCase.getListOfElement(objElement);
				WebElement we = eleList.get(0);
				int webStartY = we.getLocation().getY();
				System.out.println("webStartY = " + webStartY * (.88) + " ,endy = " + dimensions.height*(0.63) + " , startx = " + startx);
//				TestCase.swipeHorizontally((int) startx, (int) (webStartY * (.86)), (int) startx, (int) ((dimensions.height)*(0.63)), testCaseObj);
				testCaseObj.getDriverFactory().getAppiumDriver().swipe( (int) startx, (int) (webStartY * (.86)), (int) startx, (int) ((dimensions.height)*(0.63)),1000);
				TestCase.sleep(1000);
			} catch (Exception e) {
				if (swipe)
//					TestCase.swipeHorizontally((int) startx, (int) (dimensions.height * 0.55), (int) startx, (int) (dimensions.height * 0.42), testCaseObj);
				testCaseObj.getDriverFactory().getAppiumDriver().swipe( (int) startx, (int) (dimensions.height * 0.55), (int) startx, (int) (dimensions.height * 0.42),1000);
				else{
					objElement = testCaseObj.getTestCaseCache().lookUp(lastElemntPath,200);
					objElement = testCaseObj.getTestCaseCache().updateElementAfterWait(objElement,200);
					if (elementPresentOrNot(objElement)){
						break;
					}else
//					TestCase.swipeHorizontally((int) startx, (int) starty, (int) startx, (int) endy, testCaseObj);
				testCaseObj.getDriverFactory().getAppiumDriver().swipe( (int) startx, (int) starty, (int) startx, (int) endy,1000);
				}
//				sleep(100);
				counter++;
			}
		} while(!flag && counter<30);
		if (!flag && ifFailedScroolToTop){
			for (int i= 0; i<=counter/5; i++){
			scrollToTop(testCaseObj);
			}
		}
		return flag;
	}
	
	
	public static boolean swipeTopTillElement(TestCase testCaseObj, boolean ifFailedScroolToTop, String elementPath, String...parms ){
		boolean flag = false;
		int counter = 0;
		boolean swipe = false;
		Dimension dimensions = testCaseObj.getDriverFactory().getAppiumDriver().manage().window().getSize();
		//Find starty point which is at bottom side of screen.
		int starty = (int) (dimensions.height * 0.50);
		//Find endy point which is at top side of screen.
		int endy = (int) (dimensions.height * 0.30);
		//Find horizontal point where you wants to swipe. It is in middle of screen width.
		int startx = dimensions.width / 2;
		System.out.println("starty = " + starty + " ,endy = " + endy + " , startx = " + startx);
		do{
			TestObj objElement = testCaseObj.getTestCaseCache().lookUp(elementPath,400, parms);
			try {
				objElement = testCaseObj.getTestCaseCache().updateElementAfterWait(objElement,400, parms);
				TestCase.elementExistOrNot(objElement);
				Thread.sleep(100);
				if (!TestCase.elementPresentOrNot(objElement)){
					swipe  = true;
					TestCase.elementExistOrNot(objElement);
				}
//				objElement = testCaseObj.getTestCaseCache().updateElementAfterWait(objElement,400, parms);
				flag=true;
//				List<WebElement> eleList = TestCase.getListOfElement(objElement);
//				WebElement we = eleList.get(0);
//				int webStartY = we.getLocation().getY();
//				System.out.println("webStartY = " + webStartY * (.88) + " ,endy = " + dimensions.height*(0.63) + " , startx = " + startx);
////				TestCase.swipeHorizontally((int) startx, (int) (webStartY * (.86)), (int) startx, (int) ((dimensions.height)*(0.63)), testCaseObj);
//				testCaseObj.getDriverFactory().getAppiumDriver().swipe( (int) startx, (int)((dimensions.height)*(0.63)) , (int) startx, (int) (webStartY * (.86)),1000);
				swipeToMiddle(testCaseObj, elementPath, parms);
				TestCase.sleep(1000);
			} catch (Exception e) {
				if (swipe)
//					TestCase.swipeHorizontally((int) startx, (int) (dimensions.height * 0.55), (int) startx, (int) (dimensions.height * 0.42), testCaseObj);
				testCaseObj.getDriverFactory().getAppiumDriver().swipe( (int) startx, (int) (dimensions.height * 0.42), (int) startx, (int)  (dimensions.height * 0.55),1000);
				else{
//					TestCase.swipeHorizontally((int) startx, (int) starty, (int) startx, (int) endy, testCaseObj);
				testCaseObj.getDriverFactory().getAppiumDriver().swipe( (int) startx, (int) endy, (int) startx, (int) starty,1000);
				}
//				sleep(100);
				counter++;
			}
		} while(!flag && counter<30);
		if (!flag && ifFailedScroolToTop){
			for (int i= 0; i<=counter/5; i++){
			scrollToTop(testCaseObj);
			}
		}
		return flag;
	}
	
	
	/**
	 * This method will swipe the element and take that visible element into the middle of screen
	 * @author ankit.gupta04
	 * @param testCaseObj
	 * @param element
	 * @param parms
	 */
	public static void swipeToMiddle(TestCase testCaseObj, String element, String...parms){
		Dimension dimensions = testCaseObj.getDriverFactory().getAppiumDriver().manage().window().getSize();
		//Find starty point which is at bottom side of screen.
		int startx = dimensions.width / 2;
		try {
			TestObj objElement = testCaseObj.getTestCaseCache().lookUp(element,400, parms);
			objElement = testCaseObj.getTestCaseCache().updateElementAfterWait(objElement,400, parms);
			TestCase.elementExistOrNot(objElement);
			List<WebElement> eleList = TestCase.getListOfElement(objElement);
			WebElement we = eleList.get(0);
			int webStartY = we.getLocation().getY();
			if (we.getLocation().getY() > (dimensions.height * 0.75)){
				System.out.println("webStartY = " + webStartY + " ,endy = " + dimensions.height*(0.40) + " , startx = " + startx);
//				TestCase.swipeHorizontally((int) startx, (int) (webStartY), (int) startx, (int) ((dimensions.height)*(0.40)), testCaseObj);	
				testCaseObj.getDriverFactory().getAppiumDriver().swipe( (int) startx, (int) (webStartY * (.86)), (int) startx, (int) ((dimensions.height)*(0.40)),1000);
			} 
			else if (we.getLocation().getY() < (dimensions.height * 0.30)){
				System.out.println("webStartY = " + webStartY + " ,endy = " + dimensions.height*(0.45) + " , startx = " + startx);
//				TestCase.swipeHorizontally((int) startx, (int) (webStartY), (int) startx, (int) ((dimensions.height)*(0.45)), testCaseObj);
				testCaseObj.getDriverFactory().getAppiumDriver().swipe( (int) startx, (int) (webStartY * (.86)), (int) startx, (int) ((dimensions.height)*(0.45)),1000);
			}
			else{
				System.out.println("webStartY = " + webStartY  + " ,endy = " + dimensions.height*(0.50) + " , startx = " + startx);
//				TestCase.swipeHorizontally((int) startx, (int) (webStartY), (int) startx, (int) ((dimensions.height)*(0.50)), testCaseObj);	
				testCaseObj.getDriverFactory().getAppiumDriver().swipe( (int) startx, (int) (webStartY * (.86)), (int) startx, (int) ((dimensions.height)*(0.50)),1000);
			}
		} catch (Exception e) {
			System.out.println("Unable to swipe To Middle "+e.getMessage());
		}
	}
	
	/**
	 * This method will scroll the visible element to Upper 70% of window
	 * @author ankit.gupta04
	 * @param testCaseObj
	 * @param element
	 * @param parms
	 */
	public static void swipeTillUpperSeventyPercent(TestCase testCaseObj, String element, String...parms){
		Dimension dimensions = testCaseObj.getDriverFactory().getAppiumDriver().manage().window().getSize();
		//Find starty point which is at bottom side of screen.
		int startx = dimensions.width / 2;
		try {
			TestObj objElement = testCaseObj.getTestCaseCache().lookUp(element,400, parms);
			objElement = testCaseObj.getTestCaseCache().updateElementAfterWait(objElement,400, parms);
			TestCase.elementExistOrNot(objElement);
			List<WebElement> eleList = TestCase.getListOfElement(objElement);
			WebElement we = eleList.get(0);
			int webStartY = we.getLocation().getY();
			if (we.getLocation().getY() > (dimensions.height * 0.40)){
				System.out.println("webStartY = " + webStartY + " ,endy = " + dimensions.height*(0.40) + " , startx = " + startx);
//				TestCase.swipeHorizontally((int) startx, (int) (webStartY), (int) startx, (int) ((dimensions.height)*(0.30)), testCaseObj);
				testCaseObj.getDriverFactory().getAppiumDriver().swipe( (int) startx, (int) (webStartY * (.86)), (int) startx, (int) ((dimensions.height)*(0.25)),1000);
			} 
			else if (we.getLocation().getY() < (dimensions.height * 0.50) /*&& we.getLocation().getY() < (dimensions.height * 0.35) */){
//				System.out.println("webStartY = " + webStartY + " ,endy = " + dimensions.height*(0.30) + " , startx = " + startx);
//				TestCase.swipeHorizontally((int) startx, (int) (webStartY), (int) startx, (int) ((dimensions.height)*(0.30)), testCaseObj);
				testCaseObj.getDriverFactory().getAppiumDriver().swipe( (int) startx, (int) (webStartY * (.86)), (int) startx, (int) ((dimensions.height)*(0.25)),1000);
			}
			else if(we.getLocation().getY() < (dimensions.height * 0.18)){
				testCaseObj.getDriverFactory().getAppiumDriver().swipe( (int) startx, (int) (webStartY * (.86)), (int) startx, (int) ((dimensions.height)*(0.25)),1000);
			}
			else{
				System.out.println("webStartY = " + webStartY  + " ,endy = " + dimensions.height*(0.50) + " , startx = " + startx);
//				TestCase.swipeHorizontally((int) startx, (int) (webStartY), (int) startx, (int) ((dimensions.height)*(0.50)), testCaseObj);	
				testCaseObj.getDriverFactory().getAppiumDriver().swipe( (int) startx, (int) (webStartY * (.86)), (int) startx, (int) ((dimensions.height)*(0.400)),1000);
			}
		} catch (Exception e) {
			System.out.println("Unable to swipe To Upper 70% "+e.getMessage());
		}
	}
	
	/**
	 * This method will move your screen to Top
	 * @author ankit.gupta04
	 * @param testCaseObj
	 */
	public static void scrollToTop(TestCase testCaseObj){
		
		int counter = 0;
		Dimension dimensions = testCaseObj.getDriverFactory().getAppiumDriver().manage().window().getSize();
		 //Find starty point which is at bottom side of screen.
		 int endy = (int) (dimensions.height * 0.70);

		//Find endy point which is at top side of screen.
		  int starty = (int) (dimensions.height * 0.25);
		  
		//Find horizontal point where you wants to swipe. It is in middle of screen width.
		int startx = dimensions.width / 2;
		System.out.println("starty = " + starty + " ,endy = " + endy + " , startx = " + startx);
        do{
//				TestCase.swipeHorizontally((int) startx, (int) starty, (int) startx, (int) endy, testCaseObj);
				testCaseObj.getDriverFactory().getAppiumDriver().swipe( (int) startx, (int) starty, (int) startx, (int) endy,1000);
					counter++;
				
		} while(counter<5);
	}
	
	/**
	 * This method will move your screen to Bottom
	 * @author ankit.gupta04
	 * @param testCaseObj
	 */
	public static void scrollToBottom(TestCase testCaseObj){
		
		int counter = 0;
		Dimension dimensions = testCaseObj.getDriverFactory().getAppiumDriver().manage().window().getSize();
		 //Find starty point which is at bottom side of screen.
		 int endy = (int) (dimensions.height * 0.60);

		//Find endy point which is at top side of screen.
		  int starty = (int) (dimensions.height * 0.35);
		  
		//Find horizontal point where you wants to swipe. It is in middle of screen width.
		int startx = dimensions.width / 2;
		System.out.println("starty = " + starty + " ,endy = " + endy + " , startx = " + startx);
        do{
				TestCase.swipeHorizontally((int) startx, (int) endy, (int) startx, (int) starty, testCaseObj);
					counter++;
				
		} while(counter<5);
	}
	
	
	public static boolean swipeLeftTillGivenElement(TestCase testCaseObj, WebElement we, boolean reverse, String lastElemntPath, String...parms ){
		boolean flag = false;
		int counter = 0;
		boolean swipe = false;
		Dimension dimensions = testCaseObj.getDriverFactory().getAppiumDriver().manage().window().getSize();
		//		//Find starty point which is at bottom side of screen.
		//		int starty = (int) (dimensions.height * 0.60);
		//		//Find endy point which is at top side of screen.
		//		int endy = (int) (dimensions.height * 0.30);
		//		//Find horizontal point where you wants to swipe. It is in middle of screen width.
		int totalWidth = dimensions.width;
		int Y = we.getLocation().getY();
		int endX = we.getLocation().getX();
		System.out.println("startX = " + (totalWidth-20) + " ,Y = " +Y + " , endX = " + endX);
		do{
			TestObj objElement = testCaseObj.getTestCaseCache().lookUp(lastElemntPath,400, parms);
			try {
				objElement = testCaseObj.getTestCaseCache().updateElementAfterWait(objElement,400, parms);
				TestCase.elementExistOrNot(objElement);
				Thread.sleep(100);
				if (!TestCase.elementPresentOrNot(objElement)){
					swipe  = true;
					TestCase.elementExistOrNot(objElement);
				}
				objElement = testCaseObj.getTestCaseCache().updateElementAfterWait(objElement,400, parms);
				flag=true;
				List<WebElement> eleList = TestCase.getListOfElement(objElement);
				WebElement lastWe = eleList.get(0);
				int webStartY = lastWe.getLocation().getY();
				int webStartX = lastWe.getLocation().getX();
				System.out.println("webStartX = " + webStartX + " ,Y = " + webStartY+ " ,endX = " + (totalWidth*(.10)));
				testCaseObj.getDriverFactory().getAppiumDriver().swipe( (int) webStartX, (int) (webStartY), (int) (totalWidth*(.10)), webStartY,1000);
				TestCase.sleep(1000);
			} catch (Exception e) {
				if (!swipe)
					testCaseObj.getDriverFactory().getAppiumDriver().swipe( (int)(totalWidth*(.90)), Y, (int)(totalWidth*(.10)), Y,1000);
				counter++;
			}
		} while(!flag && counter<4);
		if (!flag && reverse){
			for (int i= 0; i<=counter/2; i++){
				testCaseObj.getDriverFactory().getAppiumDriver().swipe( (int)(totalWidth*(.10)), Y, (int)(int)(totalWidth*(.90)), Y,1000);
			}
		}
		return flag;
	}
	
	
	/**
	 * Method to perform swipe Operation
	 * @param swipeValue integer value for swipe
	 * @param testCase {@link TestCase} Object
	 */
	public static void swipe(int swipeValue,TestCase testCase) {
		try {
			Double swipeHight =0.0;
			swipeHight = (double) swipeValue;
			Dimension screenSize = testCase.getDriverFactory().getDriver().manage().window().getSize();
			Double screenWidth = Double.valueOf(String.valueOf(screenSize
					.getWidth())) / 2;
			Double screenHight = Double.valueOf(String.valueOf(screenSize
					.getHeight())) / 2;
			
			if(screenHight + swipeValue > screenHight*2 || swipeValue == 0){
				swipeHight = screenHight/2;
				
			}
			
			JavascriptExecutor js = (JavascriptExecutor) testCase.getDriverFactory().getDriver();

			HashMap<String, Object> swipeObject = new HashMap<String, Object>();
			swipeObject.put("startX", screenWidth);
			swipeObject.put("startY", screenHight + swipeHight);
			swipeObject.put("endX", screenWidth);
			swipeObject.put("endY", screenHight);
			swipeObject.put("duration", 1.8);
			js.executeScript("mobile: swipe", swipeObject);
			snapdealMobileStepReportingStatus = true;
		} catch (Exception ex) {
			snapdealMobileStepReportingStatus = false;
			ex.printStackTrace();
			
		}

	}
	
	/**
	 * Method to close the currently used app
	 * @param testCase {@link TestCase} Object
	 */
	public static void closeApp(TestCase testCase){
		try{
			((AppiumDriver)testCase.getDriverFactory().getDriver()).closeApp();
			snapdealMobileStepReportingStatus = true;
		}catch(Exception ex){
			snapdealMobileStepReportingStatus = false;
			ex.printStackTrace();
		}
	}
	
	/**
	 * Method to launchApp
	 * @param testCase {@link TestCase} Object
	 */
	public static void launchApp(TestCase testCase){
		try{
			((AppiumDriver)testCase.getDriverFactory().getDriver()).launchApp();
			snapdealMobileStepReportingStatus = true;
			testCase.report.teststepreporting("Able to launch app", "PASS", "Should be launch app");
		}catch(Exception ex){
			ex.printStackTrace();
			snapdealMobileStepReportingStatus = false;
			testCase.report.teststepreporting("Not able to launch app", "FAIL", "Should be launch app");
		}
	}
	
	/**
	 * Method to set Network Connection Mode
	 * @param airplanMode flag to set airPlane Mode
	 * @param wifiMode flag to set wifiMode
	 * @param dataMode flag to set dataMaode
	 * @param testCase {@link TestCase} Object
	 */
	public static void setNetworkConnection(boolean airplanMode,boolean wifiMode,boolean dataMode,TestCase testCase){
		try{
			NetworkConnectionSetting networkSetting = null;
			boolean systemAirplanMode = false;
			networkSetting = ((AndroidDriver<WebElement>) testCase.getDriverFactory().getAppiumDriver()).getNetworkConnection();
			systemAirplanMode = networkSetting.airplaneModeEnabled();
			if(systemAirplanMode){
				networkSetting = new NetworkConnectionSetting(false, wifiMode, dataMode);
				((AndroidDriver<WebElement>)testCase.getDriverFactory().getDriver()).setNetworkConnection(networkSetting);
			}
			
			networkSetting = new NetworkConnectionSetting(airplanMode, wifiMode, dataMode);
			((AndroidDriver<WebElement>)testCase.getDriverFactory().getDriver()).setNetworkConnection(networkSetting);
			snapdealMobileStepReportingStatus = true;
			testCase.report.teststepreporting("User able to set network setting", "PASS", "Should be able set wifi network");
		}catch(Exception ex){
			snapdealMobileStepReportingStatus = false;
			testCase.report.teststepreporting("User not able to set network setting", "FAIL", "Should be able set wifi network");
			ex.printStackTrace();	
		}
	}
	
	/**
	 * Method to perform swipe horizontally
	 * @param swipeValue {@link Integer} value for swipe
	 * @param testCase {@link TestCase} Object
	 */
	public static void swipeByWidth(int swipeValue,TestCase testCase) {
		try {
			
			Dimension screenSize = testCase.getDriverFactory().getDriver().manage().window().getSize();
			Double screenWidth = Double.valueOf(String.valueOf(screenSize
					.getWidth())) / 2;
			Double screenHight = Double.valueOf(String.valueOf(screenSize
					.getHeight())) / 2;
			
			if(screenWidth + swipeValue > screenWidth*2 || swipeValue == 0){
				swipeValue = 0;
				screenWidth = screenWidth*2;
			}
			JavascriptExecutor js = (JavascriptExecutor) testCase.getDriverFactory().getDriver();

			HashMap<String, Object> swipeObject = new HashMap<String, Object>();
			swipeObject.put("startX", screenWidth + swipeValue);
			swipeObject.put("startY", screenHight);
			swipeObject.put("endX", screenWidth);
			swipeObject.put("endY", screenHight);
			swipeObject.put("duration", 1.8);
			js.executeScript("mobile: swipe", swipeObject);
			snapdealMobileStepReportingStatus = true;
		} catch (Exception ex) {
			snapdealMobileStepReportingStatus = false;
			ex.printStackTrace();
			
		}

	}

	/**
	 * Method to calculate percentage of a number
	 * @param perc percentage value to calculate
	 * @param total Number for which to calculate percentage
	 * @return
	 */
	public static double getPercentofTotal(int perc,double total){
		double getPervalue = 0.0;
		try{
			getPervalue = (total*perc)/100;
		}catch(Exception ex){
			ex.printStackTrace();
		}
		return getPervalue;
	}
	
	/**
	 * Method to get List of Object text as that of {@link TestObj} given
	 * @param testObj {@link TestObj} Object
	 * @return List:String
	 */
	public static List<String> getElemntsText(TestObj testObj) {
		List<WebElement> elems=null;
		List<String>text=new ArrayList<String>();
		try {
			testObj.setMaxSequence();
			int i = 1;
			Iterator<ObjectEntry> entriesIterator = testObj.getEntriesSet().iterator();
				Clicker:
				while (entriesIterator.hasNext()) {
					ObjectEntry objectEntry = entriesIterator.next();
					elems = objectEntry.getValue();
					for(WebElement el:elems) {
						text.add(el.getText());
						System.out.println(el.getText());
					}
					if(elems.size()>0) {
						snapdealMobileStepReportingStatus = true;
						break Clicker;
					}
					if (i == testObj.getMaxSeq()) {
						snapdealMobileStepReportingStatus = false;
						throw new Exception("No Locator found for the element: "+ testObj.ObjName);
					}
					i++;
				}
		}
		catch (Exception e) {
			snapdealMobileStepReportingStatus = false;
			e.printStackTrace();
		}
		return text;
	}
	
	/**
	 * Method to get {@link WebElement} list from {@link TestObj}
	 * @param testObj {@link TestObj} Object
	 * @return List:WebElements
	 */
	public static List<WebElement> getListOfElement(TestObj testObj){
		List<WebElement> elems =null;
		try {
			testObj.setMaxSequence();
			
			Iterator<ObjectEntry> entriesIterator = testObj.getEntriesSet().iterator();
		
				while (entriesIterator.hasNext()) {
					ObjectEntry objectEntry = entriesIterator.next();
					if(objectEntry.getSeqNum()==0)
						break;
					 elems = objectEntry.getValue();   
				}
				
				
				
		} catch (Exception e) {
			snapdealMobileStepReportingStatus = false;
			e.printStackTrace();
		}
		return elems;
	}
	
	/**
	 * Method to convert String value to {@link Double}
	 * @param value {@link String} Object
	 * @return {@link Double}
	 */
	public static double covertValueToDouble(String value){
		double doubleValue = 0.0;
		try{
		doubleValue = Double.parseDouble(value);
		}
		catch(Exception ex){
			ex.printStackTrace();
		}
		return doubleValue;
	}
	
	/**
	 * Method to get current timestamp
	 * @return
	 */
	public static String getCurrentTimestemp(){
		String dateTimeStemp = "";
		try{
		Date date = new Date();
		dateTimeStemp=  date.getTime()+"".replace(".", ""); 
		}
		catch(Exception ex)
		{
			
		}
		return dateTimeStemp;
	}
	
	/**
	 * toString Method for this {@link TestCase} Object
	 */
	@Override
	public String toString() {
		return testCaseCache.toString();
	}
	
	/**
	 * Default Constructor
	 */
	public TestCase() {	
	}
	
	/**
	 * Constructor
	 * @param executionSession Currently used {@link ExecutionSession} Object
	 * @param flavour platform
	 * @param vars extra info for this {@link TestCase} instance
	 * @throws PlatformNotSupportedException
	 */
	public TestCase(ExecutionSession executionSession,String flavour,String...vars) throws PlatformNotSupportedException {
		setExecutionSession(executionSession);
		environment = executionSession.getServiceApiSession().getEnvironment();
		flavorDictionary = new FlavourDictionary(flavour);
		apiDictionary = executionSession.getServiceApiSession().getApiDictionary();
        consoleOutput = flavorDictionary.consoleOutput;
		setLLDCoverageInfo(this);
		setTestPagesInfo(this);
		driverFactory = new DriverFactory(executionSession,flavour);
		resetStopWatch();
	    startStopWatch();
		String currentLld = executionSession.getTestRunParameterSet().get(executionSession.getCurrentExecutingSeq()).getLld();
		String snapshotStrategy = executionSession.getTestRunParameterSet().get(executionSession.getCurrentExecutingSeq()).getSnapshotStrategy();
		executionSession.getReportingSession().setSnapshotStrategy(snapshotStrategy);
		boolean looginEnabled = executionSession.getTestRunParameterSet().get(executionSession.getCurrentExecutingSeq()).getLoggingEnabled();
		//String lldFlavour = executionSession.getTestRunParameterSet().get(executionSession.getCurrentExecutingSeq()).getFlavour();
		if(vars.length>0)
			setTestDataFilePath(this.getClass().getProtectionDomain().getCodeSource().getLocation().getPath()+vars[0]);
		try {
			setTestDataInfo(this,executionSession);
			String udid = (String) this.getExecutionSession().getAppiumSession().getAppiumFlags().get("udid");
			if(currentLld.equals(""))
				report = new TestCaseReporting(this.getClass().getName(),executionSession.getReportingSession().getSuiteReporting(),snapshotStrategy,
						looginEnabled,executionSession.getCurrentExecutingSeq()==(executionSession.getTestRunParameterSet().size()-1), 
						executionSession.getTestRunParameterSet().get(executionSession.getCurrentExecutingSeq()).getTestDataId(),flavour,udid);
			else
				report = new TestCaseReporting(currentLld,executionSession.getReportingSession().getSuiteReporting(),snapshotStrategy,
						looginEnabled, executionSession.getCurrentExecutingSeq()==(executionSession.getTestRunParameterSet().size()-1),
						executionSession.getTestRunParameterSet().get(executionSession.getCurrentExecutingSeq()).getTestDataId(),flavour,udid);
			apiDictionary.setFlavourProperty("setTestCaseReporting", report);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}

	private void setLLDCoverageInfo(TestCase testCase) {
		for(Method meth : testCase.getClass().getDeclaredMethods()) {
			if(meth.isAnnotationPresent(LLDCoverage.class)) {
				LLD[] lldArr = meth.getAnnotation(LLDCoverage.class).value();
				for(LLD lld : lldArr) {
					llds.add(lld.value());
				}
			}
		}
	}
	
	/**
	 * Setter method for {@link #testDataFilePath}
	 * @param testDataFilePath
	 */
	private void setTestDataFilePath(String testDataFilePath) {
		this.testDataFilePath = testDataFilePath;
	}
	
	/**
	 * Method to process TestDataId annotation in any implementation of {@link TestCase}
	 * @param testCase Currently used instance of {@link TestCase}
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	private void setTestDataInfo(TestCase testCase, ExecutionSession executionSession) throws FileNotFoundException, IOException {
		Properties testData = new Properties();
		testData.load(new FileInputStream(testDataFilePath));
		Enumeration<?> idParamNames = testData.propertyNames();
		String suiteDataId = executionSession.getTestRunParameterSet().get(executionSession.getCurrentExecutingSeq()).getTestDataId();
		if(suiteDataId.equals("")) {
			for(Method meth : testCase.getClass().getDeclaredMethods()) {
				if(meth.isAnnotationPresent(TestDataId.class)) {
					TestDataId tdid = meth.getAnnotation(TestDataId.class);
					suiteDataId = tdid.value();
					break;
				}
			}
		}
		executionSession.getTestRunParameterSet().get(executionSession.getCurrentExecutingSeq()).setTestDataId(suiteDataId);
		while(idParamNames.hasMoreElements()) {
			String idParam = (String) idParamNames.nextElement();
			if(idParam.startsWith(suiteDataId)) {
				this.testData.put(idParam.split("[.]")[1],(String) testData.get(idParam));
			}
		}
	}
	
	/**
	 * Method to process Platform and SupportedPlatforms annotations in any implementation of {@link TestCase}
	 * @param testCase
	 * @throws PlatformNotSupportedException
	 */
	private void setTestPagesInfo(TestCase testCase) throws PlatformNotSupportedException {
		boolean platformSupported = false;
		outer:
		for(Method meth : testCase.getClass().getDeclaredMethods()) {
			if(meth.isAnnotationPresent(SupportedPlatforms.class)) {
				Platform[] plf = meth.getAnnotation(SupportedPlatforms.class).value();
				for(Platform pfm : plf) {
					if(pfm.name().equals(flavorDictionary.getKey())) {
						String[] pages = pfm.pages().names();
						for(String page : pages) {
							Page pg = (Page) flavorDictionary.getValue().get(page);
							String path = getClass().getProtectionDomain().getCodeSource().getLocation().getPath();
							testCase.testCaseCache.loadPropertyMap(new File(path+pg.getPropertiesPath(testCase)));
							testCase.testCaseCache.loadPropertyMap(new File(path+pg.getParentPropertiesPath(testCase)));
						}
						platformSupported = true;
						break outer;
					}					
				}
			}
		}
		if(!platformSupported) {
			throw new PlatformNotSupportedException(testCase.getClass().getName(),flavorDictionary.getKey());
		}
	}
	
	/**
	 * Method to get {@link Method} Object for the given {@link ExpectedConditions} Method
	 * @param meth Name of the {@link ExpectedConditions} Method
	 * @return
	 */
	private Method getExpConditionMethod(String meth) {
		Method retMethod = null;
		try {
			for (Method expMeth : expConditionMeths) {
				String methName = expMeth.toString();
				if(!methName.split("[(]")[1].startsWith("org.openqa.selenium.By"))
					continue;
				methName = methName.split("[(]")[0];
				int len = methName.split("[.]").length;
				methName = methName.split("[.]")[len - 1];
				if (methName.equalsIgnoreCase(meth)) {
					retMethod = expMeth;
					break;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return retMethod;
	}

	/**
	 * This Class represents a test case cache  for the currently executing test case
	 * @author Khagesh Kapil
	 * @see TestCase
	 */
	public class TestCaseCache {
		
		/**
		 * Field specifying the default cache size
		 * default value is 25
		 */
		private static final int DEFAULT_CACHE_SIZE = 25;
		
		/**
		 * Field specifying test case cache size for this instance
		 */
		private int initSize = DEFAULT_CACHE_SIZE;
		
		/**
		 * Field specifying the pool of {@link TestObj} for {@link TestCaseCache}
		 */
		private SortedSet<TestObj> testObjPool;
		
		/**
		 * Fiels specifying the properties map for this test case cache
		 */
		private HashMap<String, Properties> propertyMap;

		/**
		 * Method to update {@link #testObjPool} after waiting on an element
		 * @param to {@link TestObj} Object
		 * @return
		 */
		public TestObj updateElementAfterWait(TestObj to,String...params) {
			String objName = to.ObjName;
			try {
				Iterator<TestObj> itr = testObjPool.iterator();
				while (itr.hasNext()) {
					TestObj t = itr.next();
					if (t.ObjName.equals(to.ObjName)) {
						testObjPool.remove(t);
						break;
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return lookUp(objName,params);
		}
		
		public TestObj updateElementAfterWait(TestObj to,int waitTime,String...params) {
			String objName = to.ObjName;
			try {
				Iterator<TestObj> itr = testObjPool.iterator();
				while (itr.hasNext()) {
					TestObj t = itr.next();
					if (t.ObjName.equals(to.ObjName)) {
						testObjPool.remove(t);
						break;
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return lookUp(objName,waitTime,params);
		}

		/**
		 * Method to specifying string description of this {@link TestCaseCache} instance
		 */
		@Override
		public String toString() {
			String desc = "";
			Iterator<TestObj> itr = testObjPool.iterator();
			while (itr.hasNext()) {
				TestObj t = itr.next();
				desc += t.toString().toString() + "\n";
			}
			return desc;
		}

//		TestCaseCache(int initSizeVal) {
//			initSize = initSizeVal;
//			testObjPool = new TreeSet<TestObj>(new CacheMaster());
//			propertyMap = new HashMap<String, Properties>();
//		}

		/**
		 * Default Constructor
		 */
		TestCaseCache() {
			testObjPool = new TreeSet<TestObj>(new CacheMaster());
			propertyMap = new HashMap<String, Properties>();
		}

		/**
		 * Setter Method for {@link #initSize}
		 * @param size
		 */
		public void setCacheSize(int size) {
			initSize = size;
		}
		
		/**
		 * Getter method for {@link #initSize}
		 * @return
		 */
		public int getCurrentCacheSize() {
			return initSize;
		}
		
		/**
		 * load {@link #propertyMap}
		 * @param f properties file object to load properties
		 */
		public void loadPropertyMap(File f) {
			try {
				FileInputStream fis = new FileInputStream(f);
				String name = f.getName().split("_")[0];
				Properties p = new Properties();
				p.load(fis);
				propertyMap.put(name, p);
			} catch (Exception e) {
				e.printStackTrace();
				String err = "Error adding properties for " + f.getName()
						+ e.getMessage();
				System.out.println(err);
			}
		}

		/**
		 * Method to search for a {@link TestObj} in {@link #testObjPool}
		 * @param prop Nof the Object as specified in properties file
		 * @return
		 */
		public TestObj lookUp(String prop,String...params) {
			TestObj to = null;
			try {
				for (TestObj teo : testObjPool) {
					String key = teo.ObjName;
					if (key.equalsIgnoreCase(prop)) {
						to = teo;
						break;
					}
				}
				if (to == null) {
					findingObj: for (Map.Entry<String, Properties> me : propertyMap
							.entrySet()) {
						Enumeration<?> e = me.getValue().propertyNames();
						while (e.hasMoreElements()) {
							String prp = (String) e.nextElement();
							if (prp.equalsIgnoreCase(prop)) {
								String prpVal = me.getValue().getProperty(prp);
								to = makeNewEntry(prp, prpVal,(int) driverFactory.getUserWaitTime(), params);
								break findingObj;
							}
						}
					}
					if (to == null)
						throw new Exception("Couldn't find: " + prop	+ " :in any of the referenced property file(s)");
				}
			} catch (Exception e) {
				if(consoleOutput.equals("true"))
					e.printStackTrace();
			}
			return to;
		}
		
		public TestObj lookUp(String prop,int waitTime,String...params) {
			TestObj to = null;
			try {
				for (TestObj teo : testObjPool) {
					String key = teo.ObjName;
					if (key.equalsIgnoreCase(prop)) {
						to = teo;
						break;
					}
				}
				if (to == null) {
					findingObj: for (Map.Entry<String, Properties> me : propertyMap
							.entrySet()) {
						Enumeration<?> e = me.getValue().propertyNames();
						while (e.hasMoreElements()) {
							String prp = (String) e.nextElement();
							if (prp.equalsIgnoreCase(prop)) {
								String prpVal = me.getValue().getProperty(prp);
								to = makeNewEntry(prp, prpVal,waitTime, params);
								break findingObj;
							}
						}
					}
					if (to == null)
						throw new Exception("Couldn't find: " + prop	+ " :in any of the referenced property file(s)");
				}
			} catch (Exception e) {
				if(consoleOutput.equals("true"))
					e.printStackTrace();
			}
			return to;
		}
		
		public boolean waitTillDisplay(String path,int waitTime, String...parms) throws Exception{
			boolean find = false;
			for (Map.Entry<String, Properties> me : propertyMap.entrySet()) {
				Enumeration<?> e = me.getValue().propertyNames();
				while (e.hasMoreElements()) {
					String prp = (String) e.nextElement();
					if (prp.equalsIgnoreCase(path)) {
						find = true;
						String prpVal = me.getValue().getProperty(prp);
//						to = makeNewEntry(prp, prpVal,waitTime, params);
						break;
					}
				}
			}
			if (!find)
				throw new Exception("Couldn't find: " + path+ " :in any of the referenced property file(s)");
			
			
			return true;
			
		}
			
		

		/**
		 * Method to add a new entry in {@link #testObjPool}
		 * @param entryName Name of the entry
		 * @param entryVal Value of the entry
		 * @return
		 */
		public TestObj makeNewEntry(String entryName, String entryVal,int waitTime, String...params) {
			TestObj t = new TestObj();
			try {
				int currPoolSize = testObjPool.size();
				t = parseObj(entryVal, entryName,waitTime,params);
				if (currPoolSize >= initSize) {
					arrangePoolSpace();
				}
				testObjPool.add(t);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return t;
		}

		/**
		 * Method to arrange for space in {@link #testObjPool} in case it is full
		 */
		public void arrangePoolSpace() {
//			TestObj oldestTestObject = new TestObj();
//			Iterator<TestObj> poolItr = pool.iterator();
//			Long currTime = System.currentTimeMillis();
//			while (poolItr.hasNext()) {
//				TestObj poolTestObj = poolItr.next();
//				long poolTestObjAge = currTime - poolTestObj.objAge;
//				long oldestObjAge = currTime - oldestTestObject.objAge;
//				if (oldestObjAge == currTime)
//					oldestObjAge = 0;
//				if (oldestObjAge < poolTestObjAge) {
//					oldestTestObject = poolTestObj;
//				}
//			}
			testObjPool.remove(testObjPool.first());
		}
		
		/**
		 * This method will return the WebElemnt of the given element path
		 * @author ankit.gupta04
		 * @param path
		 * @param waitTime
		 * @param params
		 * @return
		 * @throws Exception
		 */
		public WebElement getWebElement(String path, int waitTime, String...params ) throws Exception{
//			TestObj testObj = new TestObj();
			// Finding out path from the properties file
			String elementPath ="";
			 for (Map.Entry<String, Properties> me : propertyMap.entrySet()) {
					Enumeration<?> e = me.getValue().propertyNames();
					while (e.hasMoreElements()) {
						String prp = (String) e.nextElement();
						if (prp.equalsIgnoreCase(path)) {
							elementPath = me.getValue().getProperty(prp);
							break;
						}
					}
				}
			 if (elementPath.isEmpty()){
				 report.teststepreporting(elementPath+" :is not present in properties file", "FAIL", "INFO");
					throw new Exception(elementPath+" :is not present in properties file");
			 }
			WebElement we = null;
			String locatorString = extractString("<START>", "<END>", elementPath);
			locatorString = MessageFormat.format(locatorString, (Object[]) params);
			String info = locatorString.split("-->")[0];
			String loc = locatorString.split("-->")[1];
			String infoStr = info.substring(1);
			try {
				By by = (By) getByMethod(infoStr.replaceAll("_", "")).invoke(null, loc);
				WebDriverWait wait = new WebDriverWait(driverFactory.getDriver(), waitTime);
				we = wait.until(ExpectedConditions.presenceOfElementLocated(by));
			} catch (Exception e) {
				e.printStackTrace();
			}
			return we;
		}
		
		
		/**
		 * This method will return the list of WebElement of the given element path
		 * @author ankit.gupta04
		 * @param path
		 * @param waitTime
		 * @param params
		 * @return
		 * @throws Exception
		 */
		public List<WebElement> getWebElements(String path, int waitTime, String...params ) throws Exception{
//			TestObj testObj = new TestObj();
			// Finding out path from the properties file
			String elementPath ="";
			 for (Map.Entry<String, Properties> me : propertyMap.entrySet()) {
					Enumeration<?> e = me.getValue().propertyNames();
					while (e.hasMoreElements()) {
						String prp = (String) e.nextElement();
						if (prp.equalsIgnoreCase(path)) {
							elementPath = me.getValue().getProperty(prp);
							break;
						}
					}
				}
			 if (elementPath.isEmpty()){
				 report.teststepreporting(elementPath+" :is not present in properties file", "FAIL", "INFO");
					throw new Exception(elementPath+" :is not present in properties file");
			 }
			List<WebElement> we = null;
			String locatorString = extractString("<START>", "<END>", elementPath);
			locatorString = MessageFormat.format(locatorString, (Object[]) params);
			String info = locatorString.split("-->")[0];
			String loc = locatorString.split("-->")[1];
			String infoStr = info.substring(1);
			try {
				By by = (By) getByMethod(infoStr.replaceAll("_", "")).invoke(null, loc);
				WebDriverWait wait = new WebDriverWait(driverFactory.getDriver(), waitTime);
				we = (List<WebElement>) wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(by));
			} catch (Exception e) {
				e.printStackTrace();
			}
			return we;
		}
		

		/**
		 * Method to interpret an element from its locator in properties file
		 * @param obj Name of Object in properties file
		 * @param objName Name of object to be set in {@link TestObj} Object
		 * @return
		 */
		public TestObj parseObj(String obj, String objName,int waitTime, String...params) {
			TestObj testObj = new TestObj();
			String locatorString = extractString("<START>", "<END>", obj);
			locatorString = MessageFormat.format(locatorString, (Object[]) params);
			Pattern locator = Pattern.compile("[0-9]_[class|css|id|linkText|xpath|name|tag|partialTag|IosUIAutomation|uiAuto]");
			Matcher matcher = locator.matcher(locatorString);
			List<String> locatorsList = new ArrayList<String>();
			int start = 0;
			int limit = 0;
			while (true) {
				if(start==0) {
					matcher.find();
					start = matcher.start();
				}
				if(matcher.find())
					limit = matcher.start();
				else
					limit = locatorString.length();
				String locatorTxt = locatorString.substring(start, limit);
				if(locatorTxt.startsWith("0_uiAuto")) {
					testObj.setUiAutoObj(locatorTxt.split("-->")[1]);
					testObj.ObjName = objName;
					return testObj;
				}
				else
					locatorsList.add(locatorTxt);
				
				start = limit;
				if(limit==locatorString.length())
					break;
			 }
			String[] locators = (String[])locatorsList.toArray(new String[locatorsList.size()]);
			try {
//				Field[] entryTypes = removeThis(testObjFields);
				for (int i = 0; i < locators.length; i++) {
					String info = locators[i].split("-->")[0];
					String loc = locators[i].split("-->")[1];
					// System.out.println(Integer.parseInt(info.charAt(0)+""));
					String infoStr = info.substring(1);
					ObjectEntry objectEntry = testObj.getObjEntryByKey(infoStr);
					testObj.getEntries().remove(objectEntry);
					By by = (By) getByMethod(objectEntry.getByKey().replaceAll("_", "")).invoke(
							null, loc);
					//System.out.println(testObj);
//					Object o = entryFld.get(to);
//					ObjectEntry oe = (ObjectEntry) o;
					objectEntry = objectEntry.setSeqNum(Integer.parseInt(info.charAt(0) + ""));
					objectEntry = objectEntry.setByVal(by);
					int	counter = 0;
					do{
						try {
							objectEntry = objectEntry.getObjEntryWithVal(driverFactory.getDriver().findElements(
										by));
							if(objectEntry.getValue().size()==0)
								throw new Exception("Waiting for: "+objName+" :to load");
							break;
							} catch (Exception e) {
								if(consoleOutput.equals("true"))
									e.printStackTrace();
								Thread.sleep(200);
								counter+=200;
							}
						} while(counter<(waitTime*.85));
						
						testObj.getEntries().add(objectEntry);
//						entryFld.set(to, oe);
					}
					testObj.ObjName = objName;
					testObj.setAge(System.currentTimeMillis());
				} catch (Exception e) {
					e.printStackTrace();
				}
				return testObj;
		}
		

		/**
		 * Method to update {@link #testObjPool} to contain the given {@link TestObj}
		 * @param to {@link TestObj} Object
		 * @return
		 */
		public boolean updatePool(TestObj to) {
			boolean attempt = false;
			try {
				Iterator<TestObj> poolItr = testObjPool.iterator();
				while (poolItr.hasNext()) {
					TestObj t = poolItr.next();
					if (t.ObjName.equals(to.ObjName)) {
						t = to;
						attempt = true;
						break;
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return attempt;
		}

		/**
		 * Method to get {@link Method} Object for the given By Method
		 * @param method Name of the {@link By} Method
		 * @return
		 */
		private Method getByMethod(String method) {
			Method retMethod = null;
			try {
				List<Method> methList = new ArrayList<Method>();
				for(Method meth : byMeths)
					methList.add(meth);
				for(Method meth : mobByMeths)
					methList.add(meth);
				for (Method byMeth : methList) {
					String methName = byMeth.toString();
					methName = methName.split("[(]")[0];
					int len = methName.split("[.]").length;
					methName = methName.split("[.]")[len - 1];
					if (methName.equalsIgnoreCase(method)) {
						retMethod = byMeth;
						break;
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return retMethod;
		}

//		private String getFieldName(Field f) {
//			String fieldName = "";
//			try {
//				fieldName = f.toString();
//				int len = fieldName.split("[.]").length;
//				fieldName = fieldName.split("[.]")[len - 1].replaceAll("_", "");
//			} catch (Exception e) {
//
//			}
//			return fieldName;
//		}
	}

	/**
	 * Method to pause execution on the basis of the given condition and on given {@link TestObj}
	 * @param conditionStr String to specify condition for waiting
	 * @param testObject {@link TestObj} Object
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public TestObj pauseExecutionUntil(String conditionStr, TestObj testObject) {
		try {
			testObject.setMaxSequence();
			int failedAttempSeq = -11;
			Method expConditionMeth = getExpConditionMethod(conditionStr);
			ExpectedCondition<Object> exp;
			Iterator<ObjectEntry> entriesIterator = testObject.getEntriesSet().iterator();
			while (entriesIterator.hasNext()) {	
				ObjectEntry objectEntry = entriesIterator.next();
				try {
					exp = (ExpectedCondition<Object>) expConditionMeth
							.invoke(null, objectEntry.getByVal());
				} catch (Exception e) {
					e.printStackTrace();
					throw new Exception("Unsupported wait type");
				}
				try {
					driverFactory.getWait().until(exp);
					
					objectEntry.setWaitInfo(-1);
					testCaseCache.updatePool(testObject);
				} catch (Exception e) {
					objectEntry.setWaitInfo(failedAttempSeq--);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return testObject;
	}

	public static void main(String[] args) {
		// TestCase tc = new TestCase("iosApp");
	}

	/**
	 * Method to extract String between str1 and str2 from hostStr
	 * @param str1 Input String 1
	 * @param str2 Input String 2
	 * @param hostStr Parent String
	 * @return
	 */
	public String extractString(String str1, String str2, String hostStr) {
		String retStr = (String) hostStr.subSequence(hostStr.indexOf(str1)
				+ str1.length(), hostStr.indexOf(str2));
		return retStr;
	}

	/**
	 * Method to execute the main method of an object extending {@link TestCase} by giving its name
	 * @param params List of parameters to be passed to the method
	 * @return
	 * @throws Exception
	 */
	public Object testCaseExecutor(Object...params) throws Exception {
		Object o = null;
		try {
			Class<?> testCaseClass = this.getClass();
			Method[] testCaseMethod = testCaseClass.getDeclaredMethods();
			for(Method m : testCaseMethod) {
				//System.out.println(m.getName());
				if(m.getName().equalsIgnoreCase(TEST_CASE_SCRIPT_METHOD_NAME)) {
					o = m.invoke(this, params);
				}
			}
			//System.out.println(this.getClass().getName());
		}
		catch(Exception e) {
			e.printStackTrace();
			throw new Exception("illegal functionality called for :"+TEST_CASE_SCRIPT_METHOD_NAME);
		}
		finally {
			report.footer();
		}
		return o;
	}

	/**
	 * Method to produce clone for the current {@link TestCase} Object
	 */
	protected Object clone() throws CloneNotSupportedException {
	    TestCase testCaseClone=(TestCase)super.clone();
	    return testCaseClone;
	  }

	public static int getElementCountForLocator(TestObj testObj, String locator) {
		int elementCount = 0;
		try {
			Iterator<ObjectEntry> entriesIterator = testObj.getEntriesSet().iterator();
			///System.out.println("we gave : "+locator);
			while(entriesIterator.hasNext()) {
				ObjectEntry objectEntry = entriesIterator.next();
				//System.out.println("iteration : "+objectEntry.getKey());
				if(objectEntry.getKey().equals("_"+locator)) {
					//System.out.println("------------"+objectEntry);
					for(int i=0;i<objectEntry.getValue().size();i++) {
						System.out.println("-----------"+objectEntry.getValue().get(i).getText());
					}
					return objectEntry.getValue().size();
				}
			}
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		return elementCount;
	}
	
	public static void keyBoardEvent(int eventNumber) {
		  try {
		   Thread.sleep(1000);
		   Runtime.getRuntime().exec(
		     "cmd /C adb shell input keyevent " + eventNumber);
		   Thread.sleep(1000);
		  } catch (Throwable t) {
		   t.printStackTrace();
		  }
		 }
	
	public boolean flipElement(TestObj testObj, int div) {
		String script = testObj.getUiAutoObj();
		try{
			((JavascriptExecutor) this.getDriverFactory().getDriver()).executeScript("var r = " + script +".rect(); var w = r.size.width; var h = r.size.height; var ox = r.origin.x; var oy = r.origin.y;  var dx = ox; var dy = oy + h/2; var sx = ox + w/"+ div + "; var sy = dy; target.flickFromTo({x:sx, y:sy},{x:dx,y:dy});");
			return true;
		}
		catch(Exception e){
			e.printStackTrace();
			return false;
		}
	}
	
	public boolean dragElement(TestObj testObj, int div, int duration) {
		String script = testObj.getUiAutoObj();
		try{
			((JavascriptExecutor) this.getDriverFactory().getDriver()).executeScript("var r = " + script +".rect(); var w = r.size.width; var h = r.size.height; var ox = r.origin.x; var oy = r.origin.y;  var dx = ox; var dy = oy + h/2; var sx = ox + w/"+ div + "; var sy = dy; target.dragFromToForDuration({x:sx, y:sy}, {x:dx,y:dy}, " + duration +");");
			return true;
		}
		catch(Exception e){
			e.printStackTrace();
			return false;
		}
	}
	
	public void info(TestObj testObj){
		String script = testObj.getUiAutoObj();	
		((JavascriptExecutor) this.getDriverFactory().getDriver()).executeScript("var e = " + script + " ;var r = e.rect(); var s = r.size; var w = s.width; var h = s.height; var o = r.origin; var ox = o.x; var oy = o.y; var c = {x:(w/2)+ox, y:(h/2)+oy}; var cx = c.x; var cy = c.y; UIALogger.logMessage(\"w:\" + w); UIALogger.logMessage(\"h:\" + h); UIALogger.logMessage(\"ox:\" + ox); UIALogger.logMessage(\"oy:\" + oy); UIALogger.logMessage(\"cx:\" + cx); UIALogger.logMessage(\"cy:\" + cy);");
	}
	
	public long size(TestObj testObj) {
		String script = testObj.getUiAutoObj();
		long l = (Long) ((JavascriptExecutor) this.getDriverFactory().getDriver()).executeScript(script+".length;");
		return l;
	}
	
	public String name(TestObj testObj) {
		String script = testObj.getUiAutoObj();
		String n = (String) ((JavascriptExecutor) this.getDriverFactory().getDriver()).executeScript(script+".name();");
		return n;
	}
	
	public String value(TestObj testObj) {
		String n = "";
		String script = testObj.getUiAutoObj();
		n = (String) ((JavascriptExecutor) this.getDriverFactory().getDriver()).executeScript(script+".value();");
		return n;
	}
	
	public boolean clickKeyboardButton(String buttonName) {
		try{
			((JavascriptExecutor) this.getDriverFactory().getDriver()).executeScript("target.frontMostApp().keyboard().buttons()[\""+ buttonName +"\"].tap();");
			return true;
		}catch(Exception e){
			e.printStackTrace();
			return false;
		}
	}
	
	public boolean setText(TestObj testObj, String text) {
		String script = testObj.getUiAutoObj();
		try{
			((JavascriptExecutor) this.getDriverFactory().getDriver()).executeScript(script+".setValue(\""+ text +"\");");
			return true;
		}catch(Exception e){
			e.printStackTrace();
			return false;
		}
	}
	
	public boolean typeText(TestObj testObj, String text) {
		try{
			((JavascriptExecutor) this.getDriverFactory().getDriver()).executeScript("target.frontMostApp().keyboard().typeString(\""+ text +"\");");
			return true;
		}catch(Exception e){
			e.printStackTrace();
			return false;
		}
	}
		
	public boolean tap(TestObj testObj) {
		String script = testObj.getUiAutoObj();
		try{
			((JavascriptExecutor) this.getDriverFactory().getDriver()).executeScript(script+".tap();");
			return true;
		}catch(Exception e){
			e.printStackTrace();
			return false;
		}
	}
	
	public boolean hit(TestObj testObj) {
		String script = testObj.getUiAutoObj();
		try{
			((JavascriptExecutor) this.getDriverFactory().getDriver()).executeScript("var r = " + script +".rect(); var c = {x:r.size.width/2 + r.origin.x, y:r.size.height/2+r.origin.y}; target.tap({x:c.x, y:c.y});");
			return true;
		}catch(Exception e){
			e.printStackTrace();
			return false;
		}
	}
	
	public void tapTabBarButton(String name) {
		((JavascriptExecutor) this.getDriverFactory().getDriver()).executeScript("function tapTabBarButton(tabBarButtonName) {var tabBar = UIATarget.localTarget().frontMostApp().tabBar(); tabBar.buttons()[tabBarButtonName].tap();} tapTabBarButton(\"" + name + "\");");
	}
	
	public String getSelectedTabbarButtonName(){
		return (String) ((JavascriptExecutor) this.getDriverFactory().getDriver()).executeScript("UIATarget.localTarget().frontMostApp().tabBar().selectedButton().name();");
	}
	
	public String getLocatingString(TestObj testObj, int delay) {
		String script = testObj.getUiAutoObj();
		return script;
	}

		
	public boolean isElementPresent(TestObj testObj, int delay) {
		Long flag = (long)0;
		String script = testObj.getUiAutoObj();
		try{
			flag = (Long) ((JavascriptExecutor) this.getDriverFactory().getDriver()).executeScript("var e=" + script + ";var d="+ delay +";function check() {if(e.isValid()) {return 1; }else {for(i = 0; i < d; i++){target.delay(0.5);if(e.isValid()) {return 1; }}return 0;}} check();");
			if(flag == 1) return true;
			else return false;
		}catch (Exception e){
			e.printStackTrace();
			return false;
		}
	}
	
	
	
	public boolean isElementPresent(TestObj testObj) {
		Long flag = (long)3;
		String script = testObj.getUiAutoObj();
		try{
			System.out.println("ELEMENT ======>> " + script + ".isValid();");
			flag = (Long) ((JavascriptExecutor) this.getDriverFactory().getDriver()).executeScript("function check() {if("+script+".isValid()) return 1; else return 0;} check();");
			System.out.println("ELEMENT ======>> " + script + ".isValid();" + "======>" + flag);
			if(flag.intValue() == 1) return true;
			else return false;
		}catch(Exception e){
			e.printStackTrace();
			return false;
		}

	}
	
	public boolean isElementEnabled(TestObj testObj) {
		Long flag = (long)3;
		String script = testObj.getUiAutoObj();
		try{
			System.out.println("ELEMENT ======>> " + script + ".isEnabled();");
			flag = (Long) ((JavascriptExecutor) this.getDriverFactory().getDriver()).executeScript("function check() {if("+script+".isEnabled()) return 1; else return 0;} check();");
			System.out.println("ELEMENT ======>> " + script + ".isEnabled();" + "======>" + flag);
			if(flag.intValue() == 1) return true;
			else return false;
		}catch(Exception e){
			e.printStackTrace();
			return false;
		}
	}
	
	public boolean isElementDisplayed(TestObj testObj) {
		boolean presentFlag = isElementPresent(testObj);
		if(!presentFlag) {
			return false;
		}
		long flag = (long)0;
		String script = testObj.getUiAutoObj();
		flag = (Long) ((JavascriptExecutor) this.getDriverFactory().getDriver()).executeScript("function check() {if("+script+".isVisible()) return 1; else return 0;} check();");
		if(flag == 1) return true;
		else return false;
	}
	
	public boolean isElementDisplayed(TestObj testObj, int delay) {
		long flag = (long)0;
		String script = testObj.getUiAutoObj();
		flag = (Long) ((JavascriptExecutor) this.getDriverFactory().getDriver()).executeScript("function check() {target.pushTimeout(" + delay + "); " + script + ".withValueForKey(1, \"isVisible\"); target.popTimeout(); if("+script+".isVisible()) return 1; else return 0;} check();");
		if(flag == 1) return true;
		else return false;
	}
	
	public boolean isElementDisplayed(TestObj testObj, int delay, double step) {
		long flag = (long)0;
		String script = testObj.getUiAutoObj();
		flag = (Long) ((JavascriptExecutor) this.getDriverFactory().getDriver()).executeScript("function waitForVisible(element, timeout, step){ var stop = timeout/step; for (var i = 0; i < stop; i++){target.delay(step);if (element.isVisible()){return;}}element.logElement();throw(\"Not visible\");} waitForVisible("+ script + ", " + delay + ", " + step +");");
		if(flag == 1) return true;
		else return false;
	}
		
	public long size(String script) {
		//String script = testObj.getUiAutoObj();
		long l = (Long) ((JavascriptExecutor) this.getDriverFactory().getDriver()).executeScript(script+".length;");
		return l;
	}
	
	public String name(String script) {
		//String script = testObj.getUiAutoObj();
		String n = (String) ((JavascriptExecutor) this.getDriverFactory().getDriver()).executeScript(script+".name();");
		return n;
	}
	
	public boolean tap(String script) {
		//String script = testObj.getUiAutoObj();
		try{
			((JavascriptExecutor) this.getDriverFactory().getDriver()).executeScript(script+".tap();");
			return true;
		}catch(Exception e){
			e.printStackTrace();
			return false;
		}
	}
	
	
	/**
	 * This method will highlight the element 
	 * @param driver
	 * @param list
	 */
	public void highlighter(WebElement element) {
		try{
		JavascriptExecutor js = ((JavascriptExecutor) this.getDriverFactory().getDriver());
//		for(WebElement element : list){
		String var = (String) js.executeScript("return arguments[0].getAttribute('style', arguments[1]);", element); 
		js.executeScript("return arguments[0].setAttribute('style', arguments[1]);", element, "border: 4px solid red; ");
		Thread.sleep(200);
		js.executeScript("arguments[0].setAttribute('style', arguments[1]);", element, var);
//				js.executeScript("arguments[0].style.border = arguments[1];", element, "4px solid black");
//				Thread.sleep(1000);
//				js.executeScript("arguments[0].style.border = arguments[1];", element, "");
		
		}catch(Exception e) {
			e.printStackTrace();
		}
	}	

		
	
	public boolean scroll(TestObj testObj) {
		
		try {
			String script = testObj.getUiAutoObj();
			System.out.println("ELEMENT ======>> " + script + ".scrollToVisible();");
			((JavascriptExecutor) this.getDriverFactory().getDriver()).executeScript(script+".scrollToVisible();");
			return true;
		}catch(Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public boolean scrollTesting(TestObj testObj) {
		String script = testObj.getUiAutoObj();
		boolean flag = false;
		for(int i = 0; i < 10; i++) {
			try {
				System.out.println("ELEMENT ======>> " + script + "[" + i + "]" + ".scrollToVisible();");
				((JavascriptExecutor) this.getDriverFactory().getDriver()).executeScript(script + "[" + i + "]" +".isValid();");
				((JavascriptExecutor) this.getDriverFactory().getDriver()).executeScript(script + "[" + i + "]" +".scrollToVisible();");
				return true;
			}catch(Exception e) {
			}
		}
		return flag;
	}
	
	public void scrollZero(TestObj testObj) {
		
		String script = testObj.getUiAutoObj();
		for(int i = 0; i < 10; i++) {
			try {
				((JavascriptExecutor) this.getDriverFactory().getDriver()).executeScript(script + "[" + 0 + "]" +".scrollToVisible();");
			}catch(Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	public boolean scroll(TestObj testObj, String name) {
		try {
			String script = testObj.getUiAutoObj();
			((JavascriptExecutor) this.getDriverFactory().getDriver()).executeScript("var n = " + name + ";" +script+".scrollToElementWithName(n);");
			return true;
		}catch(Exception e) {
			e.printStackTrace();
			return false;
		}
	}
			
	public boolean scroll(String script) {
		//String script = testObj.getUiAutoObj();
		try{
			((JavascriptExecutor) this.getDriverFactory().getDriver()).executeScript(script+".scrollToVisible();");
			return true;
		}catch(Exception e){
			return false;
		}
	}
	
	public boolean isElementPresent(String script, int delay) {
		//String script = testObj.getUiAutoObj();
		//flag = (Long) ((JavascriptExecutor) this.getDriverFactory().getDriver()).executeScript("function check() {target.pushTimeout(" + delay + ");" + script + ".withValueForKey(1,isVisible);target.popTimeout();if("+script+".isValid()) return 1; else return 0;} check();");
		WebDriverWait w = new WebDriverWait(this.getDriverFactory().getDriver(), delay);
		try{
			w.until(ExpectedConditions.presenceOfElementLocated(MobileBy.IosUIAutomation(script)));
			return true;
		}catch(Exception e)  {
			return false;
		}
	}
	
	public boolean isElementPresent(String script) {
		Long flag = (long)0;
		//String script = testObj.getUiAutoObj();
		flag = (Long) ((JavascriptExecutor) this.getDriverFactory().getDriver()).executeScript("function check() {if("+script+".isValid()){ UIALogger.logMessage(\"ELEMENT IS VALID\");return 1;} else{UIALogger.logMessage(\"ELEMENT IS INVALID\"); return 0;}} check();");
		if(flag == 1) 
			return true;
		else 
			return false;
	}
	

	public boolean isElementDisplayed(String script) {
		boolean presentFlag = isElementPresent(script);
		if(!presentFlag) {
			return false;
		}
		long flag = (long)0;
		//String script = testObj.getUiAutoObj();
		flag = (Long) ((JavascriptExecutor) this.getDriverFactory().getDriver()).executeScript("function check() {if("+script+".isVisible()) return 1; else return 0;} check();");
		if(flag == 1) return true;
		else return false;
	}
	
	public boolean isElementDisplayed(String script, int delay) {
		boolean presentFlag = isElementPresent(script, delay);
		if(!presentFlag) {
			return false;
		}
		long flag = (long)0;
		//String script = testObj.getUiAutoObj();
		flag = (Long) ((JavascriptExecutor) this.getDriverFactory().getDriver()).executeScript("function check() {if("+script+".isVisible()) return 1; else return 0;} check();");
		if(flag == 1) return true;
		else return false;
	}
	
	public static void startStopWatch() {
	    System.out.println(">>>>>>>>>>>>>>>>>>>>>starting stopwatch");
	    stopWatch.start();
	  }

	  public static void stopStopWatch()
	  {
	    stopWatch.stop();
	    System.out.println(">>>>>>>>>>>>>>>>>>>>>stoping stopwatch");
	  }

	  public static void resetStopWatch() {
	    stopWatch.reset();
	    System.out.println(">>>>>>>>>>>>>>>>>>>>>reseting stopwatch");
	  }
	  
	  public static long getElapsedStopWatch(TimeUnit unit) {
	    long t = stopWatch.elapsed(unit);
	    System.out.println(new StringBuilder().append(">>>>>>>>>>>>>>>>>>>>>stopwatch : ").append(t).toString());
	    return t;
	  }
	
}

/**
 * This class is used to specify an algorithm for maintaining order of {@link TestObj} Objects in {@link TestCaseCache}
 * @author Khagesh Kapil
 * @see TestCaseCache
 */
class CacheMaster implements Comparator<TestObj>{

	/**
	 * Method to specifying comparing algorithm
	 */
	@Override
	public int compare(TestObj testObj1, TestObj testObj2) {
		if(testObj1.getAge()>testObj2.getAge())
			return 1;
		else if(testObj2.getAge()>testObj1.getAge())
			return -1;
		else
			return 0;
	}
	
}
