package snapdeal.mobileAutomation.interfaces;

import snapdeal.mobileAutomation.master.TestCase;

public interface LoadTimeStartPoint {
	
	public Boolean load(TestCase testCase);
	
	public LoadTimeStartPoint generateVariant(String variantType);
}
