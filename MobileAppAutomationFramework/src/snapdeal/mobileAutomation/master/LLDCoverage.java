package snapdeal.mobileAutomation.master;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This Annotation lets you bind a testcase to a set of LLDs
 * for ex. a test case script method can have an annotation as
 * @LLDCoverage(
 *			@LLD("LLD_01"),
 *			@LLD("LLD_02")
 *			)
 * @author Khagesh Kapil
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface LLDCoverage {
	LLD[] value();
}
