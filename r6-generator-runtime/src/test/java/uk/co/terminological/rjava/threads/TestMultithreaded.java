package uk.co.terminological.rjava.threads;

import static org.junit.jupiter.api.Assertions.*;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;

import uk.co.terminological.rjava.RSystemOut;
import uk.co.terminological.rjava.types.RCharacter;
import uk.co.terminological.rjava.types.RInteger;

class TestMultithreaded {

	@Test
	void test() throws NoSuchMethodException, SecurityException, ClassNotFoundException, InterruptedException, ExecutionException {
		
		PrintStream console = System.out;
		RSystemOut.setup();
		
		System.out.println("Initialised first");
		
		String staticClassName = ThreadTestClass.class.getCanonicalName();
		String methodName = "doStaticTest";
		ArrayList<Object> params = new ArrayList<Object>();
		params.add(RInteger.from(100));
		params.add(RCharacter.from("Hello threads"));
		
		RFuture fut = new RFuture(staticClassName, methodName, params, "JNI", "converter");
		fut.attachProgressBar("prog-bar-1");
		
		console.println("-- System --");
		console.println(RSystemOut.getSystemMessages());
		
		console.println("-- Thread monitor --");
		Thread.sleep(150);
		console.println(RThreadMonitor.status());
		
		console.println("-- Thread --");
		while (!fut.poll(10)) {
			console.println("polling...");
			console.println(fut.getSystemMessages());
			console.println(fut.getProgressRCode());
			console.println(RThreadMonitor.status());
		}
		
		console.println(fut.getProgressRCode());
		console.println(RThreadMonitor.status());
		console.println(fut.get());
		
		console.println("-- Tidyup --");
		console.println(RThreadMonitor.status());
		RSystemOut.release();
		
	}
	
	
	@Test
	void testCancelStatus() throws NoSuchMethodException, SecurityException, ClassNotFoundException {
		
		PrintStream console = System.out;
		RSystemOut.setup();
		
		String methodName = "doSlowTest";
		ArrayList<Object> params = new ArrayList<Object>();
		// Start up the thread
		RFuture fut = new RFuture(new ThreadTestClass(), methodName, params, "JNI", "converter");
		
		assertFalse(fut.isCancelled());
		assertFalse(fut.isDone());
		assertFalse(fut.isStale());
		
		fut.cancel();
		
		// cancellation is regarded as completion but result is stale
		assertTrue(fut.isCancelled());
		assertTrue(fut.isDone());
		assertTrue(fut.isStale());
		
		console.println(fut.getSystemMessages());
		
		RSystemOut.release();
	}

	@Test
	void testDoneStatus() throws NoSuchMethodException, SecurityException, ClassNotFoundException, InterruptedException, ExecutionException {
		
		PrintStream console = System.out;
		RSystemOut.setup();
		
		String methodName = "doFastTest";
		ArrayList<Object> params = new ArrayList<Object>();
		// Start up the thread
		RFuture fut = new RFuture(new ThreadTestClass(), methodName, params, "JNI", "converter");
		// first 50ms thread is running
		assertFalse(fut.isCancelled());
		assertFalse(fut.isDone());
		assertFalse(fut.isStale());
		Thread.sleep(150);
		
		// Should be complete
		assertFalse(fut.isCancelled());
		assertTrue(fut.isDone());
		assertFalse(fut.isStale());
		
		// Result pulled results in stale result
		RCharacter out = (RCharacter) fut.get();
		assertFalse(fut.isCancelled());
		assertTrue(fut.isDone());
		assertTrue(fut.isStale());
		
		// Cancellation post completion and pulling has no effect
		fut.cancel();
		assertFalse(fut.isCancelled());
		assertTrue(fut.isDone());
		assertTrue(fut.isStale());
		
		console.println(out.get());
		
		RFuture fut2 = new RFuture(new ThreadTestClass(), methodName, params, "JNI", "converter");
		Thread.sleep(150);
		assertTrue(fut.isDone());
		
		// Cancellation should mark result as processed and hence it will get
		// tidied up. 
		fut2.cancel();
		assertFalse(fut2.isCancelled());
		assertTrue(fut2.isDone());
		assertTrue(fut2.isStale());
		
		// Staleness is not exposed in R, except through the status monitor
		
		RSystemOut.release();
	}
	
	@Test
	void testProgress() throws NoSuchMethodException, SecurityException, ClassNotFoundException, InterruptedException, ExecutionException {
		
		PrintStream console = System.out;
		RSystemOut.setup();
		
		String methodName = "doProgressTest";
		ArrayList<Object> params = new ArrayList<Object>();
		// Start up the thread
		RFuture fut = new RFuture(new ThreadTestClass(), methodName, params, "JNI", "converter");
		
		assertEquals(fut.getProgressRCode(),"NULL");
		fut.attachProgressBar("test1");
		
		Thread.sleep(10);
		
		assertEquals(fut.getProgressRCode(), "list(total=100, set=10, id=\"test1\")");
		fut.get();
		
		assertEquals(fut.getProgressRCode(), "list(total=100, set=100, id=\"test1\")");
		
		RSystemOut.release();
	
		String methodName2 = "doProgressTest2";
		RFuture fut2 = new RFuture(new ThreadTestClass(), methodName2, params, "JNI", "converter");
		
		assertEquals(fut2.getProgressRCode(),"NULL");
		fut2.attachProgressBar("test1");
		
		Thread.sleep(10);
		
		assertEquals(fut2.getProgressRCode(), "list(total=NA, set=10, id=\"test1\")");
		console.println(RThreadMonitor.status());
		fut2.get();
		
		assertEquals(fut2.getProgressRCode(), "list(total=90, set=90, id=\"test1\")");
		console.println(RThreadMonitor.status());
		
		RSystemOut.release();
	}
	
	@Test
	void testMonitor() {
		PrintStream console = System.out;
		RSystemOut.setup();
		
		String methodName = "doSlowTest";
		ArrayList<Object> params = new ArrayList<Object>();
		IntStream.range(0, 10).forEach(i -> {
			try {
				RFuture fut = new RFuture(new ThreadTestClass(i), methodName, params, "JNI", "converter");
				fut.attachProgressBar("test"+i);
				Thread.sleep(5);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		});
		
		console.println(RThreadMonitor.status());
		
		
		RSystemOut.release();
	}
	
	@Test
	void testSystemOut() throws InterruptedException, ExecutionException {
		PrintStream console = System.out;
		RSystemOut.setup();
		
		String methodName = "doSystemOutTest";
		List<RFuture> tmp = IntStream.range(0, 10).mapToObj(i -> {
			try {
				ArrayList<Object> params = new ArrayList<Object>();
				params.add(i);
				RFuture fut = new RFuture(new ThreadTestClass(i), methodName, params, "JNI", "converter");
				Thread.sleep(5);
				return fut;
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}).collect(Collectors.toList());
		
		for (RFuture t: tmp) {
			console.println("---- NEXT THREAD ----");
			console.println(t.getSystemMessages());
			console.println("----");
			t.get();
			console.println(t.getSystemMessages());
		}
		
		RSystemOut.release();
	}
}
