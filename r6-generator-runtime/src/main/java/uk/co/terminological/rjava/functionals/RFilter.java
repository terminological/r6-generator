package uk.co.terminological.rjava.functionals;

import java.util.function.Predicate;

import uk.co.terminological.rjava.types.RPrimitive;

/**
 * <p>RFilter class.</p>
 *
 * @author vp22681
 * @version $Id: $Id
 */
public class RFilter<X extends RPrimitive> {

	String name;
	Predicate<X> predicate;
	
	/**
	 * <p>name.</p>
	 *
	 * @return a {@link java.lang.String} object
	 */
	public String name() {return name;}
	/**
	 * <p>predicate.</p>
	 *
	 * @return a {@link java.util.function.Predicate} object
	 */
	public Predicate<X> predicate() {return predicate;}
	
	/**
	 * <p>Constructor for RFilter.</p>
	 *
	 * @param name a {@link java.lang.String} object
	 * @param predicate a {@link java.util.function.Predicate} object
	 */
	public RFilter(String name, Predicate<X> predicate) {
		this.name = name;
		this.predicate = predicate;
	}
	
	/**
	 * <p>from.</p>
	 *
	 * @param name a {@link java.lang.String} object
	 * @param predicate a {@link java.util.function.Predicate} object
	 * @param <Y> a Y class
	 * @return a {@link uk.co.terminological.rjava.functionals.RFilter} object
	 */
	public static <Y extends RPrimitive> RFilter<Y> from(String name, Predicate<Y> predicate) {
		return new RFilter<Y>(name,predicate);
	}
	
	/**
	 * <p>from.</p>
	 *
	 * @param name a {@link java.lang.String} object
	 * @param type a {@link java.lang.Class} object
	 * @param predicate a {@link java.util.function.Predicate} object
	 * @param <Y> a Y class
	 * @return a {@link uk.co.terminological.rjava.functionals.RFilter} object
	 */
	public static <Y extends RPrimitive> RFilter<Y> from(String name, Class<Y> type, Predicate<Y> predicate) {
		return new RFilter<Y>(name,predicate);
	}
	
}
