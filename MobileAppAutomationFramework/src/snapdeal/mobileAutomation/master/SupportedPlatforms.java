package snapdeal.mobileAutomation.master;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation is used to specify a set of platforms supported for a TestCase script
 * @author Khagesh Kapil
 * @see TestCase
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface SupportedPlatforms {
	
	/**
	 * Specifies an array of Platforms supprted
	 * @return
	 * @see Platform
	 */
	Platform[] value();
}