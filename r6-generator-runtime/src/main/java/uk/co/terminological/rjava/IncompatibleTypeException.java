package uk.co.terminological.rjava;

/**
 * This gets thrown when a datatype transformation cannot continue due to a fundamentally uncatchable condition.
 *
 * @author terminological
 * @version $Id: $Id
 */
public class IncompatibleTypeException extends RuntimeException {

	/**
	 * <p>Constructor for IncompatibleTypeException.</p>
	 *
	 * @param string a {@link java.lang.String} object
	 */
	public IncompatibleTypeException(String string) {
		super(string);
	}

	/**
	 * <p>Constructor for IncompatibleTypeException.</p>
	 *
	 * @param string a {@link java.lang.String} object
	 * @param e a {@link uk.co.terminological.rjava.UnconvertableTypeException} object
	 */
	public IncompatibleTypeException(String string, UnconvertableTypeException e) {
		super(string,e);
	}

}
