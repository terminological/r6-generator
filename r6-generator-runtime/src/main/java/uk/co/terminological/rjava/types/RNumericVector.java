package uk.co.terminological.rjava.types;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import uk.co.terminological.rjava.RDataType;
import uk.co.terminological.rjava.utils.RObjectVisitor;

/**
 * The vector of numerics is needed to ensure that NA values are correctly handled and
 * allow flexibility of a java List structure for easy manipulation. Factory methods are in {@link uk.co.terminological.rjava.types.RVector}.
 *
 * @author terminological
 * @version $Id: $Id
 */
@RDataType(
		JavaToR = {
				"function(jObj) as.numeric(rJava::.jcall(jObj,returnSig='[D',method='rPrimitive'))",
		},
		RtoJava = {
				"function(rObj) {",
				"	if (is.null(rObj)) return(rJava::.jnew('~RNUMERICVECTOR~'))",
				"	if (!is.numeric(rObj)) stop('expected a numeric')",
				"	tmp = as.numeric(rObj)",
				"	return(rJava::.jnew('~RNUMERICVECTOR~',rJava::.jarray(tmp)))",
				"}"
		}//,
		//JNIType = "[D"
	)
public class RNumericVector extends RVector<RNumeric> implements JNIPrimitive {
	
	private static final long serialVersionUID = RObject.datatypeVersion;
	
	/**
	 * <p>Constructor for RNumericVector.</p>
	 *
	 * @param primitives an array of {@link double} objects
	 */
	public RNumericVector(double[] primitives) {
		for (int i=0; i<primitives.length; i++) this.add(new RNumeric(primitives[i]));
	}
	/**
	 * <p>Constructor for RNumericVector.</p>
	 */
	public RNumericVector() {super();}
	/**
	 * <p>Constructor for RNumericVector.</p>
	 *
	 * @param length a int
	 */
	public RNumericVector(int length) {super(length);}
	/**
	 * <p>Constructor for RNumericVector.</p>
	 *
	 * @param subList a {@link java.util.List} object
	 */
	public RNumericVector(List<RNumeric> subList) {
		super(subList);
	}
	/**
	 * <p>rPrimitive.</p>
	 *
	 * @return an array of {@link double} objects
	 */
	public double[] rPrimitive() {
		return this.stream().mapToDouble(ri -> ri.rPrimitive()).toArray();
	}
	
	/** {@inheritDoc} */
	@Override
	public Class<RNumeric> getType() {
		return RNumeric.class;
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
	public Stream<Double> get() {
		return this.stream().map(r -> r.get());
	}
	
	/**
	 * <p>opt.</p>
	 *
	 * @return a {@link java.util.stream.Stream} object
	 */
	@SuppressWarnings("unchecked")
	public Stream<Optional<Double>> opt() {
		return this.stream().map(s -> s.opt());
	}
	
	/** {@inheritDoc} */
	@SuppressWarnings("unchecked")
	@Override
	public RNumericVector and(RNumeric... o) {
		this.addAll(Arrays.asList(o));
		return this;
	}
	/**
	 * <p>empty.</p>
	 *
	 * @return a {@link uk.co.terminological.rjava.types.RNumericVector} object
	 */
	public static RNumericVector empty() {
		return new RNumericVector();
	}
	/** {@inheritDoc} */
	public void fillNA(int length) {this.fill(RNumeric.NA, length);}
	
	/**
	 * <p>javaPrimitive.</p>
	 *
	 * @param naValue a double
	 * @return an array of {@link double} objects
	 */
	public double[] javaPrimitive(double naValue) {
		return this.stream().mapToDouble(ri -> ri.javaPrimitive(naValue)).toArray();
	} 
}
