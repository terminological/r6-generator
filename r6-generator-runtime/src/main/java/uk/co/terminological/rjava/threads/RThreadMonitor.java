package uk.co.terminological.rjava.threads;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import uk.co.terminological.rjava.IdNotFoundException;
import uk.co.terminological.rjava.RSystemOut;

public class RThreadMonitor {

	private static ArrayList<RFuture> threads = new ArrayList<>();
	
	public static Thread build(Runnable runner) {
		try {
			return (Thread) Thread.class.getMethod("startVirtualThread").invoke(runner);
		} catch(NoSuchMethodException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e){
			return new Thread(runner);
		}
	}
	
	
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
		System.out.println(messages());
		removeCancelled();
		String out = threads.stream().map(
				t -> t.toString())
			.collect(Collectors.joining("\n"));
		return out;
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
	
	public static String messages(long id) {
		String msgs = RSystemOut.getSystemMessages(id);
		String msgs2 = Arrays.stream(msgs.split("\n"))
			.flatMap(s -> 
				s.trim() == "" ?
					Stream.empty() :
					Stream.of(id+"\t"+s.trim())
			)
			.collect(Collectors.joining("\n"));
		return msgs2.trim();
	}
	
	public static String messages() {
		return threads.stream()
				.flatMap(t -> {
					String tmp = messages(t.getId()).trim();
					if (tmp == "") return Stream.empty();
					return Stream.of(t.toString()+"\n"+tmp);
				})
				.collect(Collectors.joining("\n\n"));
	}
	
	public static void tidyUp() {
		removeStale();
		removeCancelled();
	}
	
	public static void deregister(long id) {
		RFuture fut = get(id);
		threads.remove(fut);
	}
	
	public static void deregister() {
		long id = Thread.currentThread().getId();
		deregister(id);
	}
}
