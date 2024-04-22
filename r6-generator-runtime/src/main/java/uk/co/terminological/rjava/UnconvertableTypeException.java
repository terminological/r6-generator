package uk.co.terminological.rjava;

/**
 * This gets thrown when a datatype transformation cannot continue and it is likely a catchable condition
 *
 * @author terminological
 * @version $Id: $Id
 */
public class UnconvertableTypeException extends Exception {

	/**
	 * <p>Constructor for UnconvertableTypeException.</p>
	 *
	 * @param string a {@link java.lang.String} object
	 */
	public UnconvertableTypeException(String string) {
		super(string);
	}
}
