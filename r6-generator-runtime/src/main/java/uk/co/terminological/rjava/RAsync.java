package uk.co.terminological.rjava;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Methods marked by this annotation will be included in the R library api.
 * Both static and non-static methods are supported. An async method may take a long time to
 * execute and so is delegated to a java thread and returns an RFuture which will contain the
 * asynchronously executed code.
 *
 * examples field is used to populate .Rd files
 *
 * @author terminological
 * @version $Id: $Id
 */
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.CONSTRUCTOR})
public @interface RAsync {

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
	
	/**
	 * Should the method be synchronised on the enclosing object?
	 * In general the responsibility of writing thread safe code is the developers. A method annotated with @RAsync
	 * may be called repeatedly and  
	 * @return
	 */
	boolean synchronise() default false;
}
