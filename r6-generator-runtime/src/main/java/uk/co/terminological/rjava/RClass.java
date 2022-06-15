package uk.co.terminological.rjava;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation identifies a class as part of an R library api
 * Fields here will populate data in DESCRIPTION file and allow
 * R to load dependencies when it loads the R library api 
 * @author terminological
 *
 */
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface RClass {
	
	/**
	 * A set of R library dependencies specified as the CRAN library name
	 * @return
	 */
	String[] imports() default {};
	
	/**
	 * A set of R library suggestions specified as the CRAN library name
	 * @return
	 */
	String[] suggests() default {};
	
}
