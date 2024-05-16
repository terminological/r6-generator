package uk.co.terminological.rjava;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import uk.co.terminological.rjava.threads.ByteArrayPrintStream;

/**
 * 
 * 
 * Delegates the print stream output from System.out
 * to a per-thread set of ByteArrayPrintStreams keyed
 * on thread id. This intercepts any write to System.out
 * and first of all picks a concurrent map of print streams 
 * keyed by thread Id.
 * This is possibly quite a lot slower than the default.
 * 
 * This is like it is so that later on the RFuture class can 
 * asynchronously find out what console output has been gathered so
 * far for a given thread without being in that thread. 
 * 
 * This lets system.out be called by a long running method in a thread
 * and the output be available in the RFuture monitor thread.   
 */
public class RSystemOut extends PrintStream {

	private static RSystemOut instance;
	
	static RSystemOut instance() {
		if (instance == null) {
			instance = new RSystemOut();
			instance.consoleId = Thread.currentThread().getId();
			instance.console = System.out;
			System.setOut(instance);
		}
		return instance;
	}
	
	private ConcurrentMap<Long, ByteArrayPrintStream> streams = new ConcurrentHashMap<>();
	private long consoleId;
	private PrintStream console;
	
	private ByteArrayPrintStream baps() {
		Long id = Thread.currentThread().getId();
		if (!streams.containsKey(id)) {
			streams.put(id, new ByteArrayPrintStream());
		}
		return streams.get(id);
	}
	
	private RSystemOut() {
		super(new ByteArrayOutputStream(0));
	}

	@Override public boolean checkError() {
		return baps().checkError();
	}

	@Override public void write(byte[] buf, int off, int len) {
		baps().write(buf, off, len);
	}

	@Override public void write(int b) { 
		baps().write(b); }

	@Override public void flush() { 
		baps().flush(); 
	}
	
	@Override public void close() { 
		
		baps().close();
		Long id = Thread.currentThread().getId();
		streams.remove(id);
	}

	// don't want to close System.out
	@SuppressWarnings("resource")
	public static String getSystemMessages(long id) {
		if (instance == null) throw new RuntimeException("The RSystemOut class has not been setup.");
		if (!instance().streams.containsKey(id)) {
			// this happens if a thread has not 
			// output anything on System.out, so it never
			// gets registered.
			return "";
		}
		return instance().streams.get(id).getAndClear();
	}
	
	// don't want to close System.out
	// This is the original thread it was called from.
	// By design this will be initialised by the library
	// Via the LogController.
	@SuppressWarnings("resource")
	public static String getSystemMessages() {
		return getSystemMessages(instance().consoleId);
	}

	public static void setup() {
//		if (instance != null) {
//			throw new RuntimeException("The system console is already configured")
//		}
		instance();
	}
	
	// don't want to close System.out
	@SuppressWarnings("resource")
	public static void release() {
		long mainThreadId = instance().consoleId;
		System.setOut(instance().console);
		instance().streams.forEach((i,s) -> {
			if (i != mainThreadId) {
				System.out.println(s.getAndClear());
				s.close();
			}
		});
	}
}
