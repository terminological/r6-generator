package uk.co.terminological.rjava.types;

import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * Thing list R lists or named lists are collections in a loose sense of the term
 * as they can be iterated over. R has a odd (from a java perspective) approach to
 * being able to name almost anything. RCollections are either Lists or Maps in the
 * java implementations, but can contain any R derived structure.
 *
 * @author terminological
 * @param <X> any RObject
 * @version $Id: $Id
 */
public interface RCollection<X extends RObject> extends RObject,Iterable<X> {

	/**
	 * <p>stream.</p>
	 *
	 * @return a {@link java.util.stream.Stream} object
	 */
	public default Stream<X> stream() {
		return StreamSupport.stream(this.spliterator(), false);
	}
	
	/**
	 * <p>parallelStream.</p>
	 *
	 * @return a {@link java.util.stream.Stream} object
	 */
	public default Stream<X> parallelStream() {
		return StreamSupport.stream(this.spliterator(), true);
	}
}
