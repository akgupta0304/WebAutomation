package snapdeal.mobileAutomation.master;

import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

public class Initiator {

	private String flavourMapPath;
	
	private String mailingList = null;
	
	private String autoDeployPath = null;
	
	public void setFrameworkConfigPath (String frameworkConfigPath) {
		
	}
	
	public void setSuiteConfigPath (String suiteConfigPath) {
		
	}
	
	public void startSuitesExecution(List<String> suites) {
		String header = AllowedFlags.generateCharacterString('-', 80)+
				"\n"+AllowedFlags.generateCharacterString(' ', 29)+"TEST SUITES EXECUTION"+AllowedFlags.generateCharacterString(' ', 30)+
				"\n"+AllowedFlags.generateCharacterString('-', 80);
		System.out.println(header);
		Properties p = new Properties();
		try {
			p.load(new FileInputStream(flavourMapPath));
			String[] availableSuites = new File(p.getProperty("binariesPath")+
					p.getProperty("testSuites")).list(new FilenameFilter() {
				@Override
		        public boolean accept(File dir, String name) {
					if(name.startsWith("Initiator") || name.startsWith("AllowedFlags"))
						return false;
		            return name.toLowerCase().endsWith(".class");
		        }
			});
			for(String suite : suites) {
				String suiteHeader = "\nSuite : "+suite+"\n"+AllowedFlags.generateCharacterString('-',80);
				System.out.println(suiteHeader);
				/*if(!Arrays.asList(availableSuites).contains(suite.split("::")[0]+".class")) {
					System.out.println("\nThis suite doesn't exist");
					continue;
				}*/
				/*else {*/
					String suiteClass = p.getProperty("testSuites").replaceAll("/", ".")+"."+suite/*.split("::")[0]*/;
					/*if(suite.contains("::")) {
						String suiteConfig = suite.substring(suite.indexOf("::")+2);
						System.out.println(suiteConfig);
					}*/
					/*TestSuiteTokenQueue.suiteSequence++;
					TestSuiteTokenQueue.setQueue(suites.toArray(new String[suites.size()]));*/
					Class<?> clz= Class.forName(suiteClass);
					Method meth = clz.getMethod("main", String[].class);
				    String[] params = new String[2]; // init params accordingly
				    params[0] = autoDeployPath;
				    params[1] = mailingList;
				    meth.invoke(null, (Object) params);
//					Object obj = clz.newInstance();
//					clz.getMethod("terminateSuite", null).invoke(obj, null);
					/*clz.getMethod("initiateReport").invoke(obj);
					clz.getMethod("flow").invoke(obj);
					clz.getMethod("endReport").invoke(obj);*/
//				}
			}
		}
		catch(Exception e) {
//			System.out.println(e);
			e.printStackTrace();
		}
	}
	
	public List<String> getFlagInputs(String flag,String[] options) {
		List<String> parameters = new ArrayList<String>();
		try {
			boolean startCollection = false;
			for(String option : options) {
				if(option.equalsIgnoreCase(flag)) {
					startCollection = true;
					continue;
				}
				if(startCollection) {
					if(option.startsWith("-"))
						break;
					parameters.add(option);
				}
			}
			if(flag.equals(AllowedFlags.AUTO_DEPLOY) && (parameters.size()==0))
				parameters.add("");
		}
		catch(Exception e) {	
		}
		return parameters;
	}

