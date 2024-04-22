package uk.co.terminological.rjava;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * The methods marked by this annotation will be called just prior to garbage collection and can be used to free up resources.
 * Any exceptions thrown by the methods will be ignored.
 *
 * @author terminological
 * @version $Id: $Id
 */
@Retention(RUNTIME)
@Target(METHOD)
public @interface RFinalize {

}
