package snapdeal.mobileAutomation.master;

import java.lang.reflect.Method;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;

import snapdeal.mobileAutomation.interfaces.LoadTimeMesurable;
import snapdeal.mobileAutomation.interfaces.LoadTimeStartPoint;
import snapdeal.mobileAutomation.master.TestObj;

/**
 * This class provides an abstraction of a page for any platform/flavour
 * @author Khagesh Kapil
 */
abstract public class Page {
	
	/**
	 * flavour of the page
	 */
	public static String flavour;

	public String loadVariant = "";
	
	public String getLoadVariant() {
		return loadVariant;
	}

	public void setLoadVariant(String loadVariant) {
		this.loadVariant = loadVariant;
	}

	/**
	 * Abstract getter method for a mandatory attribute "propertiesPath" of every class extending {@link #Page()}
	 * @return propertiesPath for every child class of Page
	 */
	abstract public String getPropertiesPath(TestCase testCase);

	/**
	 * Abstract getter method for a mandatory attribute "parentPropertiesPath" of every class extending {@link #Page()}
	 * @return parentPropertiesPath for every child class of Page
	 */
	abstract public String getParentPropertiesPath(TestCase testCase);
	
	public String getPageSource(TestCase testCase) {
		String pageSource = null;
		try {
			pageSource = testCase.getDriverFactory().getDriver().getPageSource();
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		return pageSource;
	}
	
	/**
	 * This method waits till the given TestObject in the given TestCase is clickable
	 * And subsequently updates it in TestCaseCache
	 * @param testCaseObj TestCase Object
	 * @param objElement TestObj Object
	 */
	public void waitWhileElementExist(TestCase testCaseObj, TestObj objElement){
		testCaseObj.pauseExecutionUntil("elementToBeClickable", objElement);
		testCaseObj.testCaseCache.updateElementAfterWait(objElement);
	}

	/**
	 * This method is used to invoke any method of a sub class of Page, using its object
	 * @param method Method name of the page to be executed
	 * @param params Object[] for all the parameters of the method
	 * @return Object The object which is returned from the method
	 * @throws Exception
	 */
	public Object pageExecutor(String method, Object...params) throws Exception {
		Object o = null;
		try {
			Class<?> pageClass = this.getClass();
			Method[] pageMethod = pageClass.getMethods();
			for(Method m : pageMethod) {
				if(m.getName().equalsIgnoreCase(method)) {
					if(m.isVarArgs()) {
						Object[] obj = new Object[params.length];
						for(int i=0;i<params.length;i++) {
							obj[i] = params[i];
						}
						o = m.invoke(this,new Object[]{obj});
					}
					else
						o = m.invoke(this,params);
				}
			}
		}
		catch(Exception e) {
			e.printStackTrace();
			throw new Exception("illegal functionality call: "+e.getMessage());
		}
		return o;
	}
	
	/**
	 * This method is used to validate if the set of given TestObjs are present on the currently displayed page
	 * @param brandPageObj
	 * @return successFlag denotes if the given TestObjs are present on the page presently displayed
	 */
	public boolean validatePageUi(TestObj... brandPageObj)
	{
		Boolean present = false;
		if(brandPageObj.length != 0)
		{
			for (TestObj testObj :brandPageObj) {
				try {
					TestCase.elementExistOrNot(testObj);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if(TestCase.snapdealMobileStepReportingStatus == true)
				{
					present= true;
				}
				else
				{
					present = false;
					break;
				}
			}
		}
		return present;
	}
	
	/**
	 * This method extracts the Object of the page object invoking this method from the Map of page objects given as parameter
	 * @param m Map containing some page objects set 
	 * @return The reference of the current page object calling this method from the supplied map
	 */
	public Page getPageObject(Map<String, Page> m) {
		Page p = null;
		try {
			String name = this.getClass().getName();
			Iterator<Map.Entry<String, Page>> itr = m.entrySet().iterator();
			while (itr.hasNext()) {
				Map.Entry<String, Page> me = itr.next();
				if (me.getKey().equals(name)) {
					p = me.getValue();
					break;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return p;
	}

	/**
	 * This method adds a reference of invoking page object type to a Map containing a set of Page Objects and returns the same
	 * @param m Map containing some page objects set 
	 * @param p The reference of invoking page object type to be include in Map
	 * @return Map:Page Map of Page objects with the reference of the invoking page object type included
	 */
	public Map<String, Page> setPageObject(Map<String, Page> m, Page p) {
		try {
			String name = this.getClass().getName();
			m.put(name, p);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return m;
	}
	
	public static long computeLoadTime(LoadTimeStartPoint ltsp, LoadTimeMesurable ltm, TestCase testCase) {
		Long loadStartTime = 0l, loadEndTime = 0l, loadTime = 0l;
		try {
			if(ltsp.load(testCase))
				loadStartTime = System.currentTimeMillis();
			while(!ltm.assertLoaded(testCase));
			loadEndTime = System.currentTimeMillis();
			loadTime = loadEndTime - loadStartTime;
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		return loadTime;
	}
	
	public static long computeLoadTimeSinceLaunch(LoadTimeMesurable ltm, TestCase testCase) {
		Long loadStartTime = testCase.getDriverFactory().getDriverTimeStamp(), loadEndTime = 0l, loadTime = 0l;
		try {
//			while(!ltm.assertLoaded(testCase)){
//				
//			}
			loadEndTime = System.currentTimeMillis();
			loadTime = loadEndTime - loadStartTime;
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		return loadTime;
	}
	
	
	 public String convertToIndianCurrency(long number) {
		 NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("en", "IN"));
		 formatter.setMaximumFractionDigits(0);
		 DecimalFormatSymbols decimalFormatSymbols = ((DecimalFormat) formatter).getDecimalFormatSymbols();
		 decimalFormatSymbols.setCurrencySymbol("");
		 ((DecimalFormat) formatter).setDecimalFormatSymbols(decimalFormatSymbols);
		 String moneyString = formatter.format(number).trim();
		 moneyString = moneyString.replaceAll("\\u00A0", "");
		 return moneyString;
		 }
}
