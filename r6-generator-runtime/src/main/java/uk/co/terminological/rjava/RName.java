package uk.co.terminological.rjava;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * The RName anotation marks getter fields for mapping POJO fields to named lists or dataframe columns.
 * It is also used to map dataframe columns to annotated java interface methods using proxy classes.
 *
 * @author vp22681
 * @version $Id: $Id
 */
@Retention(RUNTIME)
@Target(METHOD)
@Inherited
public @interface RName {
	String value();
}
