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
 * Factory methods are in {@link RVector}.
 * @author terminological
 *
 */
@RDataType(
		JavaToR = { 
				"function(jObj) as.Date(rJava::.jcall(jObj,returnSig='[Ljava/lang/String;',method='rPrimitive'),'%Y-%m-%d')",
		}, 
		RtoJava = { 
				"function(rObj) {", 
				"	if (is.null(rObj)) return(rJava::.new('~RDATEVECTOR~'))",
				"	if (any(na.omit(rObj)<'0001-01-01')) message('dates smaller than 0001-01-01 will be converted to NA')",
				"	tmp = as.character(rObj,format='%C%y-%m-%d')",
				"	return(rJava::.jnew('~RDATEVECTOR~',rJava::.jarray(tmp)))", 
				"}"
		}
		//JNIType = "[I"
	)
public class RDateVector extends RVector<RDate> implements JNIPrimitive {
	
	private static final long serialVersionUID = RObject.datatypeVersion;
	
	public RDateVector(String[] primitives) {
		super(primitives.length);
		for (int i=0; i<primitives.length; i++) this.add(new RDate(primitives[i]));
	}
	public RDateVector() {super();}
	public RDateVector(int length) {super(length);}
	
	public String[] rPrimitive() {
		return this.stream().map(ri -> ri.rPrimitive()).collect(Collectors.toList()).toArray(new String[] {});
	}
	
	@Override
	public Class<RDate> getType() {
		return RDate.class;
	}
	
	public String rCode() {
		return "as.Date(c("+this.stream().map(s -> s.isNa() ? "NA" : ("'"+s.rPrimitive()+"'")).collect(Collectors.joining(", "))+"),'%Y-%m-%d')";
	}
	
	@Override
	public <X> X accept(RObjectVisitor<X> visitor) {
		X out = visitor.visit(this);
		this.forEach(c -> c.accept(visitor));
		return out;
	}
	
	@SuppressWarnings("unchecked")
	public Stream<LocalDate> get() {
		return this.stream().map(ri -> ri.get());
	}
	
	@SuppressWarnings("unchecked")
	public Stream<Optional<LocalDate>> opt() {
		return this.stream().map(s -> s.opt());
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public RDateVector and(RDate... o) {
		this.addAll(Arrays.asList(o));
		return this;
	}
	public static RDateVector empty() {
		return new RDateVector();
	}
	
	public void fillNA(int length) {this.fill(RDate.NA, length);}
}