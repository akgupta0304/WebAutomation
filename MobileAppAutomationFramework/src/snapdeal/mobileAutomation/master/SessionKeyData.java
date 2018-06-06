package snapdeal.mobileAutomation.master;

import java.util.HashMap;
import java.util.Map;

public class SessionKeyData {

	private Object value;
	
	private Map<String,Object> metaData;
	
	public SessionKeyData() {
		value = null;
		metaData = new HashMap<String,Object>();
	}

	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
	}
	
	public Object getMetaData(String metaDataKey) {
		return metaData.get(metaDataKey);
	}
	
	public void setMetData(Map<String,Object> metaData) {
		this.metaData = metaData;
	}
	
	public Object putMetaData(String metaDataKey, Object metaDataValue) {
		return metaData.put(metaDataKey, metaDataValue);
	}
}
