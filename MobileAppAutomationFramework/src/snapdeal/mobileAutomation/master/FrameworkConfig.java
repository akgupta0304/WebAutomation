package snapdeal.mobileAutomation.master;

import java.io.File;
import java.io.FileInputStream;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamReader;

import org.openqa.selenium.remote.DesiredCapabilities;

public enum FrameworkConfig {
	FRAMEWORK_CONFIG("");
	
	private String frameworkConfigPath;
	
	private String binariesPath;
	
	private Map<String,Map<String,Object>> devices;
	
	private Map<String,Map<String,Object>> flavorMap;
	
	public String getFrameworkConfigPath() {
		return frameworkConfigPath;
	}

	public void setFrameworkConfigPath(String frameworkConfigPath) {
		this.frameworkConfigPath = frameworkConfigPath;
	}
	
	public String getBinariesPath() {
		return binariesPath;
	}

	public Map<String, Map<String, Object>> getDevices() {
		return devices;
	}

	public Map<String, Map<String, Object>> getFlavorMap() {
		return flavorMap;
	}

	FrameworkConfig(String frameworkConfigPath) {
		this.frameworkConfigPath = frameworkConfigPath;
		devices = new HashMap<String,Map<String,Object>>();
		flavorMap = new HashMap<String,Map<String,Object>>();
		processFrameworkConfig(frameworkConfigPath);
	}
	
	public void processFrameworkConfig() {
		this.processFrameworkConfig(this.frameworkConfigPath);
	}
	
	public void show() {
		System.out.println(getBinariesPath());
	}
	
	private BuildSet captureBuildInfo(XMLStreamReader xmlStreamReader,BuildSet buildSet) {
		System.out.println("captureBuildInfo called");
		Build build = new Build();
		boolean isBuildType = false,
				isBuildCapabilities = false,
				isBuildProperties = false;
		int event = xmlStreamReader.getEventType();
		String buildCapability = "", buildProperty = "";
		boolean flag = true;
		String currentTagName = "",info = "",content="";
		try {
			while(flag)	{
				content="";
				switch(event) {
					case XMLStreamConstants.START_ELEMENT:
						currentTagName = xmlStreamReader.getLocalName();
						info = "start event for ";
						if(currentTagName.equals("type"))
							isBuildType = true;
						if(currentTagName.equals("buildCapabilities")) {
							build.setBuildCapabilities(new HashMap<String,String>());
							isBuildCapabilities = true;
						}
						if(isBuildCapabilities) {
							buildCapability = currentTagName;
						}
						if(isBuildProperties) {
							buildProperty = currentTagName;
						}
						break;
					case XMLStreamConstants.CHARACTERS:
						info = "character event for ";
						content = xmlStreamReader.getText();
						if(isBuildType) {
							build.setBuildType(xmlStreamReader.getText());	
							isBuildType = false;
						}
						if(isBuildCapabilities && !buildCapability.equals("") && !xmlStreamReader.getText().contains("\n") && !xmlStreamReader.getText().contains("\t"))
							build.getBuildCapabilities().put(buildCapability, xmlStreamReader.getText());
						if(isBuildProperties && !buildProperty.equals("") && !xmlStreamReader.getText().contains("\n") && !xmlStreamReader.getText().contains("\t"))
							build.getBuildProperties().put(buildProperty, xmlStreamReader.getText());
						break;
					case XMLStreamConstants.END_ELEMENT:
						currentTagName = xmlStreamReader.getLocalName();
						info = "end event for ";
						if(xmlStreamReader.getLocalName().equals("buildCapabilities")) {
							isBuildCapabilities = false;
							build.setBuildProperties(new HashMap<String,String>());
							isBuildProperties = true;
						}
						if(xmlStreamReader.getLocalName().equals("build")) {
							isBuildProperties = false;
							buildSet.getBuildSet().add(build);
							flag = false;
						}
						break;
				}
				System.out.println(info+currentTagName+"-->\""+content+"\"");
				if (!xmlStreamReader.hasNext())
					break;
				event = xmlStreamReader.next();
			}
			System.out.println("and the buildset object made is : "+buildSet);
		}
		catch(Exception e) {
		}
		return buildSet;
	}
	
