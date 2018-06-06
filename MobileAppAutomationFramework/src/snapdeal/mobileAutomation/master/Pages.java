package snapdeal.mobileAutomation.master;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation is used to specify the Page needed by a TestCase Object before its script method
 * It is also used in Platform annotation for the same purpose
 * @author Khagesh Kapil
 * @see Platform
 * @see TestCase
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Pages {
	
	/**
	 * It is used to hold the pages needed by a TestCase Object
	 * @return names of the pages
	 */
	String[] names() default "";
}
