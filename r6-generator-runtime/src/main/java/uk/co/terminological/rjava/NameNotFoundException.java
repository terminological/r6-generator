package uk.co.terminological.rjava;

public class NameNotFoundException extends RuntimeException {

	public NameNotFoundException(String col) {
		super("missing name: "+col);
	}

}
