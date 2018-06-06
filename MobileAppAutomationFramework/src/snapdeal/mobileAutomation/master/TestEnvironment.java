package snapdeal.mobileAutomation.master;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This class specifies the environment on which a test suite should execute
 * @author Khagesh Kapil
 * @see TestSuite
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface TestEnvironment {
	
	/**
	 * Specifies an environment for a {@link TestSuite}
	 * @return
	 */
	public String value() default "";

}
