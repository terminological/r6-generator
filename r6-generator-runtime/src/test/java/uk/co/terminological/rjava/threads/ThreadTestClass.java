package uk.co.terminological.rjava.threads;

import uk.co.terminological.rjava.RAsync;
import uk.co.terminological.rjava.types.RCharacter;
import uk.co.terminological.rjava.types.RInteger;

public class ThreadTestClass {

	
	private int index;

	public ThreadTestClass() {
		this(1);
	}
	
	public ThreadTestClass(int i) {
		this.index = i;
	}

	@RAsync
	public static RCharacter doStaticTest(RInteger int1, RCharacter char1) throws InterruptedException {
		
		RProgressMonitor.setTotal(int1.get());
		for (int i=0; i<int1.get()-10; i++) {
			RProgressMonitor.increment();
			Thread.sleep(10);
			if (i % 10 == 0) {
				System.out.println(char1.get()+": "+i);
			}
		}
		RProgressMonitor.complete();
		
		System.out.println(char1.get()+" complete");
		
		return RCharacter.from(char1.get().toUpperCase());
	}
	
	@RAsync
	public RCharacter doSlowTest() throws InterruptedException {
		
		RProgressMonitor.setTotal(100);
		for (int i=0; i<100; i++) {
			Thread.sleep(10);
			RProgressMonitor.increment();
		}
		RProgressMonitor.complete();
		return RCharacter.from("slow test complete: "+index);
		
	}
	
	@RAsync
	public RCharacter doFastTest() throws InterruptedException {
		
		Thread.sleep(50);
		return RCharacter.from("fast test complete: "+index);
	}
	
	@RAsync
	public void doProgressTest() throws InterruptedException {
		
		RProgressMonitor.setTotal(100);
		RProgressMonitor.increment(10);
		Thread.sleep(100);
		RProgressMonitor.increment(80);
		Thread.sleep(100);
		RProgressMonitor.complete();
		
	}
	
	@RAsync
	public void doProgressTest2() throws InterruptedException {
		
		RProgressMonitor.increment(10);
		Thread.sleep(100);
		RProgressMonitor.increment(80);
		Thread.sleep(100);
		RProgressMonitor.complete();
		
	}
	
	public void doSystemOutTest(Integer threadNum) throws InterruptedException {
		
		for (int i=0; i<10; i++) {
			Thread.sleep(10);
			System.out.println("message "+i+" from "+threadNum);
		}
		
	}
}
