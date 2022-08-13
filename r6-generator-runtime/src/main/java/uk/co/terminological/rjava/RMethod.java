package uk.co.terminological.rjava;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Methods marked by this annotation will be included in the R library api.
 * In the R API methods must all have different names so method or constructor overloading is not 
 * supported. Both static and non-static methods are supported allowing for factory style constructors.
 *  
 * examples field is used to populate .Rd files
 * @author terminological
 *
 */
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.CONSTRUCTOR})
public @interface RMethod {

	/**
	 * Populate R examples
	 * @return A list of R commands that will be executed as an example. 
	 */
	String[] examples() default {};
	
	/**
	 * Populate R testthat tests
	 * @return A list of R commands that will be executed as an test of this function. 
	 */
	String[] tests() default {};
}