	private void processFrameworkConfig(String frameworkConfigPath) {
		boolean setBinary = false, /*isDevice = false,*/ isFrameworkDevices = false;
		XMLInputFactory xmlInputFactory = XMLInputFactory.newInstance();
		main:
		try {
			if(!(new File(frameworkConfigPath)).exists())
				break main;
			XMLStreamReader xmlStreamReader = xmlInputFactory.createXMLStreamReader(new FileInputStream(frameworkConfigPath));
			int event = xmlStreamReader.getEventType();
			boolean flag = true;
			String currentTagName = "", info = "", content = "";
			while(flag)	{
				content="";
				switch(event) {
					case XMLStreamConstants.START_ELEMENT:
						currentTagName = xmlStreamReader.getLocalName();
						info = "start event for ";
						if(currentTagName.equals("binariesPath"))
							setBinary = true;
						if(currentTagName.equals("frameworkDevices"))
							isFrameworkDevices = true;
						if(currentTagName.equals("device") && isFrameworkDevices) {
							/*isDevice = true;*/
							devices = captureFrameworkDevices(xmlStreamReader,devices);
						}
						if(currentTagName.equals("flavor") /*&& !isDevice*/) {
							flavorMap = captureFlavor(xmlStreamReader, flavorMap);
						}
						break;
					case XMLStreamConstants.CHARACTERS:
						info = "character event for ";
						content = xmlStreamReader.getText();
						if(setBinary) {
							binariesPath = xmlStreamReader.getText();
							setBinary = false;
						}
						break;
					case XMLStreamConstants.END_ELEMENT:
						currentTagName = xmlStreamReader.getLocalName();
						info = "end event for ";
						if(xmlStreamReader.getLocalName().equals("frameworkConfig")) {
							flag = false;
						}
						/*if(currentTagName.equals("device"))
							isDevice = false;*/
						if(currentTagName.equals("frameworkDevices"))
							isFrameworkDevices = false;
						break;
                }
				System.out.println(info+currentTagName+"-->\""+content+"\"");
                if (!xmlStreamReader.hasNext())
                	break;
              event = xmlStreamReader.next();
            }
			//System.out.println(buildSet);
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}

	private Map<String,Map<String,Object>> captureFlavor(XMLStreamReader xmlStreamReader,Map<String,Map<String,Object>> flavorMap) {
		boolean isFlavorName = false,
				isFlavorNameCaptured = false,
				isFlavorBuilds = false,
				isFlavorBase = false,
				isFlavorPages = false,
				isFlavorTestCases = false,
				isFlavorApiEnvironments = false,
				isFlavorApis = false;
		String flavor = "";
		BuildSet buildSet = null;
		int event = xmlStreamReader.getEventType();
		String currentTagName = "",info = "",content="";
		boolean flag = true;
		try {
			while(flag)	{
				content="";
				switch(event) {
					case XMLStreamConstants.START_ELEMENT:
						info = "start event for ";
						currentTagName = xmlStreamReader.getLocalName();
						if(currentTagName.equals("name") && !isFlavorNameCaptured)
							isFlavorName = true;
						if(currentTagName.equals("flavorBuilds")) {
							isFlavorBuilds = true;
							buildSet = new BuildSet();
							buildSet.setBuildSetFlavor(flavor);
						}
						if(isFlavorBuilds && currentTagName.equals("build"))
							buildSet = captureBuildInfo(xmlStreamReader,buildSet);
						if(currentTagName.equals("flavorParentPage"))
							flavorMap = captureFlavorParentPageInfo(xmlStreamReader, flavorMap, flavor);
						if(currentTagName.equals("flavorBase"))
							isFlavorBase = true;
						if(currentTagName.equals("flavorPages")) {
							flavorMap.get(flavor).put("flavorPages", new HashMap<String,Object>());
							isFlavorPages = true;
						}
						if(currentTagName.equals("page") && isFlavorPages)
							flavorMap = captureFlavorPageInfo(xmlStreamReader, flavorMap, flavor);
						if(currentTagName.equals("flavorDefaultParameters")) {
							flavorMap.get(flavor).put("flavorDefaultParameters", new HashMap<String,Object>());
							Object obj = flavorMap.get(flavor).get("flavorDefaultParameters");
							@SuppressWarnings("unchecked")
							HashMap<String,Object> hashMap = (HashMap<String,Object>) obj;
							hashMap.put("capability", new HashMap<String,Object>());
							hashMap.put("property", new HashMap<String,Object>());
							flavorMap = captureFlavorDefaultParameters(xmlStreamReader, flavorMap, flavor);
						}
						if(currentTagName.equals("flavorTestCases")) {
							flavorMap.get(flavor).put("flavorTestCases", new HashMap<String,Object>());
							isFlavorTestCases = true;
						}
						if(currentTagName.equals("flavorApiEnvironments")) {
							flavorMap.get(flavor).put("flavorApiEnvironments", new HashMap<String,Object>());
							isFlavorApiEnvironments = true;
						}
						if(currentTagName.equals("testCase") && isFlavorTestCases)
							flavorMap = captureTestCaseInfo(xmlStreamReader, flavorMap, flavor);
						if(currentTagName.equals("environment") && isFlavorApiEnvironments)
							flavorMap = captureApiEnvInfo(xmlStreamReader, flavorMap, flavor);
						if(currentTagName.equals("flavorGlobalParam")) {
							flavorMap.get(flavor).put("flavorGlobalParam", new HashMap<String,Object>());
							event = xmlStreamReader.next();
							flavorMap = captureflavorGlobalParam(xmlStreamReader, flavorMap, flavor);
						}
						if(currentTagName.equals("flavorApis")) {
							isFlavorApis = true;
							flavorMap.get(flavor).put("flavorApis", new HashMap<String,Object>());
						}
						if(isFlavorApis && currentTagName.equals("api")) {
							event = xmlStreamReader.next();
							flavorMap = captureflavorApiInfo(xmlStreamReader, flavorMap, flavor);
						}
						break;
					case XMLStreamConstants.CHARACTERS:
						info = "character event for ";
						content = xmlStreamReader.getText();
						if(isFlavorName) {
							isFlavorNameCaptured = true;
							flavor = xmlStreamReader.getText();
							flavorMap.put(flavor, new HashMap<String,Object>());
							isFlavorName = false;
						}
						if(isFlavorBase) {
							System.out.println("reached flavor base");
							flavorMap.get(flavor).put("flavorBase", xmlStreamReader.getText());
							isFlavorBase = false;
						}
						break;
					case XMLStreamConstants.END_ELEMENT:
						currentTagName = xmlStreamReader.getLocalName();
						info = "end event for ";
						if(xmlStreamReader.getLocalName().equals("flavor")) {
							System.out.println("This is where flavor ends");
							flag = false;
						}
						if(xmlStreamReader.getLocalName().equals("flavorPages"))
							isFlavorPages = false;
						if(xmlStreamReader.getLocalName().equals("flavorBuilds")) {
							isFlavorBuilds = false;
							System.out.println("this is flavor builds end element event and buildset to be added is:\n"+buildSet);
							flavorMap.get(flavor).put("flavorBuilds", buildSet);
							buildSet = null;
						}
						if(xmlStreamReader.getLocalName().equals("flavorTestCases"))
							isFlavorTestCases = false;
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
		return flavorMap;
	}
	
	@SuppressWarnings("unchecked")
	private Map<String,Map<String,Object>> captureFlavorDefaultParameters(XMLStreamReader xmlStreamReader,Map<String,Map<String,Object>> flavorMap,String flavor) {
		boolean isName = false,
				isValue = false;
		String name = "", value = "";
		int event = xmlStreamReader.getEventType();
		String currentTagName = "",info = "",content="";
		boolean flag = true;
		try {
			while(flag)	{
				content="";
				switch(event) {
					case XMLStreamConstants.START_ELEMENT:
						currentTagName = xmlStreamReader.getLocalName();
						info = "start event for ";
						if(xmlStreamReader.getLocalName().equals("name"))
							isName = true;
						if(xmlStreamReader.getLocalName().equals("value"))
							isValue = true;
						break;
					case XMLStreamConstants.CHARACTERS:
						content = xmlStreamReader.getText();
						info = "character event for ";
						if(isName) {
							name = content;
							isName = false;
						}
						if(isValue) {
							value = content;
							isValue = false;
						}
						break;
					case XMLStreamConstants.END_ELEMENT:
						currentTagName = xmlStreamReader.getLocalName();
						info = "end event for ";
						if(xmlStreamReader.getLocalName().equals("capability")) {
							HashMap<String,Object> hashMap = (HashMap<String,Object>)flavorMap.get(flavor).get("flavorDefaultParameters");
							((HashMap<String,Object>) hashMap.get("capability")).put(name, value);
							name = "";
							value = "";
						}
						if(xmlStreamReader.getLocalName().equals("property")) {
							HashMap<String,Object> hashMap = (HashMap<String,Object>)flavorMap.get(flavor).get("flavorDefaultParameters");
							((HashMap<String,Object>) hashMap.get("property")).put(name, value);
							name = "";
							value = "";
						}
						if(xmlStreamReader.getLocalName().equals("flavorDefaultParameters")) {
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
		return flavorMap;
	}
	
	private Map<String,Map<String,Object>> captureFrameworkDevices(XMLStreamReader xmlStreamReader,Map<String,Map<String,Object>> deviceMap) {
		DesiredCapabilities capabilities = new DesiredCapabilities();
		String capName="", uniqueName="";
		boolean flag = true,
				isUniqueName = false,
				isUdid = false,
				isDesiredCap = false,
				isflavor = false;
		int event = xmlStreamReader.getEventType();
		String currentTagName = "", info = "", content = "";
		try {
			while(flag)	{
				content="";
				switch(event) {
					case XMLStreamConstants.START_ELEMENT:
						currentTagName = xmlStreamReader.getLocalName();
						info = "start event for ";
						if(xmlStreamReader.getLocalName().equals("uniqueName"))
							isUniqueName = true;
						if(xmlStreamReader.getLocalName().equals("udid"))
							isUdid = true;
						if(xmlStreamReader.getLocalName().equals("deviceCapabilities"))
							isDesiredCap = true;
						if(isDesiredCap)
							capName = xmlStreamReader.getLocalName();
						if(xmlStreamReader.getLocalName().equals("flavor") && !isDesiredCap) {
							isflavor = true;
						}
						break;
					case XMLStreamConstants.CHARACTERS:
						info = "character event for ";
						content = xmlStreamReader.getText();
						if(isUniqueName) {
							uniqueName = xmlStreamReader.getText();
							deviceMap.put(uniqueName, new HashMap<String,Object>());
							isUniqueName=false;
						}
						if(isUdid) {
							capabilities.setCapability("udid",xmlStreamReader.getText());
							isUdid = false;
						}
						if(isDesiredCap && !xmlStreamReader.getText().contains("\n") && !xmlStreamReader.getText().contains("\t")){
							capabilities.setCapability(capName,xmlStreamReader.getText());
							capName = "";
						}
						if(isflavor) {
							//if(!xmlStreamReader.getText().contains("\n"))
							deviceMap.get(uniqueName).put("flavor",xmlStreamReader.getText());
							//System.out.println(xmlStreamReader.getText()+" "+xmlStreamReader.getText().contains("\n"));
							isflavor=false;
						}
						break;
					case XMLStreamConstants.END_ELEMENT:
						currentTagName = xmlStreamReader.getLocalName();
						info = "end event for ";
						if(xmlStreamReader.getLocalName().equals("device")) {
							flag = false;
						}
						if(xmlStreamReader.getLocalName().equals("deviceCapabilities") && isDesiredCap) {
							deviceMap.get(uniqueName).put("deviceCapabilities",capabilities);
							//devices.put(uniqueName, deviceMap);
							isDesiredCap=false;
							capabilities=null;
							//empList.add(emp);
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
		return deviceMap;
	}

	@SuppressWarnings("unchecked")
	private Map<String,Map<String,Object>> captureFlavorParentPageInfo(XMLStreamReader xmlStreamReader,Map<String,Map<String,Object>> flavorMap, String flavor) {
		flavorMap.get(flavor).put("flavorParentPage", new HashMap<String,Object>());
		int event = xmlStreamReader.getEventType();
		String currentTagName = "", info = "", content = "", locatorName = "", subLocatorName = "";
		boolean flag = true,
				isName = false,
				isSubLocator = false,
				isLocator = false;
		try {
			while(flag)	{
				content="";
				switch(event) {
					case XMLStreamConstants.START_ELEMENT:
						currentTagName = xmlStreamReader.getLocalName();
						info = "start event for ";
						if(currentTagName.equals("name") && !isLocator)
							isName = true;
						if(currentTagName.equals("locators")) {
							HashMap<String,Object> flavorParentPageMap = (HashMap<String,Object>)flavorMap.get(flavor).get("flavorParentPage");
							flavorParentPageMap.put("locators", new HashMap<String,Object>());
						}
						if(currentTagName.equals("locator")) {
							isLocator = true;
							int attrCount = xmlStreamReader.getAttributeCount();
							int itr = 0;
							while(itr<attrCount) {
								if(xmlStreamReader.getAttributeLocalName(itr).equals("name")) {
									HashMap<String,Object> flavorParentPageMap = (HashMap<String,Object>)flavorMap.get(flavor).get("flavorParentPage");
									HashMap<String,Object> flavorParentPageLocatorsMap = (HashMap<String,Object>) flavorParentPageMap.get("locators");
									locatorName = xmlStreamReader.getAttributeValue(itr);
									flavorParentPageLocatorsMap.put(locatorName, new LinkedHashMap<String,String>());
								}
								itr++;
							}
						}
						if(isLocator && !currentTagName.equals("locator")) {
							subLocatorName = currentTagName;
							isSubLocator = true;
						}
						break;
					case XMLStreamConstants.CHARACTERS:
						info = "character event for ";
						content = xmlStreamReader.getText();
						if(isName) {
							HashMap<String,Object> flavorParentPageMap = (HashMap<String,Object>) flavorMap.get(flavor).get("flavorParentPage");
							flavorParentPageMap.put("name", xmlStreamReader.getText());
							isName = false;
						}
						if(isSubLocator) {
							HashMap<String,Object> flavorParentPageMap = (HashMap<String,Object>)flavorMap.get(flavor).get("flavorParentPage");
							HashMap<String,Object> flavorParentPageLocatorsMap = (HashMap<String,Object>) flavorParentPageMap.get("locators");
							HashMap<String,Object> flavorParentPageLocatorMap = (HashMap<String,Object>) flavorParentPageLocatorsMap.get(locatorName);
							flavorParentPageLocatorMap.put(subLocatorName, xmlStreamReader.getText());
							isSubLocator = false;
						}
						break;
					case XMLStreamConstants.END_ELEMENT:
						currentTagName = xmlStreamReader.getLocalName();
						info = "end event for ";
						if(currentTagName.equals("locator")) {
							locatorName = "";
							subLocatorName = "";
							isLocator = false;
						}
						if(xmlStreamReader.getLocalName().equals("flavorParentPage")) {
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
		return flavorMap;
	}

	@SuppressWarnings("unchecked")
	private Map<String,Map<String,Object>> captureFlavorPageInfo(XMLStreamReader xmlStreamReader,Map<String,Map<String,Object>> flavorMap, String flavor) {
		int event = xmlStreamReader.getEventType();
		String currentTagName = "", info = "", content = "", pageName = "", locatorName = "", subLocatorName = "";
		boolean flag = true,
				isPageName = false,
				isSubLocator = false,
				isLocator = false;
		try {
			while(flag)	{
				content="";
				switch(event) {
					case XMLStreamConstants.START_ELEMENT:
						currentTagName = xmlStreamReader.getLocalName();
						info = "start event for ";
						if(currentTagName.equals("name") && !isLocator)
							isPageName = true;
						if(currentTagName.equals("locator")) {
							isLocator = true;
							int attrCount = xmlStreamReader.getAttributeCount();
							int itr = 0;
							while(itr<attrCount) {
								if(xmlStreamReader.getAttributeLocalName(itr).equals("name")) {
									HashMap<String,Object> flavorPagesMap = (HashMap<String,Object>)flavorMap.get(flavor).get("flavorPages");
									HashMap<String,Object> flavorParentPageLocatorsMap = (HashMap<String,Object>) flavorPagesMap.get(pageName);
									locatorName = xmlStreamReader.getAttributeValue(itr);
									flavorParentPageLocatorsMap.put(locatorName, new LinkedHashMap<String,String>());
								}
								itr++;
							}
						}
						if(isLocator && !currentTagName.equals("locator")) {
							subLocatorName = currentTagName;
							isSubLocator = true;
						}
						break;
					case XMLStreamConstants.CHARACTERS:
						info = "character event for ";
						content = xmlStreamReader.getText();
						if(isPageName) {
							HashMap<String,Object> flavorPagesMap = (HashMap<String,Object>) flavorMap.get(flavor).get("flavorPages");
							pageName = content;
							flavorPagesMap.put(pageName, new HashMap<String,Object>());
							isPageName = false;
						}
						if(isSubLocator) {
							HashMap<String,Object> flavorParentPageMap = (HashMap<String,Object>)flavorMap.get(flavor).get("flavorPages");
							HashMap<String,Object> flavorParentPageLocatorsMap = (HashMap<String,Object>) flavorParentPageMap.get(pageName);
							HashMap<String,Object> flavorParentPageLocatorMap = (HashMap<String,Object>) flavorParentPageLocatorsMap.get(locatorName);
							flavorParentPageLocatorMap.put(subLocatorName, xmlStreamReader.getText());
							isSubLocator = false;
						}
						break;
					case XMLStreamConstants.END_ELEMENT:
						currentTagName = xmlStreamReader.getLocalName();
						info = "end event for ";
						if(xmlStreamReader.getLocalName().equals("page")) {
							flag = false;
						}
						if(xmlStreamReader.getLocalName().equals("locator")) {
							locatorName = "";
							subLocatorName = "";
							isLocator = false;
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
		return flavorMap;
	}
	
	@SuppressWarnings("unchecked")
	private Map<String,Map<String,Object>> captureTestCaseInfo(XMLStreamReader xmlStreamReader,Map<String,Map<String,Object>> flavorMap, String flavor) {
		int event = xmlStreamReader.getEventType();
		String currentTagName = "", info = "", content = "", testCaseName = "", testDataId = "", testDataIdparameterName = "";
		boolean flag = true,
				isName = false,
				isTestData = false,
				isTestDataIdparameter = false;
		try {
			while(flag)	{
				content="";
				switch(event) {
					case XMLStreamConstants.START_ELEMENT:
						currentTagName = xmlStreamReader.getLocalName();
						info = "start event for ";
						if(currentTagName.equals("name") && !isTestData)
							isName = true;
						if(currentTagName.equals("testData")) {
							isTestData = true;
							int attrCount = xmlStreamReader.getAttributeCount();
							int itr = 0;
							while(itr<attrCount) {
								if(xmlStreamReader.getAttributeLocalName(itr).equals("id")) {
									HashMap<String,Object> flavorCasesMap = (HashMap<String,Object>)flavorMap.get(flavor).get("flavorTestCases");
									HashMap<String,Object> flavorCaseMap = (HashMap<String,Object>) flavorCasesMap.get(testCaseName);
									testDataId = xmlStreamReader.getAttributeValue(itr);
									flavorCaseMap.put(testDataId, new HashMap<String,Object>());
								}
								itr++;
							}
						}
						if(isTestData && !currentTagName.equals("testData")) {
							isTestDataIdparameter = true;
							testDataIdparameterName = currentTagName;
						}
						break;
					case XMLStreamConstants.CHARACTERS:
						info = "character event for ";
						content = xmlStreamReader.getText();
						if(isName) {
							HashMap<String,Object> flavorCasesMap = (HashMap<String,Object>) flavorMap.get(flavor).get("flavorTestCases");
							testCaseName = content;
							flavorCasesMap.put(testCaseName, new HashMap<String,Object>());
							isName = false;
						}
						if(isTestDataIdparameter) {
							HashMap<String,Object> flavorCasesMap = (HashMap<String,Object>) flavorMap.get(flavor).get("flavorTestCases");
							HashMap<String,Object> flavorCaseMap = (HashMap<String,Object>) flavorCasesMap.get(testCaseName);
							HashMap<String,Object> flavorCaseDataIdMap = (HashMap<String,Object>) flavorCaseMap.get(testDataId);
							flavorCaseDataIdMap.put(testDataIdparameterName, content);
							isTestDataIdparameter = false;
						}
						break;
					case XMLStreamConstants.END_ELEMENT:
						currentTagName = xmlStreamReader.getLocalName();
						info = "end event for ";
						if(xmlStreamReader.getLocalName().equals("testCase")) {
							flag = false;
						}
						if(xmlStreamReader.getLocalName().equals("testData")) {
							isTestData = false;
							testDataId = "";
							testDataIdparameterName = "";
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
		return flavorMap;
	}
	
	@SuppressWarnings("unchecked")
	private Map<String,Map<String,Object>> captureApiEnvInfo(XMLStreamReader xmlStreamReader,Map<String,Map<String,Object>> flavorMap, String flavor) {
		int event = xmlStreamReader.getEventType();
		String currentTagName = "", info = "", content = "", envName = "", paramName = "";
		boolean flag = true,
				isName = false;
		try {
			while(flag)	{
				content="";
				switch(event) {
					case XMLStreamConstants.START_ELEMENT:
						currentTagName = xmlStreamReader.getLocalName();
						info = "start event for ";
						if(currentTagName.equals("name"))
							isName = true;
						if(!isName)
							paramName = currentTagName;
						break;
					case XMLStreamConstants.CHARACTERS:
						info = "character event for ";
						content = xmlStreamReader.getText();
						if(isName) {
							HashMap<String,Object> flavorApiEnvMap = (HashMap<String,Object>) flavorMap.get(flavor).get("flavorApiEnvironments");
							envName = content;
							flavorApiEnvMap.put(envName, new HashMap<String,Object>());
						}
						if(!isName && !xmlStreamReader.getText().contains("\n") && !xmlStreamReader.getText().contains("\t")) {
							HashMap<String,Object> flavorApiEnvsMap = (HashMap<String,Object>) flavorMap.get(flavor).get("flavorApiEnvironments");
							HashMap<String,Object> flavorApiEnvMap = (HashMap<String,Object>) flavorApiEnvsMap.get(envName);
							flavorApiEnvMap.put(paramName, content);
						}
						break;
					case XMLStreamConstants.END_ELEMENT:
						currentTagName = xmlStreamReader.getLocalName();
						info = "end event for ";
						if(currentTagName.equals("name"))
							isName = false;
						if(xmlStreamReader.getLocalName().equals("environment")) {
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
		return flavorMap;
	}
	
	@SuppressWarnings("unchecked")
	private Map<String,Map<String,Object>> captureflavorGlobalParam(XMLStreamReader xmlStreamReader,Map<String,Map<String,Object>> flavorMap, String flavor) {
		int event = xmlStreamReader.getEventType();
		String currentTagName = "", info = "", content = "", paramKey = "", paramContainer = "";
		boolean flag = true,
				isParam = false,
				isParamKey = false,
				isParamValue = false,
				isParamContainer = false;
		HashMap<String,Object> flavorGlobalParamMap = (HashMap<String,Object>) flavorMap.get(flavor).get("flavorGlobalParam");
		try {
			while(flag)	{
				content="";
				switch(event) {
					case XMLStreamConstants.START_ELEMENT:
						currentTagName = xmlStreamReader.getLocalName();
						info = "start event for ";
						if(!currentTagName.equals("param") && !isParam) {
							isParamContainer = true;
							paramContainer = currentTagName;
							flavorGlobalParamMap.put(paramContainer, new HashMap<String,Object>());
						}
						if(currentTagName.equals("param"))
							isParam = true;
						if(isParam && currentTagName.equals("key"))
							isParamKey = true;
						if(isParam && currentTagName.equals("value"))
							isParamValue = true;
						break;
					case XMLStreamConstants.CHARACTERS:
						info = "character event for ";
						content = xmlStreamReader.getText();
						if(isParamKey) {
							paramKey = content;
							isParamKey = false;
						}
						if(isParamValue) {
							if(isParamContainer) {
								HashMap<String,Object> flavorParamContainerMap = (HashMap<String,Object>) flavorGlobalParamMap.get(paramContainer);
								flavorParamContainerMap.put(paramKey, content);
							}
							else
								flavorGlobalParamMap.put(paramKey, content);
							isParamValue = false;
						}
						break;
					case XMLStreamConstants.END_ELEMENT:
						currentTagName = xmlStreamReader.getLocalName();
						info = "end event for ";
						if(currentTagName.equals("param")) {
							paramKey = "";
							isParam = false;
						}
						if(currentTagName.equals(paramContainer))
							isParamContainer = false;
						if(xmlStreamReader.getLocalName().equals("flavorGlobalParam")) {
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
		return flavorMap;
	}
	
	@SuppressWarnings("unchecked")
	private Map<String,Map<String,Object>> captureflavorApiInfo(XMLStreamReader xmlStreamReader,Map<String,Map<String,Object>> flavorMap, String flavor) {
		int event = xmlStreamReader.getEventType();
		String currentTagName = "", info = "", content = "", apiName = "", apiProperty = "", paramKey = "";
		boolean flag = true,
				isName = false,
				isParam = false,
				isParams = false,
				isParamKey = false,
				isParamValue = false,
				isApiProperty = false;
		HashMap<String,Object> flavorApisMap = (HashMap<String,Object>) flavorMap.get(flavor).get("flavorApis");
		try {
			while(flag)	{
				content="";
				switch(event) {
					case XMLStreamConstants.START_ELEMENT:
						currentTagName = xmlStreamReader.getLocalName();
						info = "start event for ";
						if(currentTagName.equals("params")) {
							isParams = true;
							HashMap<String,Object> flavorApiMap = (HashMap<String,Object>) flavorApisMap.get(apiName);
							flavorApiMap.put("params", new HashMap<String,Object>());
						}
						if(currentTagName.equals("name") && !isParams)
							isName = true;
						if(isParams && currentTagName.equals("param"))
							isParam = true;
						if(isParams && isParam && currentTagName.equals("key"))
							isParamKey = true;
						if(isParams && isParam && currentTagName.equals("value"))
							isParamValue = true;
						if(!isParams && !isName) {
							isApiProperty = true;
							apiProperty = currentTagName;
						}
						break;
					case XMLStreamConstants.CHARACTERS:
						info = "character event for ";
						content = xmlStreamReader.getText();
						if(isName) {
							apiName = content;
							flavorApisMap.put(apiName, new HashMap<String,Object>());
							isName = false;
						}
						if(isParamKey) {
							paramKey = content;
							isParamKey = false;
						}
						if(isParamValue) {
							HashMap<String,Object> flavorApiMap = (HashMap<String,Object>) flavorApisMap.get(apiName);
							HashMap<String,Object> flavorApiParamsMap = (HashMap<String,Object>) flavorApiMap.get("params");
							flavorApiParamsMap.put(paramKey, content);
							paramKey = "";
							isParamValue = false;
						}
						if(isApiProperty) {
							HashMap<String,Object> flavorApiMap = (HashMap<String,Object>) flavorApisMap.get(apiName);
							flavorApiMap.put(apiProperty, content);
						}
						break;
					case XMLStreamConstants.END_ELEMENT:
						currentTagName = xmlStreamReader.getLocalName();
						info = "end event for ";
						if(currentTagName.equals("param"))
							isParam = false;
						if(currentTagName.equals("params"))
							isParams = false;
						if(currentTagName.equals("api"))
							flag = false;
						if(currentTagName.equals(apiProperty))
							isApiProperty = false;
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
		return flavorMap;
	}

}