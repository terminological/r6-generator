package uk.co.terminological.rjava.functionals;

import java.util.function.Predicate;

import uk.co.terminological.rjava.types.RPrimitive;

public class RFilter<X extends RPrimitive> {

	String name;
	Predicate<X> predicate;
	
	public String name() {return name;}
	public Predicate<X> predicate() {return predicate;}
	
	public RFilter(String name, Predicate<X> predicate) {
		this.name = name;
		this.predicate = predicate;
	}
	
	public static <Y extends RPrimitive> RFilter<Y> from(String name, Predicate<Y> predicate) {
		return new RFilter<Y>(name,predicate);
	}
	
	public static <Y extends RPrimitive> RFilter<Y> from(String name, Class<Y> type, Predicate<Y> predicate) {
		return new RFilter<Y>(name,predicate);
	}
	
}
