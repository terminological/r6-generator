package uk.co.terminological.rjava.types;

import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import uk.co.terminological.rjava.ZeroDimensionalArrayException;
import uk.co.terminological.rjava.utils.RObjectVisitor;

/**
 * The RArrays are actually vectors with the logical dimensions specified seperately, and they can be re-dimensioned on the fly.
 *
 * @param <X> the generic type
 * @author vp22681
 * @version $Id: $Id
 */
public abstract class RArray<X extends RPrimitive> implements RObject {

	private static final long serialVersionUID = RObject.datatypeVersion;
	
	/**
	 * <p>getVector.</p>
	 *
	 * @return a {@link uk.co.terminological.rjava.types.RVector} object
	 */
	public abstract RVector<X> getVector();
	int[] dimensions;
	
	/**
	 * <p>getDimensionality.</p>
	 *
	 * @return a int
	 */
	public int getDimensionality() {
		return dimensions.length;
	}
		
	/**
	 * <p>get.</p>
	 *
	 * @return a {@link java.util.stream.Stream} object
	 * @throws uk.co.terminological.rjava.ZeroDimensionalArrayException if any.
	 */
	public abstract Stream<? extends RArray<X>> get() throws ZeroDimensionalArrayException;
	
	/** {@inheritDoc} */
	@Override
	public String rCode() {
		String dims = Arrays.stream(dimensions).boxed().map(n -> n.toString()).collect(Collectors.joining(", "));
		return "array("+getVector().rCode()+", c("+dims+"))";
	}
	
	/** {@inheritDoc} */
	@Override
	public <Y> Y accept(RObjectVisitor<Y> visitor) {
		Y out = visitor.visit(this);
		if (getDimensionality() == 0) this.getVector().get(0).accept(visitor);
		if (getDimensionality() == 1) this.getVector().accept(visitor);
		try {
			this.get().forEach(visitor::visit);
		} catch (ZeroDimensionalArrayException e) {
			throw new RuntimeException(e);
		}
		return out;
	}

	/**
	 * <p>getType.</p>
	 *
	 * @return a {@link java.lang.Class} object
	 */
	public abstract Class<RNumeric> getType();
	
	/**
	 * <p>stream.</p>
	 *
	 * @return a {@link java.util.stream.Stream} object
	 */
	public Stream<RObject> stream() {
		if (getDimensionality() == 0) return Stream.of(this.getVector().get(0));
		if (getDimensionality() == 1) return this.getVector().stream().map(x -> (RPrimitive) x);
		try {
			return this.get().map(x->(RObject) x);
		} catch (ZeroDimensionalArrayException e) {
			throw new RuntimeException(e);
		}
	}
	
}
