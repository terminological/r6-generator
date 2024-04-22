package uk.co.terminological.rjava.types;

import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Stream;

import uk.co.terminological.rjava.RDataType;
import uk.co.terminological.rjava.utils.RObjectVisitor;

/**
 * The vector of logicals is needed to ensure that NA values are correctly handled and
 * allow flexibility of a java List structure for easy manipulation. Factory methods are in {@link uk.co.terminological.rjava.types.RVector}.
 *
 * @author terminological
 * @version $Id: $Id
 */
@RDataType(
		JavaToR = {
				"function(jObj) as.logical(rJava::.jcall(jObj,returnSig='[I',method='rPrimitive'))",
		},
		RtoJava = {
				"function(rObj) {",
				"	if (is.null(rObj)) return(rJava::.jnew('~RLOGICALVECTOR~'))",
				"	if (!is.logical(rObj)) stop('expected a vector of logicals')",
				"	tmp = as.integer(rObj)",
				"	return(rJava::.jnew('~RLOGICALVECTOR~',rJava::.jarray(tmp)))",
				"}"
		}//,
		//JNIType = "[I"
	)
public class RLogicalVector extends RVector<RLogical> implements JNIPrimitive {
	
	private static final long serialVersionUID = RObject.datatypeVersion;
	
	/**
	 * <p>Constructor for RLogicalVector.</p>
	 *
	 * @param primitives an array of {@link int} objects
	 */
	public RLogicalVector(int[] primitives) {
		for (int i=0; i<primitives.length; i++) this.add(new RLogical(primitives[i]));
	}
	/**
	 * <p>Constructor for RLogicalVector.</p>
	 */
	public RLogicalVector() {super();}
	/**
	 * <p>Constructor for RLogicalVector.</p>
	 *
	 * @param length a int
	 */
	public RLogicalVector(int length) {super(length);}
	/**
	 * <p>Constructor for RLogicalVector.</p>
	 *
	 * @param array an array of {@link boolean} objects
	 */
	public RLogicalVector(boolean[] array) {
		super(array.length);
		for (int i=0;i<array.length;i++) this.add(new RLogical(array[i] ? 1 : 0));
	}
	/**
	 * <p>rPrimitive.</p>
	 *
	 * @return an array of {@link int} objects
	 */
	public int[] rPrimitive() {
		return this.stream().mapToInt(ri -> ri.rPrimitive()).toArray();
	}
	
	/** {@inheritDoc} */
	@Override
	public Class<RLogical> getType() {
		return RLogical.class;
	}
	
	/** {@inheritDoc} */
	@Override
	public <X> X accept(RObjectVisitor<X> visitor) {
		X out = visitor.visit(this);
		this.forEach(c -> c.accept(visitor));
		return out;
	}
	
	/**
	 * <p>get.</p>
	 *
	 * @return a {@link java.util.stream.Stream} object
	 */
	@SuppressWarnings("unchecked")
	public Stream<Boolean> get() {
		return this.stream().map(ri -> ri.get());
	}
	
	/**
	 * <p>opt.</p>
	 *
	 * @return a {@link java.util.stream.Stream} object
	 */
	@SuppressWarnings("unchecked")
	public Stream<Optional<Boolean>> opt() {
		return this.stream().map(s -> s.opt());
	}
	
	/** {@inheritDoc} */
	@SuppressWarnings("unchecked")
	@Override
	public RLogicalVector and(RLogical... o) {
		this.addAll(Arrays.asList(o));
		return this;
	}
	/**
	 * <p>empty.</p>
	 *
	 * @return a {@link uk.co.terminological.rjava.types.RLogicalVector} object
	 */
	public static RLogicalVector empty() {
		return new RLogicalVector();
	}
	/** {@inheritDoc} */
	public void fillNA(int length) {this.fill(RLogical.NA, length);}
}
