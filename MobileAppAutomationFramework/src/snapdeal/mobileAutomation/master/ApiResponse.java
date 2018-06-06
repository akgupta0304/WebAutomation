package snapdeal.mobileAutomation.master;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.util.Map;
import java.util.Set;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.apache.http.util.TextUtils;
import org.json.*;

public class ApiResponse {

	public static Header[] responseHeader = null;
	public static int statusCode = -1;
	
	public static JSONObject requestJson(String endPointUrl,
			Map<String, Object> requestParameters, String resuestType,
			Map<String, String> headers) {
		JSONObject responseObj = null;
		HttpResponse response = null;
		try {
			if ((!endPointUrl.startsWith("http")) || (headers == null)
					|| (resuestType.isEmpty())) {
				throw new IOException();
			}
			HttpClient client = HttpClientBuilder.create().build();
			if (resuestType.equals("get")) {
				HttpGet httpGet = getReqquest(endPointUrl, requestParameters, headers);
				response = client.execute(httpGet);
				responseHeader = response.getAllHeaders();
				statusCode = response.getStatusLine().getStatusCode();
			} else if (resuestType.equals("post")) {
				HttpPost httpPost = postRequest(headers, requestParameters, endPointUrl);
				response = client.execute(httpPost);
				responseHeader = response.getAllHeaders();
				statusCode = response.getStatusLine().getStatusCode();
			}
			String responseData = EntityUtils.toString(response.getEntity());
			try {
				responseObj = new JSONObject(responseData);
			} catch (Exception e) {
				System.out.println("Bad request " + response.getStatusLine()
				+ "Status Code");
				System.out.println("Headers: "+headers);
				System.out.println("endPointUrl is:"+ endPointUrl);
				System.out.println("requestParameters are: "+requestParameters);
			}
		} catch (ClientProtocolException e) {
			System.out.println("Bad Request");
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("Bad Request");
			e.printStackTrace();
		}
		return responseObj;
	}
	
	
	
	/**
	 * This method will return the text of response 
	 * @author ankit.gupta04
	 * @param endPointUrl
	 * @param requestParameters
	 * @param resuestType
	 * @param headers
	 * @return
	 */
	public static String requestPlaneText(String endPointUrl,
			Map<String, Object> requestParameters, String resuestType,
			Map<String, String> headers) {
		HttpResponse response = null;
		String responseData = "";
		try {
			if ((!endPointUrl.startsWith("http")) || (headers == null)
					|| (resuestType.isEmpty())) {
				throw new IOException();
			}
			HttpClient client = HttpClientBuilder.create().build();
			if (resuestType.equals("get")) {
				HttpGet httpGet = getReqquest(endPointUrl, requestParameters, headers);
				response = client.execute(httpGet);
				responseHeader = response.getAllHeaders();
			} else if (resuestType.equals("post")) {
				HttpPost httpPost = postRequestAdditional(headers, requestParameters, endPointUrl);
				response = client.execute(httpPost);
				responseHeader = response.getAllHeaders();
			}
			try{
			if (showContentType(response.getEntity()).equalsIgnoreCase("text/plain")){
				
				responseData = EntityUtils.toString(response.getEntity());
			}
			} catch (Exception e) {
				System.out.println("Bad request " + response.getStatusLine()+ "Status Code");
				System.out.println("Headers: "+headers);
				System.out.println("endPointUrl is:"+ endPointUrl);
				System.out.println("requestParameters are: "+requestParameters);
				System.out.println("response return type is: "+showContentType(response.getEntity()));

			}
		} catch (ClientProtocolException e) {
			System.out.println("Bad Request");
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("Bad Request");
			e.printStackTrace();
		}
		return responseData;
	}
	
	/**
	 * This method will return the response type of Data
	 * @param entity
	 * @return
	 */
	private static String showContentType(HttpEntity entity) {
        ContentType contentType = ContentType.getOrDefault(entity);
        String mimeType = contentType.getMimeType();
        System.out.println("\nMimeType = " + mimeType);
        return mimeType;
    }
	

	private static HttpPost postRequest(Map<String, String> headers,
			Map<String, Object> requestParameters, String url)
					throws UnsupportedEncodingException {

		JSONObject jsonRequest = (requestParameters == null) ? null : new JSONObject(
				requestParameters);
		HttpPost httpPost = new HttpPost(url);
		Set<String> keySet = headers.keySet();
		for (String key : keySet) {
			String value = headers.get(key);
			if (TextUtils.isEmpty(value)) {
				value = "";
			}
			httpPost.addHeader(key, value);
		}
		StringEntity se = new StringEntity(jsonRequest.toString());
		httpPost.setEntity(se);
		return httpPost;
	}

	/**
	 * Created for HTML post request for TDM
	 * @author ankit.gupta04
	 * @param headers
	 * @param requestParameters
	 * @param url
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	private static HttpPost postRequestAdditional(Map<String, String> headers,
				Map<String, Object> requestParameters, String url)
				throws UnsupportedEncodingException {
			
			JSONObject jsonRequest = (requestParameters == null) ? null : new JSONObject(
					requestParameters);
			HttpPost httpPost = new HttpPost(url);
			Set<String> keySet = headers.keySet();
			for (String key : keySet) {
				String value = headers.get(key);
				if (TextUtils.isEmpty(value)) {
					value = "";
				}
				httpPost.addHeader(key, value);
			}
			String jsonreq =jsonRequest.toString();  
			if(jsonRequest.toString().contains("[")){
				int first = jsonRequest.toString().indexOf("[");
				int last =  jsonRequest.toString().indexOf("]");
				StringBuffer str = new StringBuffer(jsonRequest.toString()).replace(first-1, first, "");
				str = str.replace(last, last+1, "");
				jsonreq = str.toString();
			}
			
	//		StringEntity se = new StringEntity(jsonRequest.toString());
			StringEntity se = new StringEntity(jsonreq);
			httpPost.setEntity(se);
			return httpPost;
		}


	private static HttpGet getReqquest(String url, Map<String, Object> requestParameters,
			Map<String, String> headers) {
		String uri = null;
		try {
			if (requestParameters != null)
				uri = ApiResponse.generateGetUrl(url, requestParameters);
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		HttpGet httpGet = new HttpGet(uri);
		Set<String> keySet = headers.keySet();
		for (String key : keySet) {
			String value = headers.get(key);
			if (TextUtils.isEmpty(value)) {
				value = "";
			}
			httpGet.addHeader(key, value);
		}
		return httpGet;
	}

	public static String generateGetUrl(String url, Map<String, Object> requestParameters)
			throws URISyntaxException {

		if (requestParameters != null) {
			URIBuilder uriBuilder = new URIBuilder(url);
			Set<String> keySet = requestParameters.keySet();
			for (String key : keySet) {
				String value = (String) requestParameters.get(key);
				if (TextUtils.isEmpty(value)) {
					value = "";
				}
				uriBuilder.addParameter(key, value);
			}
			return uriBuilder.build().toString();
		} else
			return url;

	}
}


