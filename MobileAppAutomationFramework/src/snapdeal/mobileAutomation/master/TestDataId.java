package snapdeal.mobileAutomation.master;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This class is used to map a test case script method to a test data set specified in its properties file by the means of an id
 * @author Khagesh Kapil
 * @see TestCase
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface TestDataId {
	
	/**
	 * Specifies the value of id for selecting test data set
	 * @return
	 */
	String value() default "";
}
