package snapdeal.mobileAutomation.master;

import java.util.HashMap;
import java.util.Map;

public class Session {

	private Map<String,SessionKeyData> dataPool;
	
	protected Session() {
		dataPool = new HashMap<String,SessionKeyData>();
	}
	
	public SessionKeyData putData(String key, Object value) {
		SessionKeyData skd = new SessionKeyData();
		skd.setValue(value);
		return dataPool.put(key, skd);
	}
	
	public SessionKeyData putData(String key, Object value, Map<String,Object> metaData) {
		SessionKeyData skd = new SessionKeyData();
		skd.setValue(value);
		skd.setMetData(metaData);
		return dataPool.put(key, skd);
	}
	
	public SessionKeyData getData(String key) {
		return dataPool.get(key);
	}
	
	public Object getMetaData(String key, String metaDataKey) {
		return dataPool.get(key).getMetaData(metaDataKey);
	}
	
	public Object putMetaData(String key, Object value) {
		return null;
	}
}
