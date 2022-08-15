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
	 * @return the imports packages to add to the DESCRIPTION file
	 */
	String[] imports() default {};
	
	/**
	 * A set of R library suggestions specified as the CRAN library name
	 * @return the suggests packages to add to the DESCRIPTION file
	 */
	String[] suggests() default {};
	
	/**
	 * The common setup for examples of non-static functions in this class.
	 * @return must be valid R code that provides necessary setup for manual examples of the generated code.
	 */
	String[] exampleSetup() default {};
	
	/**
	 * The common setup for tests of non-static functions in this class.
	 * @return must be valid R code that provides necessary setup for testing the generated code.
	 */
	String[] testSetup() default {};
}
