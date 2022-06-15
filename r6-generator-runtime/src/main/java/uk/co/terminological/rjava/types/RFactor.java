package uk.co.terminological.rjava.types;

import java.util.Optional;

import uk.co.terminological.rjava.RConverter;
import uk.co.terminological.rjava.RDataType;
import uk.co.terminological.rjava.RObjectVisitor;

/**
 * A single value of a factor is largely the same as a String from most perpectives
 * but has an associated index value. This is largely expected to make sense in the context of a 
 * {@link RFactorVector}.
 * @author terminological
 *
 */
@RDataType(
		JavaToR = { 
				"function(jObj) as.character(rJava::.jcall(jObj,returnSig='Ljava/lang/String;',method='rLabel'))",
		}, 
		RtoJava = { 
				"function(rObj) {", 
				"	if (is.na(rObj)) return(rJava::.jnew('~RFACTOR~'))",
				"	if (length(rObj) > 1) stop('input too long')", 
				"	tmp = as.integer(rObj)[[1]]",
				"	tmpLabel = levels(rObj)[[tmp]]",
				"	return(rJava::.jnew('~RFACTOR~',tmp, tmpLabel))", 
				"}"
		}
		//JNIType = "I"
	)
public class RFactor implements RPrimitive {

	private static final long serialVersionUID = RObject.datatypeVersion;
	
	Integer self;
	String label;
	
	static final int NA_VALUE = Integer.MIN_VALUE;
	static final String NA_LABEL = "NA";
	public static final RFactor NA = new RFactor(NA_VALUE, NA_LABEL);
	
	public RFactor(Enum<?> e) {
		// convert zero based to 1 based for conversion to R.
		this(
			e==null ? NA_VALUE : e.ordinal()+1, 
			e==null ? NA_LABEL : e.toString()
		);
	}
	
	public RFactor(int value, String label) {
		if ((int) value == NA_VALUE) {
			this.self = null;
			this.label = NA_LABEL;
		} else {
			this.self = Integer.valueOf((int) value);
			this.label = label;
		}
	}
	
	public RFactor() {
		this(NA_VALUE, NA_LABEL);
	}

	

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + label.hashCode();
		result = prime * result + ((self == null) ? 0 : self.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof RFactor))
			return false;
		RFactor other = (RFactor) obj;
		if (self == null) {
			if (other.self != null)
				return false;
		} else if (!self.equals(other.self))
			return false;
		return label.equals(other.label);
	}
	
	public String rLabel() {return label;}

	public int rValue() {
		return self == null ? NA_VALUE : self.intValue();
	}

	@SuppressWarnings("unchecked")
	@Override
	public <X> X get(Class<X> type) throws ClassCastException {
		if(type.isEnum()) return type.getEnumConstants()[this.self];
		if(type.isInstance(this)) return (X) this;
		if(type.equals(String.class)) return (X) this.label;
		if(type.equals(Integer.class)) return (X) this.self;
		throw new ClassCastException("Can't convert to a "+type.getCanonicalName());
	}
	
	
	public String toString() {return self==null? NA_LABEL : label;}
	
	public String rCode() {
		if (this.isNa()) return "NA";
		return RConverter.rQuote(this.toString(), "'");
	}
	
	@SuppressWarnings("unchecked")
	public String get() {return label.toString();}
	
	@Override
	public <X> X accept(RObjectVisitor<X> visitor) {return visitor.visit(this);}
	public boolean isNa() {return self == null;}

	public static RFactor from(int value, String label) {
		return new RFactor(value,label);
	}

	public static RFactor from(Enum<?> e) {
		return new RFactor(e);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public Optional<String> opt() {return Optional.of(label);}
	
}
