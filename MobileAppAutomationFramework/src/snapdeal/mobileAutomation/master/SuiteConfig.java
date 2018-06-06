package snapdeal.mobileAutomation.master;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamReader;

public class SuiteConfig {

	private String configPath;
	
	private String name = "";
	
	private int instances;
	
	private Map<String,Map<String,Object>> devices;
	
	private Map<String,Object> defaultAppiumConfig;
	
	private Map<String,Map<String,Object>> cases;
	
	public String getSuiteConfigPath() {
		return configPath;
	}
	
	SuiteConfig(String suiteConfigPath) {
		configPath = suiteConfigPath;
		devices = new HashMap<String,Map<String,Object>>();
		defaultAppiumConfig = new HashMap<String,Object>();
		cases = new HashMap<String,Map<String,Object>>();
		processSuiteConfig();
	}
	
	private void processSuiteConfig() {
		boolean isName = false, 
				isSuiteDevices = false,
				isSuiteDevice = false,
				isTestCases = false;
		XMLInputFactory xmlInputFactory = XMLInputFactory.newInstance();
		try {
			if(!(new File(configPath)).exists())
				throw new Exception("Suite Config file does not exist at "+configPath);
			XMLStreamReader xmlStreamReader = xmlInputFactory.createXMLStreamReader(new FileInputStream(configPath));
			int event = xmlStreamReader.getEventType();
			boolean flag = true;
			String currentTagName = "", info = "", content = "";
			while(flag)	{
				content="";
				switch(event) {
					case XMLStreamConstants.START_ELEMENT:
						currentTagName = xmlStreamReader.getLocalName();
						info = "start event for ";
						if(currentTagName.equals("name") && name.equals(""))
							isName = true;
						if(currentTagName.equals("testSuite")) {
							int attrCount = xmlStreamReader.getAttributeCount();
							int itr = 0;
							while(itr<attrCount) {
								if(xmlStreamReader.getAttributeLocalName(itr).equals("executionInstances"))
									instances = Integer.parseInt(xmlStreamReader.getAttributeValue(itr));
								itr++;
							}
						}
						if(currentTagName.equals("suiteDevices"))
							isSuiteDevices = true;
						if(currentTagName.equals("device") && isSuiteDevices)
							isSuiteDevice = true;
						if(currentTagName.equals("appiumConfig"))
							captureDefaultAppiumConfigInfo(xmlStreamReader);
						if(currentTagName.equals("testCases"))
							isTestCases = true;
						if(currentTagName.equals("testCase") && isTestCases) {
							cases = captureCasesInfo(xmlStreamReader, cases);
						}
						break;
					case XMLStreamConstants.CHARACTERS:
						info = "character event for ";
						content = xmlStreamReader.getText();
						if(isName) {
							name = content;
							isName = false;
						}
						if(isSuiteDevice){
							devices = captureDeviceInfo(xmlStreamReader, devices);
							isSuiteDevice = false;
						}
						break;
					case XMLStreamConstants.END_ELEMENT:
						currentTagName = xmlStreamReader.getLocalName();
						info = "end event for ";
						if(currentTagName.equals("device") && isSuiteDevices)
							isSuiteDevice = false;
						if(currentTagName.equals("suiteDevices"))
							isSuiteDevices = false;
						if(currentTagName.equals("testCases"))
							isTestCases = false;
						if(currentTagName.equals("testSuite")) {
							flag = false;
						}
						break;
                }
				System.out.println(info+currentTagName+"-->\""+content+"\"");
                if (!xmlStreamReader.hasNext())
                	break;
              event = xmlStreamReader.next();
            }
		}
		catch(Exception e) {
			System.err.println(e.getMessage());
			e.printStackTrace();
		}
	}

