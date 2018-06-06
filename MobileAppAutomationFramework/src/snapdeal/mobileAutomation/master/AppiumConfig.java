package snapdeal.mobileAutomation.master;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This Annotation lets you bind a testsuite flow with its .properties file
 * @author Khagesh Kapil
 * @see AppiumSession
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface AppiumConfig {
	String value() default "";
}