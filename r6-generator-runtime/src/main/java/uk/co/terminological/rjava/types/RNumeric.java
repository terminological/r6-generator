package uk.co.terminological.rjava.types;

import java.math.BigDecimal;
import java.util.Optional;

import uk.co.terminological.rjava.RDataType;
import uk.co.terminological.rjava.RObjectVisitor;
import uk.co.terminological.rjava.UnexpectedNaValueException;

/**
 * The numeric wrapper handles the translation of R numerics to Java Double.class while the value is passed 
 * through the JNI interface as an unboxed primitive double. This class is largely needed to handle 
 * R NA values, and map them to Java nulls.  If you are not
 * using a structure (such as a list or vector) 
 * and you know the value is not NA you can substitute a primitive double instead of this.
 * This is also the target datatype for java Long, Float, and BigDecimal, all of which will end up
 * as R numerics. Round tripping a java float is not supported at present, and Api classes that attempt
 * to use floats will not compile, but on the java side the conversion is handled by this class.
 * @author terminological
 *
 */
@RDataType(
		JavaToR = { 
				"function(jObj) as.numeric(rJava::.jcall(jObj,returnSig='D',method='rPrimitive'))",
		}, 
		RtoJava = { 
				"function(rObj) {", 
				"	if (length(rObj) > 1) stop('input too long')",
				"	if (!is.numeric(rObj)) stop('expected a numeric')",
				"	tmp = as.numeric(rObj)[[1]]",
				"	return(rJava::.jnew('~RNUMERIC~',tmp))", 
				"}"
		}//,
		//JNIType = "D"
	)
public class RNumeric implements RPrimitive, JNIPrimitive  {

	private static final long serialVersionUID = RObject.datatypeVersion;
	
	Double self;
	
	private static final long NA_VALUE_LONG = 0x7ff00000000007a2L;
	static final double NA_VALUE = Double.longBitsToDouble(NA_VALUE_LONG);
	public static final RNumeric NA = new RNumeric(NA_VALUE);
	
	//NaN 7ff8000000000000
	//Infinity 7ff0000000000000
	//-Infinity fff0000000000000
	//private static final double NA_DOUBLE = Double.longBitsToDouble(9221120237041090560L);
	
	@SuppressWarnings("unchecked")
	public Double get() {
		return self;
	}
	
	public double getOrNaN() {
		return self == null ? NA_VALUE : self;
	}
	
	public RNumeric(Double value) {
		if (Double.doubleToRawLongBits(value.doubleValue()) == NA_VALUE_LONG) self = null;
		else self = (Double) value;
	}
	
	public RNumeric(Long value) {
		if (value == null) self=null;
		else self = Double.valueOf(value);
	}
	
	public RNumeric(Float value) {
		if (value == null) self=null;
		else self = Double.valueOf(value);
	}
	
	public RNumeric(BigDecimal value) {
		if (value == null) self=null;
		else self = value.doubleValue();
		
	}
	
	public RNumeric(double value) {
		if (Double.doubleToRawLongBits(value) == NA_VALUE_LONG) self = null;
		else self = (Double) value;
	}
	
	public RNumeric() {
		self = null;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((self == null) ? 0 : self.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		RNumeric other = (RNumeric) obj;
		if (self == null) {
			if (other.self != null)
				return false;
		} else if (!self.equals(other.self))
			return false;
		return true;
	}
	
	public double rPrimitive() {return self == null ? NA_VALUE : self.doubleValue();} 
	
	
	
	/* (non-Javadoc)
	 * @see uk.co.terminological.rjava.types.RPrimitive#get(java.lang.Class)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public <X> X get(Class<X> type) throws ClassCastException {
		if (type.isInstance(this)) return (X) this;
		if (type.isInstance(self)) return (X) self;
		if (type.equals(Long.class)) return (X) (Long) (self == null ? null : self.longValue());
		if (type.equals(Double.class)) return (X) (Double) (self == null ? null : self.doubleValue());
		if (type.equals(Float.class)) return (X) (Float) (self == null ? null : self.floatValue());
		if (type.equals(BigDecimal.class)) return (X) (self == null ? null : BigDecimal.valueOf(self.doubleValue()));
		throw new ClassCastException("Can't convert to a "+type.getCanonicalName());
	}
	
	public String toString() {return self==null?"NA":self.toString();}
	
	public String rCode() {
		if (this.isNa()) return "NA";
		if (self==Double.POSITIVE_INFINITY) return "Inf";
		if (self==Double.NEGATIVE_INFINITY) return "-Inf";
		if (self==Double.NaN) return "NaN";
		return self.toString();
	}
	
	@Override
	public <X> X accept(RObjectVisitor<X> visitor) {return visitor.visit(this);}
	public boolean isNa() {return self == null;}

	public static RNumeric from(double value) {
		return new RNumeric(value);
	}

	public double javaPrimitive(double naValue) {
		return this.self == null ? naValue : this.self;
	}

	public double javaPrimitive() throws UnexpectedNaValueException {
		if (this.self == null) throw new UnexpectedNaValueException();
		return this.self.doubleValue();
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public Optional<Double> opt() {return opt(Double.class);}
	
	
	
}
