package snapdeal.mobileAutomation.master;

import java.util.ArrayList;
import java.util.List;

import snapdeal.mobileAutomation.exceptions.InvalidTestSuiteTokenException;

public class TestSuiteTokenQueue {

	public static int suiteSequence = -1;
	
	private static List<TestSuiteToken> queue = new ArrayList<TestSuiteToken>();
	
	public static boolean setQueue(String[] testSuites) throws InvalidTestSuiteTokenException {
		boolean queueSet = false;
		TestSuiteToken tst = null;
		try {
			int i=0;
			for(String suite:testSuites) {
				tst = new TestSuiteToken();
				tst.setTokenSequence(++i);
				tst.setTokenSuite(suite.split("::")[0]);
				tst.setTokenSuiteConfig(suite.split("::")[1]);
				queue.add(tst);
			}
			queueSet = true;
		}
		catch(Exception e) {
			throw new InvalidTestSuiteTokenException(tst);
		}
		return queueSet;
	}
	
	public static List<TestSuiteToken> getQueue() {
		return queue;
	}
}
