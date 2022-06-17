package uk.co.terminological.rjava;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.stream.Collector;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * The ConvertingCollector interface allows us to reuse stream collectors for other non stream data types by
 * apply these to streams, iterators, arrays, collections, and iterables, as well as plain
 * instances. By default all of these conversions are handled through streams. The target data type is in our case usually
 * an RVector
 * 
 * @author terminological
 *
 * @param <X> - source datatype which will usually be a java primitive type or similar
 * @param <Y> - target datatype which will usually be an RVector of some kind
 */
public class CollectingConverter<X,Y> {	
	
	/** The collector. */
	Collector<X,?,Y> collector;
	
	/**
	 * Instantiates a new collecting converter.
	 *
	 * @param collector the collector
	 */
	public CollectingConverter(Collector<X,?,Y> collector) {
		this.collector = collector;
	}
	
	/**
	 * Convert an instance of type X to Y.
	 *
	 * @param instance a singleton of type X
	 * @return a list or array
	 */
	public Y convert(X instance) {
		return convert(Stream.of(instance));
	}
	
	/**
	 * Convert a stream of type X to Y.
	 *
	 * @param stream a stream of type X
	 * @return a list or array
	 */
	public Y convert(Stream<X> stream) {
		return stream.collect(collector);
	}
	
	/**
	 * Convert a collection of type X to Y.
	 *
	 * @param collection a collection of type X
	 * @return a list or array
	 */
	public Y convert(Collection<X> collection) {
		return convert(collection.stream());
	}
	
	/**
	 * Convert a iterable of type X to Y.
	 *
	 * @param iterable the iterable
	 * @return the y
	 */
	public Y convert(Iterable<X> iterable) {
		return convert(StreamSupport.stream(iterable.spliterator(), false));
	}
	
	/**
	 * Convert a iterator of type X to Y.
	 *
	 * @param iterator the iterator
	 * @return the y
	 */
	public Y convert(Iterator<X> iterator) {
		return convert(() -> iterator);
	}
	
	/**
	 * Convert a array of type X to Y.
	 *
	 * @param array the array
	 * @return the y
	 */
	public Y convert(X[] array) {
		return convert(Arrays.asList(array));
	}

	/**
	 * Static constructor using a collector.
	 *
	 * @param <A> the generic type
	 * @param <B> the generic type
	 * @param collector the collector
	 * @return the collecting converter
	 */
	public static <A,B> CollectingConverter<A,B> from(Collector<A,?,B> collector) {
		return new CollectingConverter<A,B>(collector);
	}
	
	
	
	
	
}