package uk.co.terminological.rjava;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
	boolean cancelled = false;

	Logger log = LoggerFactory.getLogger(RFuture.class);
	
	/**
	 * <p>Constructor for RFuture.</p>
	 *
	 * @param o a {@link java.lang.Object} object
	 * @param method a {@link java.lang.String} object
	 * @param parameters a {@link java.util.ArrayList} object
	 * @throws java.lang.NoSuchMethodException if any.
	 * @throws java.lang.SecurityException if any.
	 * @throws java.lang.ClassNotFoundException if any.
	 */
	public RFuture(Object o, String method, ArrayList<Object> parameters) throws NoSuchMethodException, SecurityException, ClassNotFoundException {
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
			log.debug("Java call to `"+method+"(...)` was interrupted");
			thread.interrupt();
			cancelled = true;
		}
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
		return thread.getState().equals(java.lang.Thread.State.TERMINATED);
	}

	/** {@inheritDoc} */
	@Override
	public Object get() throws InterruptedException, ExecutionException {
		thread.join();
		if (!runner.succeeded()) throw new ExecutionException("Java call to `"+method+"(...)` resulted in an error",(Exception) runner.result());
		return runner.result();
	}

	/**
	 * <p>get.</p>
	 *
	 * @param timeout a long
	 * @return a {@link java.lang.Object} object
	 * @throws java.lang.InterruptedException if any.
	 * @throws java.util.concurrent.ExecutionException if any.
	 * @throws java.util.concurrent.TimeoutException if any.
	 */
	public Object get(long timeout) throws InterruptedException, ExecutionException, TimeoutException {
		return get(timeout, TimeUnit.MILLISECONDS);
	}
	
	/** {@inheritDoc} */
	@Override
	public Object get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
		unit.timedJoin(thread, timeout);
		if (thread.isAlive()) throw new TimeoutException("Java call to `"+method+"(...)` has not yet completed");
		if (!runner.succeeded()) throw new ExecutionException("Java call to `"+method+"(...)` resulted in an error",(Exception) runner.result());
		return runner.result();
	}
	
	/**
	 * Polls for completion for use in while loop.
	 *
	 * @param ms time in millisecods
	 * @return a boolean
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
	 * <p>succeeded.</p>
	 *
	 * @return a boolean
	 * @throws java.util.concurrent.TimeoutException if any.
	 */
	public boolean succeeded() throws TimeoutException {
		if (thread.isAlive()) throw new TimeoutException("Java call to `"+method+"(...)` has not completed.");
		return !this.isCancelled() && runner.completed();
	}
	
	/**
	 * This assumes checks have been done to ensure the thread is complete and
	 * has succeeded
	 *
	 * @return the result of the thread.
	 */
	public Object getSuccess() {
		return runner.result();
	}
	
	/**
	 * This assumes checks have been dome to ensure thread is complete and
	 * has failed
	 *
	 * @throws java.util.concurrent.ExecutionException
	 */
	public void throwFailure() throws ExecutionException {
		Exception e = (Exception) runner.result();
		throw new ExecutionException("Java call to `"+method+"(...)` resulted in an error: "+e.getMessage(), e);
	}
	
		
	public static class MethodRunnable implements Runnable {
		
		Object o;
		Object[] parameters;
		Method m;
		Object result;
		boolean complete = false;
		boolean success = false;
		
		public MethodRunnable(
				Object o, //may be null if method is static
				Object[] parameters,
				Method m
		) {
			this.o = o;
			this.parameters = parameters;
			this.m = m;
		}
		
		private void doMethod() {
			try {
				result = m.invoke(o, parameters);
				success = true;
			} catch (IllegalAccessException e) {
				throw new RuntimeException("The Java method invoked is not public.");
			} catch (IllegalArgumentException e) {
				throw new RuntimeException("The parameters provided are illegal or not appropriate.");
			} catch (InvocationTargetException e) {
				success = false;
				result = e.getCause();
			}
			complete = true;
		}
		
		public void run() {
			if (Modifier.isStatic(m.getModifiers())) {
				doMethod();
			} else {
				if (
						(m.isAnnotationPresent(RAsync.class) && m.getAnnotation(RAsync.class).synchronise())
						|| m.isAnnotationPresent(RBlocking.class)
				) {
					synchronized (o) {
						doMethod();
					}
				} else {
					doMethod();
				}
			}
		}
		
		public boolean completed() {
			return complete;
		}
		
		public boolean succeeded() {
			return complete && success;
		}
		
		public Object result() {
			return result;
		}
		
	}

	
	
}
