package snapdeal.mobileAutomation.master;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import org.apache.http.Header;
import org.apache.http.HttpStatus;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.json.JSONObject;

import testCaseReporting.TestCaseReporting;

/**
 * This Class acts as an Generic API Page class (Abstration for API classes)
 * @author Khagesh Kapil
 * @see Page
 */
public class ServiceApi {
	
	private JSONObject jsonObject;
	
	private TestCaseReporting testCaseReporting;
	
	private Map<String,Object> requestParameters = new HashMap<String,Object>();
	
	private Map<String,String> requestHeaders = new HashMap<String,String>();
	
	private Properties globalRequestParameters = new Properties();
	
	private Properties relevantRequestParameters = new Properties();

	private String globalRequestParametersPath;
	
	private String relevantRequestParametersPath;
	
	private String requestType;
	
	private String url;
	
	public void setTestCaseReporting(TestCaseReporting testCaseReporting) {
		this.testCaseReporting = testCaseReporting;
	}
	
	public void addRequestHeader(String key, String value){
		requestHeaders.put(key, value);
		}
	
	public String getRequestHeader(String key){
		if(this.requestHeaders.containsKey(key))
			return this.requestHeaders.get(key);
		else
			return null;
	}
	
	public ServiceApi(){
		
	}
	
	public ServiceApi(String relevantRequestParametersPath) throws FileNotFoundException, IOException {
		this.relevantRequestParametersPath = relevantRequestParametersPath;
		if(FlavourDictionary.binariesPath.equals("")) {
			Properties p = new Properties();
			p.load(new FileReader(FlavourDictionary.flavourMapPath));
			FlavourDictionary.binariesPath = p.getProperty("binariesPath");
		}
		relevantRequestParameters.load(new FileInputStream(FlavourDictionary.binariesPath+relevantRequestParametersPath));
		Enumeration<?> parameters = relevantRequestParameters.propertyNames();
		while(parameters.hasMoreElements()) {
			String parameter = (String) parameters.nextElement();
			if(relevantRequestParameters.getProperty(parameter).equals("<NULL>"))
				relevantRequestParameters.setProperty(parameter, "");
		}
		globalRequestParametersPath = UpdateMasterConfig.masterConfiguration.get("mobAPI");
		globalRequestParameters.load(new FileInputStream(new File(globalRequestParametersPath)));
		requestType = relevantRequestParameters.getProperty("API_REQUEST_TYPE");
		url = relevantRequestParameters.getProperty("API_URL");
		prepareRequestParameters();
		prepareRequestHeaders();
	}
	
	public static String getAPIFromURL(String url){
		String[] parts = url.split("\\?");
		String[] partsBeforeQuestionMark = parts[0].split("/");
		return partsBeforeQuestionMark[partsBeforeQuestionMark.length-1];
	}
	
	public static int getURLStatusCode(String pageurl){
		
		int response_code = -1;
		int attempt = 3;
		
		while(attempt != 0){
		
		try {
			URL url = new URL(pageurl);
			HttpURLConnection openConnection = (HttpURLConnection)url.openConnection();
			if (openConnection != null)
				openConnection.addRequestProperty("User-Agent",
						"Mozilla/5.0 (Windows NT 6.1; WOW64; rv:25.0) Gecko/20100101 Firefox/25.0");

			response_code = openConnection.getResponseCode();

			while (response_code == HttpStatus.SC_MOVED_TEMPORARILY || response_code == HttpStatus.SC_MOVED_PERMANENTLY || response_code==HttpStatus.SC_TEMPORARY_REDIRECT) {
				String newUrl = openConnection.getHeaderField("Location");
				String[] parts = newUrl.split("\\?");
				if(parts.length == 2)
					newUrl = parts[0] + "?" + URLEncoder.encode(parts[1], "UTF-8");
				url = new URL(newUrl);
				
				openConnection= (HttpURLConnection) url.openConnection();
				if (openConnection != null)
					openConnection.addRequestProperty("User-Agent",
							"Mozilla/5.0 (Windows NT 6.1; WOW64; rv:25.0) Gecko/20100101 Firefox/25.0");
				response_code = openConnection.getResponseCode();
			}
			
			return response_code;

		} catch (IOException e) {
			e.printStackTrace();
			attempt--;
		}
		
	}
		return response_code;

	}
	
