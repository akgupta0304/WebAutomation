package snapdeal.mobileAutomation.master;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation used to specify version support for a test case script 
 * @author Khagesh Kapil
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface VersionControlledFor {
	
	/**
	 * specifies an array of versions supported
	 * @return
	 */
	String[] value();
}
