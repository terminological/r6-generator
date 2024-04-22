package uk.co.terminological.rjava.types;

import uk.co.terminological.rjava.RDataType;
import uk.co.terminological.rjava.utils.RObjectVisitor;

/**
 * <p>RUntypedNa class.</p>
 *
 * @author vp22681
 * @version $Id: $Id
 */
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

	/** Constant <code>NA</code> */
	public static final RUntypedNa NA = new RUntypedNa();
	/** Constant <code>NA_LABEL="NA"</code> */
	public static final String NA_LABEL = "NA";

	/** {@inheritDoc} */
	@Override
	public String rCode() {
		return NA_LABEL;
	}

	/** {@inheritDoc} */
	@Override
	public <X> X accept(RObjectVisitor<X> visitor) {
		return visitor.visit(this);
	}

	/** {@inheritDoc} */
	@Override
	public boolean isNa() {
		return true;
	}
	
	/** {@inheritDoc} */
	@Override
	public <X> X get() {return null;}
	
	/** {@inheritDoc} */
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
	
	/**
	 * <p>toString.</p>
	 *
	 * @return a {@link java.lang.String} object
	 */
	public String toString() {return NA_LABEL;}
	
}
