package snapdeal.mobileAutomation.master;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.Map;

public class UpdateMasterConfig {
	
	public static Map<String,String> masterConfiguration = new HashMap<String,String>();
	
	/*public static void main(String[] args) throws FileNotFoundException, IOException, InterruptedException {
		Properties p = new Properties();
		updateProperties("iosApp","defaultWaitTime","20");
		p.load(new FileInputStream(new File("D:\\SnapdealMobileAutomation\\bin\\snapdeal\\mobileAutomation\\master\\masterConfig\\iosApp.properties")));
		System.out.println(p.get("defaultWaitTime"));
	}*/
	
	public static void updateProperties(String fileName,String property ,String value) {
		try {
			Class<?> thisClass = Class.forName("snapdeal.mobileAutomation.master.UpdateMasterConfig");
			String basePath = thisClass.getProtectionDomain().getCodeSource().getLocation().getPath()+"snapdeal/mobileAutomation/master/masterConfig/";
			File f = new File(basePath+fileName+".properties");
			FileWriter fileWritter = new FileWriter(f, true);
			BufferedWriter bufferWritter = new BufferedWriter(fileWritter);
			bufferWritter.append("\n"+property+"="+value);
			fileWritter.flush();
			bufferWritter.flush();
			fileWritter.close();
			bufferWritter.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void updatePath(String property ,String value) {
		masterConfiguration.put(property, value);
	}
}
