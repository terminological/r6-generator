package uk.co.terminological.rjava;

/**
 * <p>NameNotFoundException class.</p>
 *
 * @author vp22681
 * @version $Id: $Id
 */
public class NameNotFoundException extends RuntimeException {

	/**
	 * <p>Constructor for NameNotFoundException.</p>
	 *
	 * @param col a {@link java.lang.String} object
	 */
	public NameNotFoundException(String col) {
		super("missing name: "+col);
	}

}
