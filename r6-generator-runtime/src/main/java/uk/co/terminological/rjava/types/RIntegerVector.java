package uk.co.terminological.rjava.types;

import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Stream;

import uk.co.terminological.rjava.RDataType;
import uk.co.terminological.rjava.utils.RObjectVisitor;


/**
 * The vector of integers is needed to ensure that NA values are correctly handled and
 * allow flexibility of a java List structure for easy manipulation. Factory methods are in {@link uk.co.terminological.rjava.types.RVector}.
 *
 * @author terminological
 * @version $Id: $Id
 */
@RDataType(
		JavaToR = {
				"function(jObj) as.integer(rJava::.jcall(jObj,returnSig='[I',method='rPrimitive'))",
		},
		RtoJava = {
				"function(rObj) {",
				"	if (is.null(rObj)) return(rJava::.jnew('~RINTEGERVECTOR~'))",
				"	tmp = as.integer(rObj)",
				"	if (any(rObj!=tmp,na.rm=TRUE)) stop('cannot coerce to integer: ',rObj)",
				"	return(rJava::.jnew('~RINTEGERVECTOR~',rJava::.jarray(tmp)))",
				"}"
		}
		//JNIType = "[I"
	)
public class RIntegerVector extends RVector<RInteger> implements JNIPrimitive {
	
	private static final long serialVersionUID = RObject.datatypeVersion;
	
	/**
	 * <p>Constructor for RIntegerVector.</p>
	 *
	 * @param primitives an array of {@link int} objects
	 */
	public RIntegerVector(int[] primitives) {
		for (int i=0; i<primitives.length; i++) this.add(new RInteger(primitives[i]));
	}
	/**
	 * <p>Constructor for RIntegerVector.</p>
	 */
	public RIntegerVector() {super();}
	
	/**
	 * <p>Constructor for RIntegerVector.</p>
	 *
	 * @param length a int
	 */
	public RIntegerVector(int length) {
		super(length);
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
	public Class<RInteger> getType() {
		return RInteger.class;
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
	public Stream<Integer> get() {
		return this.stream().map(r -> r.get());
	}
	
	/**
	 * <p>opt.</p>
	 *
	 * @return a {@link java.util.stream.Stream} object
	 */
	@SuppressWarnings("unchecked")
	public Stream<Optional<Integer>> opt() {
		return this.stream().map(s -> s.opt());
	}
	
	/** {@inheritDoc} */
	@SuppressWarnings("unchecked")
	@Override
	public RIntegerVector and(RInteger... o) {
		this.addAll(Arrays.asList(o));
		return this;
	}
	/**
	 * <p>empty.</p>
	 *
	 * @return a {@link uk.co.terminological.rjava.types.RIntegerVector} object
	 */
	public static RIntegerVector empty() {
		return new RIntegerVector();
	}
	/** {@inheritDoc} */
	public void fillNA(int length) {this.fill(RInteger.NA, length);}
	
	/**
	 * <p>javaPrimitive.</p>
	 *
	 * @param naValue a int
	 * @return an array of {@link int} objects
	 */
	public int[] javaPrimitive(int naValue) {
		return this.stream().mapToInt(ri -> ri.javaPrimitive(naValue)).toArray();
	}
}
