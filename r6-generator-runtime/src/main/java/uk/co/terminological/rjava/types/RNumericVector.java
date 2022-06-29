package uk.co.terminological.rjava.types;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import uk.co.terminological.rjava.RDataType;
import uk.co.terminological.rjava.utils.RObjectVisitor;

/**
 * The vector of numerics is needed to ensure that NA values are correctly handled and
 * allow flexibility of a java List structure for easy manipulation. Factory methods are in {@link RVector}. 
 * @author terminological
 *
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
	
	public RNumericVector(double[] primitives) {
		for (int i=0; i<primitives.length; i++) this.add(new RNumeric(primitives[i]));
	}
	public RNumericVector() {super();}
	public RNumericVector(int length) {super(length);}
	public RNumericVector(List<RNumeric> subList) {
		super(subList);
	}
	public double[] rPrimitive() {
		return this.stream().mapToDouble(ri -> ri.rPrimitive()).toArray();
	}
	
	@Override
	public Class<RNumeric> getType() {
		return RNumeric.class;
	}
	
	@Override
	public <X> X accept(RObjectVisitor<X> visitor) {
		X out = visitor.visit(this);
		this.forEach(c -> c.accept(visitor));
		return out;
	}
	
	@SuppressWarnings("unchecked")
	public Stream<Double> get() {
		return this.stream().map(r -> r.get());
	}
	
	@SuppressWarnings("unchecked")
	public Stream<Optional<Double>> opt() {
		return this.stream().map(s -> s.opt());
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public RNumericVector and(RNumeric... o) {
		this.addAll(Arrays.asList(o));
		return this;
	}
	public static RNumericVector empty() {
		return new RNumericVector();
	}
	public void fillNA(int length) {this.fill(RNumeric.NA, length);}
	
	public double[] javaPrimitive(double naValue) {
		return this.stream().mapToDouble(ri -> ri.javaPrimitive(naValue)).toArray();
	} 
}