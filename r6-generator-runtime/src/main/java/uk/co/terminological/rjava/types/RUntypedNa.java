package uk.co.terminological.rjava.types;

import uk.co.terminological.rjava.RDataType;
import uk.co.terminological.rjava.utils.RObjectVisitor;

@RDataType(
		JavaToR = { 
				"function(jObj) return(NA)",
		}, 
		RtoJava = { 
				"function(rObj) {", 
				"	return(rJava::.jnew('~RUNTYPEDNA~'))", 
				"}"
		}
		//JNIType = "[C"
	)
public class RUntypedNa implements RPrimitive {

	public static final RUntypedNa NA = new RUntypedNa();
	public static final String NA_LABEL = "NA";

	@Override
	public String rCode() {
		return NA_LABEL;
	}

	@Override
	public <X> X accept(RObjectVisitor<X> visitor) {
		return visitor.visit(this);
	}

	@Override
	public boolean isNa() {
		return true;
	}
	
	@Override
	public <X> X get() {return null;}
	
	@SuppressWarnings("unchecked")
	@Override
	public <X> X get(Class<X> type) throws ClassCastException {
		if (type == RCharacter.class) return (X) RCharacter.NA;
		if (type == RInteger.class) return (X) RInteger.NA;
		if (type == RNumeric.class) return (X) RNumeric.NA;
		if (type == RFactor.class) return (X) RFactor.NA;
		if (type == RDate.class) return (X) RDate.NA;
		if (type == RLogical.class) return (X) RLogical.NA;
		if (type.isInstance(this)) return (X) this;
		throw new ClassCastException("Can't convert to a "+type.getCanonicalName());
	}
	
	public String toString() {return NA_LABEL;}
	
}
