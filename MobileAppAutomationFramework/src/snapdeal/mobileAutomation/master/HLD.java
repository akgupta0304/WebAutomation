package snapdeal.mobileAutomation.master;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This Annotation lets you bind a testcase script with a HLD
 * for ex. a test case script method can have an annotation as
 * @HLD("HLD_01")
 * @author Khagesh Kapil
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface HLD {
 String value() default "";
}
