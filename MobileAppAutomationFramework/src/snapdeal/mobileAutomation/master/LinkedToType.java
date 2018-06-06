package snapdeal.mobileAutomation.master;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This Annotation lets you bind a testcase to a entity type which can be a Module or MultiModuleFeature/SupportingModule or SpecialFeature
 * for ex. a test case script method can have an annotation as
 * @LinkedToType("Module:Login") or @LinkedToType("MultiModuleFeature:Notifications") or @LinkedToType("SupportingModule:Notifications") or @LinkedToType("SpecialFeature:Refferal")
 * Multiple scripts can have a common value in its @LinkedToType annotation but the reverse in not true
 * @author Khagesh Kapil
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface LinkedToType {
	String value() default "";
}
