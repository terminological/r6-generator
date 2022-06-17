package uk.co.terminological.rjava;

import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * The RDefault annotation can be used to define R code that will be used if nothing is passed to the this method for the annotated value.
 */
@Retention(RUNTIME)
@Target(PARAMETER)
public @interface RDefault {
	
	/**
	 * A default value specified as an R expression that will be passed to the function
	 * if not explicitly given. e.g. "c('a','b','c')" or "1.234" or "Sys.Date()"
	 * @return the R expression
	 */
	String rCode();
	
}
