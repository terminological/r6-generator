package uk.co.terminological.rjava.threads;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.concurrent.ExecutionException;

import uk.co.terminological.rjava.RAsync;
import uk.co.terminological.rjava.RBlocking;

public class MethodRunnable implements Runnable {
	
	Object o;
	Object[] parameters;
	Method m;
	Object result = null;
	Exception exception = null;
	boolean complete = false;
	
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
		} catch (IllegalAccessException e) {
			throw new RuntimeException("The method invoked is not public.");
		} catch (IllegalArgumentException e) {
			throw new RuntimeException("The parameters provided are illegal or not appropriate.");
		} catch (InvocationTargetException e) {
			exception = (Exception) e.getCause();
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
		return complete && exception == null;
	}
	
	public Object result() throws ExecutionException, InterruptedException {
		if (exception != null) {
			if (exception instanceof InterruptedException) throw (InterruptedException) exception;
			throw new ExecutionException("Background call to `"+m.getName()+"(...)` resulted in an error: "+exception.getMessage(), exception);
		}
		return result;
	}

}