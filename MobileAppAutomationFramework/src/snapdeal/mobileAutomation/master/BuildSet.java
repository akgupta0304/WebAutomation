package snapdeal.mobileAutomation.master;

import java.util.ArrayList;
import java.util.List;

public class BuildSet {
	private String buildSetFlavor;
	
	private List<Build> buildSet;
	
	BuildSet() {
		buildSet = new ArrayList<Build>();
	}
	
	public void setBuildSetFlavor(String buildSetFlavor) {
		this.buildSetFlavor = buildSetFlavor;
	}

	public String getBuildSetFlavor() {
		return buildSetFlavor;
	}
	
	public void setBuildSet(List<Build> buildSet) {
		this.buildSet = buildSet;
	}

	public List<Build> getBuildSet() {
		return buildSet;
	}
	
	@Override
	public String toString() {
		String str = "Build Set Flavor : "+getBuildSetFlavor()+"\n";
		for(Build build : buildSet) {
			str += build+"\n";
		}
		return str;
	}
}
