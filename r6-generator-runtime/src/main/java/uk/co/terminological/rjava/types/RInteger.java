package uk.co.terminological.rjava.types;

import java.util.Optional;

import uk.co.terminological.rjava.RDataType;
import uk.co.terminological.rjava.UnexpectedNaValueException;
import uk.co.terminological.rjava.utils.RObjectVisitor;

/**
 * The integer wrapper handles the translation of R integers to Java Integer.class while the value is passed 
 * through the JNI interface as an unboxed primitive int. This class is largely needed to handle 
 * R NA values, and map them to Java nulls.  If you are not
 * using a structure (such as a list or vector) 
 * and you know the value is not NA you can substitute a primitive int instead of this.
 * @author terminological
 *
 */
@RDataType(
	JavaToR = { 
			"function(jObj) as.integer(rJava::.jcall(jObj,returnSig='I',method='rPrimitive'))",
	}, 
	RtoJava = { 
			"function(rObj) {", 
			"	if (is.na(rObj)) return(rJava::.jnew('~RINTEGER~'))",
			"	if (length(rObj) > 1) stop('input too long')", 
			"	tmp = as.integer(rObj)[[1]]", 
			"	if (rObj[[1]]!=tmp) stop('cannot cast to integer: ',rObj)", 
			"	return(rJava::.jnew('~RINTEGER~',tmp))", 
			"}"
	}
	//JNIType = "I"
)
public class RInteger implements RPrimitive, JNIPrimitive {

	private static final long serialVersionUID = RObject.datatypeVersion;
	
	Integer self = null;
	
	static final int NA_VALUE = Integer.MIN_VALUE;
	public static final RInteger NA = new RInteger(NA_VALUE);
	
	public RInteger(Integer value) {
		if (value == null) {
			self = null;
		} else if (value.intValue() == NA_VALUE) {
			self = null;
		} else {
			self = value;
		}
	}
	
	public RInteger(int value) {
		if ((int) value == NA_VALUE) {
			self = null;
		} else {
			self = Integer.valueOf((int) value);
		}
	}
	
	public RInteger() {
		this(NA_VALUE);
	}
	
	@Override
	public int hashCode() {return self.hashCode();}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		RInteger other = (RInteger) obj;
		if (self == null) {
			if (other.self != null)
				return false;
		} else if (!self.equals(other.self))
			return false;
		return true;
	}

	public int rPrimitive() {
		return self==null ? NA_VALUE : self.intValue();
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public <X> X get(Class<X> type) throws ClassCastException {
		if (type.isInstance(this)) return (X) this;
		if (type.isInstance(self)) return (X) self;
		throw new ClassCastException("Can't convert to a "+type.getCanonicalName());
	}

	public String toString() {return this.isNa()?"NA":self.toString();}
	
	public String rCode() {return this.isNa()?"NA":this.toString()+"L";}
	
	@SuppressWarnings("unchecked")
	public Integer get() {return self;}
	
	@Override
	public <X> X accept(RObjectVisitor<X> visitor) {return visitor.visit(this);}
	public boolean isNa() {return self == null;}

	public static RInteger from(int value) {
		return new RInteger(value);
	}

	public int javaPrimitive(int naValue) {
		return this.self == null ? naValue : this.self;
	}
	
	public int javaPrimitive() throws UnexpectedNaValueException {
		if (this.self == null) throw new UnexpectedNaValueException();
		return this.self.intValue();
	}
	
	public static RInteger MIN_VALUE = RInteger.from(Integer.MIN_VALUE+1);
	public static RInteger MAX_VALUE = RInteger.from(Integer.MAX_VALUE);
	
	@SuppressWarnings("unchecked")
	@Override
	public Optional<Integer> opt() {return opt(Integer.class);}
	
}