	private Map<String,Map<String,Object>> captureCasesInfo(XMLStreamReader xmlStreamReader,Map<String,Map<String,Object>> cases) {
		boolean isName = false,
				isFlavor = false,
				isEnvironment = false,
				isResetApp = false;
		int event = xmlStreamReader.getEventType();
		String testCaseName = "",currentTagName = "",info = "",content="";
		List<String> testDataIds = new ArrayList<String>();
		boolean flag = true;
		try {
			while(flag)	{
				switch(event) {
					case XMLStreamConstants.START_ELEMENT:
						info = "start event for ";
						currentTagName = xmlStreamReader.getLocalName();
						if(currentTagName.equals("testCase")) {
							int attrCount = xmlStreamReader.getAttributeCount();
							int itr = 0;
							while(itr<attrCount) {
								if(xmlStreamReader.getAttributeLocalName(itr).equals("testDataIterations"))
									testDataIds = Arrays.asList(xmlStreamReader.getAttributeValue(itr).split(","));
								itr++;
							}
						}
						if(currentTagName.equals("name"))
							isName = true;
						if(currentTagName.equals("flavor"))
							isFlavor = true;
						if(currentTagName.equals("environment"))
							isEnvironment = true;
						if(currentTagName.equals("resetApp"))
							isResetApp = true;
						break;
					case XMLStreamConstants.CHARACTERS:
						info = "character event for ";
						content = xmlStreamReader.getText();
						if(isName) {
							testCaseName = content;
							cases.put(testCaseName, new HashMap<String, Object>());
							Map<String, Object> caseMap = cases.get(testCaseName);
							caseMap.put("testDataIterations", testDataIds);
							isName = false;
						}
						if(isFlavor) {
							Map<String, Object> caseMap = cases.get(testCaseName);
							caseMap.put("flavor", content);
							isFlavor = false;
						}
						if(isEnvironment) {
							Map<String, Object> caseMap = cases.get(testCaseName);
							caseMap.put("environment", content);
							isEnvironment = false;
						}
						if(isResetApp) {
							Map<String, Object> caseMap = cases.get(testCaseName);
							caseMap.put("resetApp", content);
							isResetApp = false;
						}
						break;
					case XMLStreamConstants.END_ELEMENT:
						currentTagName = xmlStreamReader.getLocalName();
						info = "end event for ";
						if(currentTagName.equals("testCase"))
							flag = false;
						break;
				}
				System.out.println(info+currentTagName+"-->\""+content+"\"");
				if (!xmlStreamReader.hasNext())
					break;
				event = xmlStreamReader.next();
			}
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		return cases;
	}
	
	private Map<String,Map<String,Object>> captureDeviceInfo(XMLStreamReader xmlStreamReader,Map<String,Map<String,Object>> devices){
		boolean isDeviceName = false,
				isFlavor = false,
				isAppiumFlag = false,
				isFlag = false;	
		String deviceName = "";
		Map<String,String> flags = null;
		int event = xmlStreamReader.getEventType();
		String currentTagName = "",info = "",content="";
		boolean flag = true;
		try {
			while(flag)	{
				switch(event) {
					case XMLStreamConstants.START_ELEMENT:
						info = "start event for ";
						currentTagName = xmlStreamReader.getLocalName();
						if(currentTagName.equals("name"))
							isDeviceName = true;
						if(currentTagName.equals("flavor"))
							isFlavor = true;
						if(currentTagName.equals("appiumFlags"))
							isAppiumFlag = true;
						if(currentTagName.equals("flag"))
							isFlag = true;
						break;
					case XMLStreamConstants.CHARACTERS:
						info = "character event for ";
						content = xmlStreamReader.getText();
						if(isDeviceName){
							deviceName = xmlStreamReader.getText();
							devices.put(deviceName, new HashMap<String,Object>());
							isDeviceName = false;
						}
						if(isFlavor){
							devices.get(deviceName).put("flavor", xmlStreamReader.getText());
							isFlavor = false;
						}
						if(isAppiumFlag)
							flags = new HashMap<String, String>();
						if(isFlag){
							flags = captureAppiumFlags(xmlStreamReader,flags);
							isFlag = false;
							isAppiumFlag = false;
						}
						break;
					case XMLStreamConstants.END_ELEMENT:
						currentTagName = xmlStreamReader.getLocalName();
						info = "end event for ";
						if(currentTagName.equals("appiumFlags")){
							devices.get(deviceName).put("appiumFlags", flags);
							isAppiumFlag = false;
						}
						if(currentTagName.equals("device")){
							System.out.println("This is where device ends");
							flag = false;
						}
						break;
				}
				System.out.println(info+currentTagName+"-->\""+content+"\"");
				if (!xmlStreamReader.hasNext())
					break;
				event = xmlStreamReader.next();
			}
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		return devices;
	}
	
	private void captureDefaultAppiumConfigInfo(XMLStreamReader xmlStreamReader){
		boolean isHost = false,
				isPort = false,
				isDefaultArguments = false,
				isDefaultArgument = false;
		String deviceName = "";
		Map<String,String> arguments = null;
		int event = xmlStreamReader.getEventType();
		String currentTagName = "",info = "",content="";
		boolean flag = true;
		try {
			while(flag)	{
				switch(event) {
					case XMLStreamConstants.START_ELEMENT:
						info = "start event for ";
						currentTagName = xmlStreamReader.getLocalName();
						if(currentTagName.equals("serverHost"))
							isHost = true;
						if(currentTagName.equals("serverPort"))
							isPort = true;
						if(currentTagName.equals("serverDefaultArguments"))
							isDefaultArguments = true;
						if(currentTagName.equals("defaultArgument"))
							isDefaultArgument = true;
						break;
				
					case XMLStreamConstants.CHARACTERS:
						info = "character event for ";
						content = xmlStreamReader.getText();
						if(isHost){
							defaultAppiumConfig.put("serverHost", content);
							isHost = false;
						}
						if(isPort){
							defaultAppiumConfig.put("serverPort", xmlStreamReader.getText());
							isPort = false;
						}
						if(isDefaultArguments)
							arguments = new HashMap<String, String>();
						if(isDefaultArgument){
							arguments = captureAppiumFlags(xmlStreamReader,arguments);
							isDefaultArgument = false;
							isDefaultArguments = false;
						}
						break;
					case XMLStreamConstants.END_ELEMENT:
						currentTagName = xmlStreamReader.getLocalName();
						info = "end event for ";
						if(currentTagName.equals("serverDefaultArguments")){
							defaultAppiumConfig.put("serverDefaultArguments", arguments);
							isDefaultArguments = false;
						}
						if(currentTagName.equals("appiumConfig")){
							System.out.println("This is where appiumConfig ends");
							flag = false;
						}
						break;
				}
				System.out.println(info+currentTagName+"-->\""+content+"\"");
				if (!xmlStreamReader.hasNext())
					break;
				event = xmlStreamReader.next();
			}
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}

	private Map<String,String> captureAppiumFlags(XMLStreamReader xmlStreamReader,Map<String,String> flags){
		
		boolean isName = false,
				isValue = false;
		int event = xmlStreamReader.getEventType();
		String currentTagName = "",info = "",content = "",name = "",value = "";
		boolean flag = true;
		try{
			while(flag)	{
				switch(event) {
					case XMLStreamConstants.START_ELEMENT:
						info = "start event for ";
						currentTagName = xmlStreamReader.getLocalName();
						if(currentTagName.equals("name"))
							isName = true;
						if(currentTagName.equals("value"))
							isValue = true;
						break;
					case XMLStreamConstants.CHARACTERS:
						info = "character event for ";
						content = xmlStreamReader.getText();
						if(isName){
							name = xmlStreamReader.getText();
							isName = false;
						}
						if(isValue){
							value = xmlStreamReader.getText();
							isValue = false;
						}
						break;
					case XMLStreamConstants.END_ELEMENT:
						currentTagName = xmlStreamReader.getLocalName();
						info = "end event for ";
						if(currentTagName.equals("value")){
							flags.put(name, value);
							flag = false;
						}
				}
				System.out.println(info+currentTagName+"-->\""+content+"\"");
				if (!xmlStreamReader.hasNext())
					break;
				event = xmlStreamReader.next();
			}
		}
		catch(Exception e){
			e.printStackTrace();
		}
		return flags;
	}
	
	public String getConfigPath() {
		return configPath;
	}

	public String getName() {
		return name;
	}

	public int getInstances() {
		return instances;
	}

	public Map<String, Map<String, Object>> getDevices() {
		return devices;
	}

	public Map<String,Object> getDefaultAppiumConfig() {
		return defaultAppiumConfig;
	}

	public Map<String, Map<String, Object>> getCases() {
		return cases;
	}

}
