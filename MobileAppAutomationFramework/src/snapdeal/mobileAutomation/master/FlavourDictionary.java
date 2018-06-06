package snapdeal.mobileAutomation.master;

import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.lang.reflect.Method;

import testCaseReporting.TestCaseReporting;

/**
 * This Class serves as generic dictionary service for any execution platform a.k.a. flavour and even for test case scripts
 * @author Khagesh Kapil
 */
public class FlavourDictionary implements Map.Entry<String, Map<String,Object>>{

	/**
	 * Name of the platform for which the dictionary service is to be provided
	 */
	private String platform;
	
	public static String flavourMapPath="";
	
	public static String binariesPath="";
	
	public String consoleOutput="true";
	
	/**
	 * Map containing pool of java objects for the flavour specified and their key being their java class names
	 */
	private Map<String,Object> pageObjects = new HashMap<String, Object>();
	
	/**
	 * Method to populate {@link #pageObjects} with the java objects of specified platform/flavour
	 * @param plf Name of the platform/flavour
	 */
	private void loadObjects(String plf) {
		try {
			Properties p = new Properties();
			//p.load(new FileInputStream(new File(getClass().getProtectionDomain().getCodeSource().getLocation().getPath()+"snapdeal/mobileAutomation/master/masterConfig/flavourMap.properties")));
			p.load(new FileInputStream(new File(flavourMapPath)));
			Enumeration<?> e = p.propertyNames();
			while(e.hasMoreElements()) {
				String itrPlat = (String) e.nextElement();
				if(itrPlat.equals(plf)) {
					FileFilter fileFilter = new FileFilter();
					consoleOutput = p.getProperty("consoleOutput");
//					binariesPath = p.getProperty("binariesPath");
//					String[] s = new File(p.getProperty("binariesPath")+p.getProperty(itrPlat)).list(fileFilter);
//					List<String> fileList= fileFilter.getUpdateArray(p.getProperty("binariesPath")+p.getProperty(itrPlat),s);
					String[] s = new File(binariesPath+p.getProperty(itrPlat)).list(fileFilter);
					List<String> fileList= fileFilter.getUpdateArray(binariesPath+p.getProperty(itrPlat),s);
					/*String[] s = new File(p.getProperty("binariesPath")+p.getProperty(itrPlat)).list(new FilenameFilter(){
				        @Override
				        public boolean accept(File dir, String name) {
				            return name.toLowerCase().endsWith(".class");
				        }
					}
					);*/
					for(String str : fileList) {
						str = str.replace(".class","");
						String doubleSlash = "\\";
						String platform = p.getProperty(itrPlat).replaceAll("/", ".");
						platform = platform.replace(doubleSlash, ".");
						str = str.replaceAll("/", ".");
						str = str.replace(doubleSlash, ".");
						str = str.substring(str.indexOf(platform));
//						String s1 = p.getProperty(itrPlat).replaceAll("/", ".")+"."+str;
						String s1 = str.replaceAll("/", ".");
						System.out.println(s1);
						String page = s1.split("[.]")[s1.split("[.]").length-1];
						Class<?> clz= Class.forName(s1);
						pageObjects.put(page,(Object) clz.newInstance());
					}
					break;
				}
			}
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Constructor
	 * @param plf platform/flavour for dictionary service
	 */
	public FlavourDictionary(String plf) {
		platform = plf;
		loadObjects(plf);
	}
	
	/**
	 * Getter method for {@link #platform}
	 * @return {@link #platform}
	 */
	@Override
	public String getKey() {
		return platform;
	}

	/**
	 * Getter Method for {@link #pageObjects}
	 * @return {@link #pageObjects}
	 */
	@Override
	public Map<String,Object> getValue() {
		return pageObjects;
	}

	/**
	 * Setter method for {@link #pageObjects}
	 * @returns {@link #pageObjects}
	 */
	@Override
	public Map<String,Object> setValue(Map<String,Object> m) {
		this.pageObjects = m;
		return this.pageObjects;
	}
	
//	public static void main(String[] args) {
//		new FlavourDictionary("iosApp");
//	}
	
	public void setFlavourProperty(String flavourPropertyName, Object flavourPropertyValue) throws
		IllegalArgumentException, IllegalAccessException, InvocationTargetException {
		for(String flavourInstance : pageObjects.keySet()) {
			Method[] methords = pageObjects.get(flavourInstance).getClass().getMethods();
			for(Method m : methords) {
				if(m.getName().equals(flavourPropertyName)) {
					m.invoke((ServiceApi) pageObjects.get(flavourInstance), (TestCaseReporting) flavourPropertyValue);
				}
			}
		}
	}
	
	public void setApiFlavorEnvironment(String setEnvMethod, String env) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		for(String flavourInstance : pageObjects.keySet()) {
			Method[] methords = pageObjects.get(flavourInstance).getClass().getMethods();
			for(Method m : methords) {
				if(m.getName().equals(setEnvMethod)) {
					m.invoke((ServiceApi) pageObjects.get(flavourInstance), (String) env);
				}
			}
		}
	}

}

class FileFilter implements FilenameFilter {

	private List<String> fileList = new ArrayList<String>();
	
	@Override
	public boolean accept(File dir, String name) {
		String path = dir.getPath();
		File file = new File(path+"/"+name);
		if(file.isDirectory())
			list(file);
		return name.toLowerCase().endsWith(".class");
	}
	
	public List<String> getUpdateArray(String basePath,String[] s) {
        if(s!=null) {
            for(String itrFileName : s) {
                fileList.add(basePath+"/"+itrFileName);
            }
        }
        return fileList;
    }

	private void list(File file) {
		File[] files = file.listFiles();
		for(File f : files) {
			if(accept(file,f.getName()))
				fileList.add(file.getPath()+"/"+f.getName());
		}
	}
}
