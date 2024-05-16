package uk.co.terminological.rjava;

public class IdNotFoundException  extends RuntimeException {

	/**
	 * <p>Constructor for IdNotFoundException.</p>
	 *
	 * @param id a {@link java.lang.String} thread ID object
	 */
	public IdNotFoundException(long id) {
		super("missing id: "+id);
	}
	
}
