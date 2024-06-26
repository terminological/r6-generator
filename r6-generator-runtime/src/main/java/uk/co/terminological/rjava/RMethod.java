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
 * Methods annotated with this block the R process and cannot be interrupted. For this reason they
 * should always return a value in a socially acceptable time frame. If there is the possibility the
 * method can hang then using an `RBlocking` or `RAsync` alternative annotation is preferred.
 *
 * examples field is used to populate .Rd files
 *
 * @author terminological
 * @version $Id: $Id
 */
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.CONSTRUCTOR})
public @interface RMethod {

	/**
	 * Populate R examples. For non static methods this will be combined with @RClass(exampleSetup) annotation to
	 * construct a complete example. For static methods (and constructors) the examples will be run as is. Use try({}) 
	 * in examples you are not sure about or are developing. Please use single quotes only in R expressions (don't try escaping double quotes)
	 * @return A list of R commands that will be executed as an example of this function.
	 */
	String[] examples() default {};
	
	/**
	 * Populate R testthat tests. For non static methods this will be combined with @RClass(testSetup) annotation to
	 * construct a complete test. For static methods (and constructors) the examples will be run as is. Use try({}) 
	 * in examples you are not sure about or are developing. Please use single quotes only in R expressions (don't try escaping double quotes)
	 * @return A list of R commands that will be executed as a test of this function. 
	 */
	String[] tests() default {};
	
}
