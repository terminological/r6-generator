package uk.co.terminological.rjava;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.stream.Collector;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * The ConvertingCollector interface allows us to specify a set of rules for converting an X into a Y and
 * then apply these to streams, iterators, arrays, collections, and iterables, as well as plain
 * instances. By default all of these conversions are handled through streams. The target data type is essentially
 * something that will convert to a R dataframe or vector.
 * 
 * @author terminological
 *
 * @param <X> - source datatype
 * @param <Y> - target datatype
 */
public class CollectingConverter<X,Y> {	
	
	Collector<X,?,Y> collector;
	
	public CollectingConverter(Collector<X,?,Y> collector) {
		this.collector = collector;
	}
	
	/**
	 * Convert an instance of type X to Y
	 * @param instance
	 * @return
	 */
	public Y convert(X instance) {
		return convert(Stream.of(instance));
	}
	
	/**
	 * Convert a stream of 
	 * @param stream
	 * @return
	 */
	public Y convert(Stream<X> stream) {
		return stream.collect(collector);
	}
	
	public Y convert(Collection<X> collection) {
		return convert(collection.stream());
	}
	
	public Y convert(Iterable<X> iterable) {
		return convert(StreamSupport.stream(iterable.spliterator(), false));
	}
	
	public Y convert(Iterator<X> iterator) {
		return convert(() -> iterator);
	}
	
	public Y convert(X[] array) {
		return convert(Arrays.asList(array));
	}

	public static <A,B> CollectingConverter<A,B> from(Collector<A,?,B> collector) {
		return new CollectingConverter<A,B>(collector);
	}
	
	
	
	
	
}