	public Properties getRelevantRequestParameters() {
		return relevantRequestParameters;
	}

	private void prepareRequestParameters() {
		Iterator<?> itr = relevantRequestParameters.entrySet().iterator();
		while(itr.hasNext()) {
			Entry<?,?> e = (Entry<?, ?>) itr.next();
			if(((String) e.getKey()).startsWith("PARAM")) {
				requestParameters.put((String) e.getValue(),"");
			}
			requestParameters.put(globalRequestParameters.getProperty("KEY_REQUEST_PROTOCOL"),globalRequestParameters.getProperty("VALUE_REQUEST_PROTOCOL"));
			requestParameters.put(globalRequestParameters.getProperty("KEY_RESPONSE_PROTOCOL"),globalRequestParameters.getProperty("VALUE_RESPONSE_PROTOCOL"));
			requestParameters.put(globalRequestParameters.getProperty("KEY_API_KEY"),globalRequestParameters.getProperty("VALUE_SNAPDEAL_API_KEY"));
		}
	}
	
	private void prepareRequestHeaders() {
		requestHeaders.put(globalRequestParameters.getProperty("KEY_HEADER_TYPE"), globalRequestParameters.getProperty("VALUE_HEADER_TYPE"));
	}

	public String getRequestType() {
		return requestType;
	}
	
	public String getRequestURL() {
		return url;
	}
	
	public void setApiEnvironment(String environment) {
		url = environment + getRequestURL();
	}
	
	public String getGlobalRequestParametersPath() {
		return globalRequestParametersPath;
	}

	public String getRelevantRequestParametersPath() {
		return relevantRequestParametersPath;
	}
	
	public boolean hit() { 
		boolean status = false;
		try {
			jsonObject = ApiResponse.requestJson(url, requestParameters, requestType, requestHeaders);
			status = true;
			
			String apiDetails = "Url = "+url;
			apiDetails += "\nRequest Parameters = "+ requestParameters +"\nRequest Type = "+requestType+"\n Request Headers = "+requestHeaders; 
			ObjectMapper objectMapper = new ObjectMapper();
			byte[] jsonData = jsonObject.toString().getBytes("UTF-8");
			Object s1 = objectMapper.readValue(jsonData, Object.class);
			String response = objectMapper.defaultPrettyPrintingWriter().writeValueAsString(s1);
			System.out.println(apiDetails+"\n"+response);
			testCaseReporting.testStepApiReporting(apiDetails, response);
		}
		catch(Exception e) {
			status = false;
			String apiDetails = "Url = "+url;
			apiDetails += "\nRequest Parameters = "+ requestParameters +"\nRequest Type = "+requestType+"\n Request Headers = "+requestHeaders;
			testCaseReporting.testStepApiReporting(apiDetails, "false");
			e.printStackTrace();
		}
		return status;
	}
	
