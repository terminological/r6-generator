package uk.co.terminological.rjava;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation identifies a class as a datatype in the R library api
 * Fields here are R code used to handle conversion to and from R. 
 * @author terminological
 *
 */
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface RDataType {
	String[] RtoJava();
	String[] JavaToR();
	//String JNIType();
}
