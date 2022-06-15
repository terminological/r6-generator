package uk.co.terminological.rjava.types;

import java.util.function.Predicate;

public class RNamedPredicate<X extends RPrimitive> {

	String name;
	Predicate<X> predicate;
	
	public String name() {return name;}
	public Predicate<X> predicate() {return predicate;}
	
	public RNamedPredicate(String name, Predicate<X> predicate) {
		this.name = name;
		this.predicate = predicate;
	}
	
	public static <Y extends RPrimitive> RNamedPredicate<Y> from(String name, Predicate<Y> predicate) {
		return new RNamedPredicate<Y>(name,predicate);
	}
	
	public static <Y extends RPrimitive> RNamedPredicate<Y> from(String name, Class<Y> type, Predicate<Y> predicate) {
		return new RNamedPredicate<Y>(name,predicate);
	}
	
}