	/**
	 * This method will use the for TDM API in the case of text
	 * @author ankit.gupta04
	 * @return
	 */
	public String hitTDM(){
		
		String response = "";
		try {
			response = ApiResponse.requestPlaneText(url, requestParameters, requestType, requestHeaders);
			
			String apiDetails = "Url = "+url;
			apiDetails += "\nRequest Parameters = "+ requestParameters +"\nRequest Type = "+requestType+"\n Request Headers = "+requestHeaders; 
//			ObjectMapper objectMapper = new ObjectMapper();
//			byte[] jsonData = jsonObject.toString().getBytes();
//			Object s1 = objectMapper.readValue(jsonData, Object.class);
//			String response = objectMapper.defaultPrettyPrintingWriter().writeValueAsString(s1);
			System.out.println(apiDetails+"\n"+response);
			testCaseReporting.testStepApiReporting(apiDetails, response);
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		return response;
	}
	
	
	
	public boolean hitThroughURL(String url){
		boolean status = false;
		try {
			jsonObject = ApiResponse.requestJson(url, requestParameters, "get", requestHeaders);
			if (jsonObject != null) {
				status = true;
				String apiDetails = "Url = " + url;
				apiDetails += "\nRequest Parameters = " + requestParameters + "\nRequest Type = " + requestType
						+ "\n Request Headers = " + requestHeaders;
				ObjectMapper objectMapper = new ObjectMapper();
				byte[] jsonData = jsonObject.toString().getBytes("UTF-8");
				Object s1 = objectMapper.readValue(jsonData, Object.class);
				String response = objectMapper.defaultPrettyPrintingWriter().writeValueAsString(s1);
				System.out.println(apiDetails + "\n" + response);
				testCaseReporting.testStepApiReporting(apiDetails, response);
			}
		} catch (Exception e) {
			status = false;
			e.printStackTrace();
		}
		return status;
	}
	
	
	/**
	 * @author ankit.gupta04
	 * This method will call the API using passed URL
	 * @param url
	 * @return
	 */
	public boolean hitThroughURLGeneric(String url){
		boolean status = false;
		try{
			jsonObject = ApiResponse.requestJson(url, requestParameters, requestType, requestHeaders);
			if (jsonObject != null) {
				status = true;
				String apiDetails = "Url = "+url;
				apiDetails += "\nRequest Parameters = "+ requestParameters +"\nRequest Type = "+requestType+"\n Request Headers = "+requestHeaders; 
				ObjectMapper objectMapper = new ObjectMapper();
				byte[] jsonData = jsonObject.toString().getBytes("UTF-8");
				Object s1 = objectMapper.readValue(jsonData, Object.class);
				String response = objectMapper.defaultPrettyPrintingWriter().writeValueAsString(s1);
				System.out.println(apiDetails+"\n"+response);
				testCaseReporting.testStepApiReporting(apiDetails, response);
			}
		}
		catch(Exception e){
			e.printStackTrace();
		}
		return status;
	}
	public boolean hitForSessionEntry(Session session) {
		return hit();
	}
	
	public String getResponseParameter(String parameterName) throws JsonProcessingException, IOException {
		String value = "";
		String parameterXPath = relevantRequestParameters.getProperty("ResponseKey_"+parameterName);
		String[] xpathOrder = parameterXPath.split("/");
		ObjectMapper objectMapper = new ObjectMapper();
		byte[] jsonData = jsonObject.toString().getBytes();
		JsonNode rootNode = objectMapper.readTree(jsonData);
		JsonNode node = null;
		for(int i=1;i<xpathOrder.length;i++) {
			if(node==null)
				node = rootNode;
			node = node.path(xpathOrder[i]);
		}
		value = node.asText();
		return value;
	}
	
	public String getResponseParameterThroughUrl(String url) throws JsonProcessingException, IOException {
		String value = "";
		String[] xpathOrder = url.split("/");
		ObjectMapper objectMapper = new ObjectMapper();
		byte[] jsonData = jsonObject.toString().getBytes();
		JsonNode rootNode = objectMapper.readTree(jsonData);
		JsonNode node = null;
		for(int i=1;i<xpathOrder.length;i++) {
			if(node==null)
				node = rootNode;
			if(xpathOrder[i].contains("[")){
				xpathOrder[i] = xpathOrder[i].replace("[", "");
				xpathOrder[i] = xpathOrder[i].replace("]", "");
				node = node.get(Integer.parseInt(xpathOrder[i]));
			}
			else
				node = node.path(xpathOrder[i]);
		}
		value = node.asText();
		return value;
	}

	public int getArraySize(String parameterName) throws JsonProcessingException, IOException {
		int value = 0;
		String parameterXPath = relevantRequestParameters.getProperty("ResponseKey_"+parameterName);
		String[] xpathOrder = parameterXPath.split("/");
		ObjectMapper objectMapper = new ObjectMapper();
		byte[] jsonData = jsonObject.toString().getBytes();
		JsonNode rootNode = objectMapper.readTree(jsonData);
		JsonNode node = null;
		for(int i=1;i<xpathOrder.length;i++) {
			if(node==null)
				node = rootNode;
			node = node.path(xpathOrder[i]);
		}
		value = node.size();
		return value;
	}
	
	public int getArraySizeViaUrl(String parameterName) throws JsonProcessingException, IOException {
		int value = 0;
		//String parameterXPath = relevantRequestParameters.getProperty("ResponseKey_"+parameterName);
		String[] xpathOrder = parameterName.split("/");
		ObjectMapper objectMapper = new ObjectMapper();
		byte[] jsonData = jsonObject.toString().getBytes();
		JsonNode rootNode = objectMapper.readTree(jsonData);
		JsonNode node = null;
		for(int i=1;i<xpathOrder.length;i++) {
			if(node==null)
				node = rootNode;
			if(xpathOrder[i].contains("[")){
				xpathOrder[i] = xpathOrder[i].replace("[", "");
				xpathOrder[i] = xpathOrder[i].replace("]", "");
				node = node.get(Integer.parseInt(xpathOrder[i]));
			}
			else
				node = node.path(xpathOrder[i]);
		}
		value = node.size();
		return value;
	}
	
	public JsonNode getResponseNodeThroughUrl(String url) throws JsonProcessingException, IOException {
		String[] xpathOrder = url.split("/");
		ObjectMapper objectMapper = new ObjectMapper();
		byte[] jsonData = jsonObject.toString().getBytes();
		JsonNode rootNode = objectMapper.readTree(jsonData);
		JsonNode node = null;
		for(int i=1;i<xpathOrder.length;i++) {
		if(node==null)
		node = rootNode;
		if(xpathOrder[i].contains("[")){
		xpathOrder[i] = xpathOrder[i].replace("[", "");
		xpathOrder[i] = xpathOrder[i].replace("]", "");
		node = node.get(Integer.parseInt(xpathOrder[i]));
		}
		else
		node = node.path(xpathOrder[i]);
		}
		return node;
		}

	public Map<String, Object> getRequestParameters() {
		return requestParameters;
	}

	protected void setRequestParameters(Map<String, Object> requestParameters) {
		this.requestParameters = requestParameters;
	}
	
	protected void removeRequestParameter(String key) {
		if(this.requestParameters.containsKey(key))
			this.requestParameters.remove(key);
	}
	
	protected void removeRequestHeader(String key) {
		if(this.requestHeaders.containsKey(key))
			this.requestHeaders.remove(key);
	}
	
	public boolean isThisPdpUrl(String url){
		boolean isPdpUrl = false;
		String[] urlEntries = url.split("/");
		if(urlEntries[0].equals("product")||urlEntries[3].equals("product"))
			isPdpUrl = true;
		return isPdpUrl;
	}
	
	public String getProductId(String url){
		String[] urlEntries = url.split("/");
		return urlEntries[(urlEntries.length-1)];
	}
	
	public Object serviceExecutor(String method, Object...params) throws Exception {
		Object o = null;
		setter:
		try {
			Iterator<?> itr = relevantRequestParameters.entrySet().iterator();
			while(itr.hasNext()) {
				Entry<?,?> e = (Entry<?, ?>) itr.next();
				if(((String) e.getKey()).startsWith("PARAM_KEY") && method.equalsIgnoreCase("set"+((String) e.getValue()))) {
					requestParameters.put((String) e.getValue(), params[0]);
					break setter;
				}
				else if(((String) e.getKey()).startsWith("PARAM_KEY") && method.equalsIgnoreCase("get"+((String) e.getValue()))) {
					return requestParameters.get((String) e.getValue());
				}
			}
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
			throw new Exception("illegal functionality call");
		}
		return o;
	}
	


	protected String getHeadervalue(String headerName) {
		String headerValue = "";
		boolean flag = false;
		for (Header header : ApiResponse.responseHeader) {
			if (header.getName().equals(headerName)) {
				headerValue = header.getValue();
				flag = true;
				break;
			}
		}
		if (flag) {
			return headerValue;
		} else
			return null;
	}


}