	public void processInput(String[] inputs) {
		List<String> testSuites = getFlagInputs(AllowedFlags.TEST_SUITES,inputs);
		List<String> frameworkConfigPath = getFlagInputs(AllowedFlags.FRAMEWORK_CONFIG,inputs);
		List<String> suiteConfigPath = getFlagInputs(AllowedFlags.SUITE_CONFIG,inputs);
		List<String> flavourMapPath = getFlagInputs(AllowedFlags.FLAVOR_MAP,inputs);
		mailingList = getFlagInputs(AllowedFlags.MAILING_LIST, inputs).toString();
		autoDeployPath = getFlagInputs(AllowedFlags.AUTO_DEPLOY, inputs).get(0);
		boolean helpQuery = Arrays.asList(inputs).contains(AllowedFlags.HELP);
		boolean flavorMapCommand = Arrays.asList(inputs).contains(AllowedFlags.FLAVOR_MAP);
		boolean frameworkConfigCommand = Arrays.asList(inputs).contains(AllowedFlags.FRAMEWORK_CONFIG);
		boolean suiteConfigCommand = Arrays.asList(inputs).contains(AllowedFlags.SUITE_CONFIG);
		boolean testSuiteCommand = Arrays.asList(inputs).contains(AllowedFlags.TEST_SUITES);
		try {
			if(flavorMapCommand) {
				if(!(flavourMapPath.size()>1)) {
					this.flavourMapPath = flavourMapPath.get(0);
					FlavourDictionary.flavourMapPath = flavourMapPath.get(0);
				}
				else {
					System.out.println("invalid sytax for flavor map command");
					System.out.println("Exiting...");
					System.exit(0);
				}
			}
			if(helpQuery) {
				if(inputs.length>1) {
					System.out.println("invalid sytax for help command");
					System.out.println("Exiting...");
					System.exit(0);
				}
				else {
					System.out.println(AllowedFlags.description());
				}
			}
			/*if(availableTestSuitesQuery) {
				if(inputs.length>1) {
					System.out.println("invalid sytax for availableTestSuites command");
					System.out.println("Exiting...");
					System.exit(0);
				}
				else {
					System.out.println(getAvailableTestSuites());
				}
			}
			if(showTestSuiteDetails.size()>0) {
				if(!(inputs.length == showTestSuiteDetails.size()+1)) {
					System.out.println("invalid sytax for showTestSuiteDetails command");
					System.out.println("Exiting...");
					System.exit(0);
				}
				else {
					System.out.println(getTestSuiteDetails(showTestSuiteDetails));
				}
			}*/
			if(frameworkConfigCommand) {
				if(frameworkConfigPath.size()>0) {
					System.out.println("invalid sytax for framework config command");
					System.out.println("Exiting...");
					System.exit(0);
				}
				else {
					setFrameworkConfigPath(frameworkConfigPath.get(0));
				}
			}
			if(suiteConfigCommand) {
				if(suiteConfigPath.size()>0) {
					if(frameworkConfigPath.size() == 0) {
						System.out.println("invalid sytax for objectInput command");
						System.out.println("Exiting...");
						System.exit(0);
					}
					else
						setSuiteConfigPath(suiteConfigPath.get(0));
				}
			}
			if(testSuiteCommand) {
				if(testSuites.size()>0) {
					if((inputs.length-(testSuites.size()+flavourMapPath.size())<2)) {
						System.out.println("invalid sytax for testSuites command");
						System.out.println("Exiting...");
						System.exit(0);
					}
					else
						startSuitesExecution(testSuites);
				}
			}
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String args[]) {
		Initiator ini = new Initiator();
		ini.processInput(args);
	}
}

class AllowedFlags {
	/**
	 * To specify test suite execution (can be given used with other flags)
	 */
	final static String TEST_SUITES = "-testSuites";
	
	/**
	 * To specify path of framework configuration file for execution (can be given/used with other flags)
	 */
	final static String FRAMEWORK_CONFIG = "-frameworkConfig";
	
	/**
	 * To specify path of suite configuration file for execution (can be given/used with other flags)
	 */
	final static String SUITE_CONFIG = "-suiteConfig";
	
	/**
	 * To see all the flag names with their description (can not be clubbed with other flags)
	 */
	final static String HELP = "-help";

	/**
	 * To specify the path to flavor map properties file (can be clubbed with other flags)
	 */
	final static String FLAVOR_MAP = "-flavorMap";
	
	/**
	 * To specify the path to auto deploy folder (can be clubbed with other flags)
	 */
	final static String AUTO_DEPLOY = "-autoDeploy";
	
	/**
	 * To specify the execution results mailing list (can be clubbed with other flags)
	 */
	final static String MAILING_LIST = "-mailTo";
	
	public static String description() {
		String retStr="";
		try {
			retStr += "FLAG"+generateCharacterString(' ',26)+"DESCRIPTION"+generateCharacterString(' ',39)+"\n";
			retStr += generateCharacterString('-',25)+generateCharacterString(' ',5)+generateCharacterString('-',50)+"\n";
			retStr += "-testSuites"+generateCharacterString(' ',19)+"to specify test suites for execution"+"\n";
			retStr += "-frameworkConfig"+generateCharacterString(' ',14)+"to specify path to framework config XML file"+"\n";
			retStr += "-suiteConfig"+generateCharacterString(' ',18)+"to specify path to suite config XML file"+"\n";
			retStr += "-flavorMap"+generateCharacterString(' ',20)+"to specify path to flavor map properties file"+"\n";
			retStr += "-autoDeploy"+generateCharacterString(' ',19)+"to specify the path to auto deploy folder"+"\n";
			retStr += "-mailTo"+generateCharacterString(' ',23)+"to specify the execution results mailing list"+"\n";
			retStr += "-help"+generateCharacterString(' ',25)+"to show help menu for all flags"+"\n";
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		return retStr;
	}
	
	public static String generateCharacterString(char c,int num) {
		String characterString="";
		for(int i=0;i<num;i++) {
			characterString += c;
		}
		return characterString;
	}
}