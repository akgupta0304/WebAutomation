package snapdeal.mobileAutomation.master;

import java.util.Map;

public class Build {
	private String buildType;
	
	private Map<String,String> buildCapabilities;
	
	private Map<String,String> buildProperties;
	
	public String getBuildType() {
		return buildType;
	}
	
	public Map<String, String> getBuildCapabilities() {
		return buildCapabilities;
	}
	
	public Map<String, String> getBuildProperties() {
		return buildProperties;
	}

	public void setBuildType(String buildType) {
		this.buildType = buildType;
	}

	public void setBuildCapabilities(Map<String, String> buildCapabilities) {
		this.buildCapabilities = buildCapabilities;
	}

	public void setBuildProperties(Map<String, String> buildProperties) {
		this.buildProperties = buildProperties;
	}
	
	@Override
	public String toString() {
		String retStr = "";
		retStr += "Build Type : "+buildType+"\n";
		retStr += "Capabilities :"+"\n";
		for(Map.Entry<String,String> entry : buildCapabilities.entrySet()) {
			retStr += entry.getKey()+" : "+entry.getValue()+"\n";
		}
		retStr += "Properties :"+"\n";
		for(Map.Entry<String,String> entry : buildProperties.entrySet()) {
			retStr += entry.getKey()+" : "+entry.getValue()+"\n";
		}
		return retStr;
	}
}
