package uk.co.terminological.rjava.threads;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

public class ByteArrayPrintStream extends PrintStream {

	private ByteArrayOutputStream baos;
	
	private ByteArrayPrintStream(ByteArrayOutputStream b) {
		super(b);
		baos = b;
	}

	public ByteArrayPrintStream() {
		this(new ByteArrayOutputStream());
	}
	
	public String getAndClear() {
		String tmp =  baos.toString().trim();
		baos.reset();
		return tmp;
	}
}
