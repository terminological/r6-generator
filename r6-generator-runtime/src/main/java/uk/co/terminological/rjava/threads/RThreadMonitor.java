package uk.co.terminological.rjava.threads;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.stream.Collectors;

import uk.co.terminological.rjava.IdNotFoundException;

public class RThreadMonitor {

	private static ArrayList<RFuture> threads = new ArrayList<>();

	public static String register(RFuture future) {
		threads.add(future);
		RProgressMonitor.register(future.getId());
		return(future.getMethod()+"#"+threads.size());
	}
	
	private static void removeStale() {
		for (Iterator<RFuture> it = threads.iterator(); it.hasNext(); ) {
			RFuture fut = it.next();
			if (fut.isStale()) {
				it.remove();
				RProgressMonitor.deregister(fut.getId());
			}
		}
	}
	
	private static void removeCancelled() {
		for (Iterator<RFuture> it = threads.iterator(); it.hasNext(); ) {
			RFuture fut = it.next();
			if (fut.isCancelled()) {
				it.remove();
				RProgressMonitor.deregister(fut.getId());
			}
		}
	}
	
	public static String status() {
		return threads.stream().map(
				t -> String.valueOf(t.getId()) + "\t"+
						t.toString())
			.collect(Collectors.joining("\n"));
	}
	
	public static void cancelAll() {
		threads.stream().forEach(t -> t.cancel());
		removeCancelled();
	}
	
	public static void cancel(long id) {
		threads.stream()
			.filter(t -> t.getId()==id)
			.findFirst()
			.ifPresent(t -> t.cancel());
		removeCancelled();
	}
	
	public static RFuture get(long id) {
		return threads.stream()
			.filter(t -> t.getId()==id)
			.findFirst()
			.orElseThrow(() -> new IdNotFoundException(id));
	}
	
	public static void tidyUp() {
		removeStale();
		removeCancelled();
	}
}
