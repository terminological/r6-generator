package uk.co.terminological.rjava.threads;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.co.terminological.rjava.RSystemOut;

/**
 * A slightly idiosyncratic implementation of a Java Future API that can be called
 * from generated R code. This only executes a named method with supplied parameters
 * which are validated at runtime using reflection. The use of this is triggered
 * by an @RAsync annotation on a method. It is also used for @RBlocking annotated
 * methods so as to allow long running blocking functions to be interrupted through R.
 *
 * This is intended really to be only accessible through generated R6 code and not
 * generally expected to be used directly.
 *
 * @author vp22681
 * @version $Id: $Id
 */
public class RFuture implements Future<Object> {

	Thread thread;
	MethodRunnable runner;
	String method;
	long id;
	boolean cancelled = false;
	boolean processed = false;
	String returnSig;
	String converter;
	
	
	
	
	
	Logger log = LoggerFactory.getLogger(RFuture.class);
	
	/**
	 * <p>Constructor for RFuture.</p>
	 *
	 * @param o a {@link java.lang.Object} or a String representing a fully qualified class name
	 * @param method a {@link java.lang.String} object to be run in a thread
	 * @param parameters a {@link java.util.ArrayList} object the parameters of the method (not checked)
	 * @param returnSig the return signature in JNI notation
	 * @param converter the R expression to convert the return value to a R value (as a string).
	 * @throws java.lang.NoSuchMethodException if any.
	 * @throws java.lang.SecurityException if any.
	 * @throws java.lang.ClassNotFoundException if any.
	 */
	public RFuture(
			Object o, 
			String method, 
			ArrayList<Object> parameters,
			String returnSig,
			String converter
		) throws NoSuchMethodException, SecurityException, ClassNotFoundException {
		Class<?>[] types = parameters.stream().map(p -> p.getClass()).collect(Collectors.toList()).toArray(new Class[] {});
		if (o instanceof String) {
			//TODO: this needs to be fixed for more generic class names, mapping JNI to Java
			Method m = Class.forName(((String) o).replaceAll("/", ".")).getMethod(method, types);
			runner = new MethodRunnable(null,parameters.toArray(),m);
		} else {
			Method m = o.getClass().getMethod(method, types);
			runner = new MethodRunnable(o,parameters.toArray(),m);
		}
		this.method = method;
		thread = new Thread(runner);
		thread.start();
		while(thread.getState().equals(java.lang.Thread.State.NEW)) {}
		String name = RThreadMonitor.register(this);
		this.id = thread.getId();
		this.converter = converter;
		this.returnSig = returnSig;
		thread.setName(name);
	}
	
	/**
	 * <p>cancel.</p>
	 *
	 * @return a boolean
	 */
	public boolean cancel() {
		return cancel(true);
	}
	
	/** {@inheritDoc} */
	@Override
	public boolean cancel(boolean mayInterruptIfRunning) {
		if (!thread.getState().equals(java.lang.Thread.State.TERMINATED)) {
			thread.interrupt();
			cancelled = true;
		}
		processed = true;
		return isCancelled();
	}

	/** {@inheritDoc} */
	@Override
	public boolean isCancelled() {
		return cancelled || thread.isInterrupted();
	}

	/** {@inheritDoc} */
	@Override
	public boolean isDone() {
		return isCancelled() || thread.getState().equals(java.lang.Thread.State.TERMINATED);
	}

	public boolean isStale() {
		return processed;
	}
	
	public String getMethod() {
		return method;
	}
	
	/** {@inheritDoc} 
	 * This will only throw an interrupted exception if it was cancelled already
	 * or in interrupted whilst waiting to join the thread.
	 * 
	 */
	@Override
	public Object get() throws InterruptedException, ExecutionException {
		if (isCancelled()) {
			processed = true;
			throw new InterruptedException("Background call to `"+method+"(...)` was interrupted");
		}
		thread.join();
		processed = true;
		return runner.result();
	}

	/**
	 * <p>get.</p>
	 *
	 * @param timeout a long representing number of milliseconds
	 * @return a {@link java.lang.Object} object
	 * @throws java.lang.InterruptedException if the function was cancelled already or is cancelled in the .
	 * next `timeout` milliseconds.
	 * @throws java.util.concurrent.ExecutionException if any issues executing the function.
	 * @throws java.util.concurrent.TimeoutException if the function has not completed.
	 */
	public Object get(long timeout) throws InterruptedException, ExecutionException, TimeoutException {
		return get(timeout, TimeUnit.MILLISECONDS);
	}
	
	/** {@inheritDoc} */
	@Override
	public Object get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
		if (isCancelled()) {
			processed = true;
			throw new InterruptedException("Background call to `"+method+"(...)` was interrupted");
		}
		unit.timedJoin(thread, timeout);
		if (!this.isDone()) {
			processed = false;
			throw new TimeoutException("Background call to `"+method+"(...)` has not yet completed");
		}
		processed = true;
		return runner.result();
	}
	
	/**
	 * Polls for completion for use in while loop.
	 *
	 * @param ms time in millisecods
	 * @return a boolean, true if completed, false if still running. Completed
	 * means thread is finished processing. It can be result of cancellation, 
	 * error or completion.
	 */
	public boolean poll(long ms) {
		try {
			thread.join(ms);
			return !thread.isAlive();
		} catch (InterruptedException e) {
			return true;
		}
	}

	/**
	 * Gets the system messages from Java for thread in current
	 * method thread.
	 *
	 * @return the system messages
	 */
	public String getSystemMessages() {
		return RSystemOut.getSystemMessages(id);
	}
	
	public void attachProgressBar(String name) {
		RProgressMonitor.setName(this,name);
	}
	
	// possible this has been deregistered.
	private Optional<RProgress> getProgress(){
		return RProgressMonitor.progress(this);
	}
	
	public String getProgressRCode() {
		return getProgress().map(p -> p.rCode()).orElse("NULL");
	}
	
	public String toString() {
		return this.method+"(...)"
			+(this.isCancelled() ? " [cancelled]" : 
				(this.isDone() ? 
					(this.isStale() ? " [result processed]" : " [result ready]"):
					(this.getProgress().map(p -> " ["+p.toString()+"]").orElse(" [in progress]"))
				)
			);
	}

	public long getId() {
		return id;
	}
	
	public String getReturnSig() {
		return returnSig;
	}
	
	public String getConverter() {
		return converter;
	}
}
