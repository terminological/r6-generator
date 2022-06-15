package uk.co.terminological.rjava;

/**
 * This gets thrown when a datatype transformation cannot continue due to a fundamentally uncatchable condition. 
 * @author terminological
 *
 */
public class IncompatibleTypeException extends RuntimeException {

	public IncompatibleTypeException(String string) {
		super(string);
	}

	public IncompatibleTypeException(String string, UnconvertableTypeException e) {
		super(string,e);
	}

}
