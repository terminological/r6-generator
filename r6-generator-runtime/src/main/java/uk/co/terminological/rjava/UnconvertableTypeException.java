package uk.co.terminological.rjava;

/**
 * This gets thrown when a datatype transformation cannot continue and it is likely a catchable condition 
 * @author terminological
 *
 */
public class UnconvertableTypeException extends Exception {

	public UnconvertableTypeException(String string) {
		super(string);
	}
}
