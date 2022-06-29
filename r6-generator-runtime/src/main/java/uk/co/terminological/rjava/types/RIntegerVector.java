package uk.co.terminological.rjava.types;

import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Stream;

import uk.co.terminological.rjava.RDataType;
import uk.co.terminological.rjava.utils.RObjectVisitor;


/**
 * The vector of integers is needed to ensure that NA values are correctly handled and
 * allow flexibility of a java List structure for easy manipulation. Factory methods are in {@link RVector}. 
 * @author terminological
 *
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
	
	public RIntegerVector(int[] primitives) {
		for (int i=0; i<primitives.length; i++) this.add(new RInteger(primitives[i]));
	}
	public RIntegerVector() {super();}
	
	public RIntegerVector(int length) {
		super(length);
	}
	public int[] rPrimitive() {
		return this.stream().mapToInt(ri -> ri.rPrimitive()).toArray();
	}
	
	@Override
	public Class<RInteger> getType() {
		return RInteger.class;
	}
	
	@Override
	public <X> X accept(RObjectVisitor<X> visitor) {
		X out = visitor.visit(this);
		this.forEach(c -> c.accept(visitor));
		return out;
	}
	
	@SuppressWarnings("unchecked")
	public Stream<Integer> get() {
		return this.stream().map(r -> r.get());
	}
	
	@SuppressWarnings("unchecked")
	public Stream<Optional<Integer>> opt() {
		return this.stream().map(s -> s.opt());
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public RIntegerVector and(RInteger... o) {
		this.addAll(Arrays.asList(o));
		return this;
	}
	public static RIntegerVector empty() {
		return new RIntegerVector();
	}
	public void fillNA(int length) {this.fill(RInteger.NA, length);}
	
	public int[] javaPrimitive(int naValue) {
		return this.stream().mapToInt(ri -> ri.javaPrimitive(naValue)).toArray();
	}
}