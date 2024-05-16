package uk.co.terminological.rjava.threads;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * A set of static methods for monitoring progress
 * The key functions are setTotal()
 */
public class RProgressMonitor {

	private static ConcurrentMap<Long, RProgress> monitors = new ConcurrentHashMap<>();
	
	protected static void register() {
		Long id = Thread.currentThread().getId();
		register(id);
	}
	
	protected static void register(long id) {
		if (!monitors.containsKey(id)) monitors.put(id, new RProgress());
	}
	
	protected static void deregister() {
		Long id = Thread.currentThread().getId();
		register(id);
	}
	
	protected static void deregister(long id) {
		if (monitors.containsKey(id)) monitors.remove(id);
	}
	
	protected static Optional<RProgress> progress(RFuture fut) {
		return Optional.ofNullable(monitors.get(fut.getId()));
	}
	
	private static RProgress prog() {
		Long id = Thread.currentThread().getId();
		register(id);
		return monitors.get(id);
	}
	
	public static void increment() {
		prog().increment();
	}
	
	public static void increment(int i) {
		prog().increment(i);
	}
	
	
	public static void setTotal(int total) {
		prog().setTarget(total);
	}

	protected static void setName(RFuture fut, String name) {
		register(fut.getId());
		monitors.get(fut.getId()).setName(name);
	}

	public static void complete() {
		prog().complete();
	}
	
	

}
