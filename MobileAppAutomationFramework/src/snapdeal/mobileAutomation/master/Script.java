package snapdeal.mobileAutomation.master;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation is used to set execution parameters for a particular test case script in the TestSuite class
 * @author Khagesh Kapil
 * @see TestCases
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Script {
	
	/**
	 * Specifies TestCase object name for this script 
	 * @return
	 */
	String testCaseName() default "";
	
	/**
	 * Specifies flavour for this script
	 * @return
	 */
	String flavour() default "";
	
	/**
	 * Specifies the selenium reset flag (to be evaluvated before script execution)
	 * for this script
	 * @return
	 */
	boolean driverResetFlag() default false;
	
	/**
	 * Specifies the environment for this script
	 * default value is "none"
	 * @return
	 */
	
	String lld() default "";
	
	String testDataId() default "";
	
	/**
	 * Specifies the default snapshot strategy for the testsuite
	 * "everytime" : for snapshots to be taken at every step of the test case
	 * "only on failure" : for snapshots to be taken only on steps which failed
	 * default value is "everytime"
	 * @return
	 * @see TestEnvironment
	 */
	SnapshotStrategy SnapshotStrategy() default @SnapshotStrategy("");
}