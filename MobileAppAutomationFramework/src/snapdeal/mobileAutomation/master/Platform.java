package snapdeal.mobileAutomation.master;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation is used to set supported platforms to a TestCase Object
 * @author Khagesh Kapil
 * @see Pages
 * @see VersionControlledFor
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Platform {
	
	/**
	 * Specifies the name of the platform/flavour to be added
	 * @return
	 */
	String name();
	
	/**
	 * Specifies the Pages needed by the script
	 * @return
	 * @see Pages
	 */
	Pages pages();
	
	/**
	 * Specifies the supported version of the platform/flavour
	 * @return
	 * @see VersionControlledFor
	 */
	VersionControlledFor version();
}