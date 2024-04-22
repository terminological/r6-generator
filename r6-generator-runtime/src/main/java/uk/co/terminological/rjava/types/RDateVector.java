package uk.co.terminological.rjava.types;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import uk.co.terminological.rjava.RDataType;
import uk.co.terminological.rjava.utils.RObjectVisitor;

/**
 * Java wrapper for R vector of dates. When transferred between R and Java this uses a string format for the date.
 * Factory methods are in {@link uk.co.terminological.rjava.types.RVector}.
 *
 * @author terminological
 * @version $Id: $Id
 */
@RDataType(
		JavaToR = {
				"function(jObj) as.Date(rJava::.jcall(jObj,returnSig='[Ljava/lang/String;',method='rPrimitive'),'%Y-%m-%d')",
		},
		RtoJava = {
				"function(rObj) {",
				"	if (is.null(rObj)) return(rJava::.new('~RDATEVECTOR~'))",
				"	if (any(na.omit(rObj)<'0001-01-01')) message('dates smaller than 0001-01-01 will be converted to NA')",
				"	tmp = format(rObj,format='%C%y-%m-%d')",
				"	return(rJava::.jnew('~RDATEVECTOR~',rJava::.jarray(tmp)))",
				"}"
		}
		//JNIType = "[I"
	)
public class RDateVector extends RVector<RDate> implements JNIPrimitive {
	
	private static final long serialVersionUID = RObject.datatypeVersion;
	
	/**
	 * <p>Constructor for RDateVector.</p>
	 *
	 * @param primitives an array of {@link java.lang.String} objects
	 */
	public RDateVector(String[] primitives) {
		super(primitives.length);
		for (int i=0; i<primitives.length; i++) this.add(new RDate(primitives[i]));
	}
	/**
	 * <p>Constructor for RDateVector.</p>
	 */
	public RDateVector() {super();}
	/**
	 * <p>Constructor for RDateVector.</p>
	 *
	 * @param length a int
	 */
	public RDateVector(int length) {super(length);}
	
	/**
	 * <p>rPrimitive.</p>
	 *
	 * @return an array of {@link java.lang.String} objects
	 */
	public String[] rPrimitive() {
		return this.stream().map(ri -> ri.rPrimitive()).collect(Collectors.toList()).toArray(new String[] {});
	}
	
	/** {@inheritDoc} */
	@Override
	public Class<RDate> getType() {
		return RDate.class;
	}
	
	/**
	 * <p>rCode.</p>
	 *
	 * @return a {@link java.lang.String} object
	 */
	public String rCode() {
		return "as.Date(c("+this.stream().map(s -> s.isNa() ? "NA" : ("'"+s.rPrimitive()+"'")).collect(Collectors.joining(", "))+"),'%Y-%m-%d')";
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
	public Stream<LocalDate> get() {
		return this.stream().map(ri -> ri.get());
	}
	
	/**
	 * <p>opt.</p>
	 *
	 * @return a {@link java.util.stream.Stream} object
	 */
	@SuppressWarnings("unchecked")
	public Stream<Optional<LocalDate>> opt() {
		return this.stream().map(s -> s.opt());
	}
	
	/** {@inheritDoc} */
	@SuppressWarnings("unchecked")
	@Override
	public RDateVector and(RDate... o) {
		this.addAll(Arrays.asList(o));
		return this;
	}
	/**
	 * <p>empty.</p>
	 *
	 * @return a {@link uk.co.terminological.rjava.types.RDateVector} object
	 */
	public static RDateVector empty() {
		return new RDateVector();
	}
	
	/** {@inheritDoc} */
	public void fillNA(int length) {this.fill(RDate.NA, length);}
}
