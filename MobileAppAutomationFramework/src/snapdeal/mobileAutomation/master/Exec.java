package snapdeal.mobileAutomation.master;

public class Exec {
	public static void main(String[] args) {
//		FrameworkConfig.FRAMEWORK_CONFIG.setFrameworkConfigPath("/Users/khagesh.kapil/Documents/config - Copy.xml");
//		FrameworkConfig.FRAMEWORK_CONFIG.processFrameworkConfig();
//		FrameworkConfig.FRAMEWORK_CONFIG.show();
//		System.out.println("------------------");
//		System.out.println(FrameworkConfig.FRAMEWORK_CONFIG.getFlavorMap());
//		System.out.println("------------------");
//		System.out.println(FrameworkConfig.FRAMEWORK_CONFIG.getDevices());
		SuiteConfig sc = new SuiteConfig("/Users/khagesh.kapil/Documents/suiteConfig.xml");
		System.out.println(sc.getDevices());
		System.out.println(sc.getDefaultAppiumConfig()	);
		System.out.println(sc.getCases());
	}
}