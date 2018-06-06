package snapdeal.mobileAutomation.master;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.annotation.ElementType;

/**
 * This annotation is used to specify the test cases to be executed in a suite
 * @author Khagesh Kapil
 * @see Script
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface TestCases {
	
	/**
	 * Specifies a group of {@link Script} to be executed as part of a test suite
	 * @return
	 */
	Script[] value();
}
