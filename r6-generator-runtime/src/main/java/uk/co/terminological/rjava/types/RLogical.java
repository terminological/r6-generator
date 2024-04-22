package uk.co.terminological.rjava.types;

import java.util.Optional;

import uk.co.terminological.rjava.RDataType;
import uk.co.terminological.rjava.utils.RObjectVisitor;


/**
 * The logical wrapper handles the translation of R logical to Java Boolean.class while the value is passed
 * through the JNI interface as an unboxed primitive int. This class is largely needed to handle
 * R NA values, and map them to Java nulls.  If you are not
 * using a structure (such as a list or vector)
 * and you know the value is not NA you can substitute a primitive boolean instead of this.
 *
 * @author vp22681
 * @version $Id: $Id
 */
@RDataType(
		JavaToR = {
				"function(jObj) as.logical(rJava::.jcall(jObj,returnSig='I',method='rPrimitive'))",
		},
		RtoJava = {
				"function(rObj) {",
				"	if (is.na(rObj)) return(rJava::.jnew('~RLOGICAL~'))",
				"	if (length(rObj) > 1) stop('input too long')",
				"	if (!is.logical(rObj)) stop('expected a logical')",
				"	tmp = as.integer(rObj)[[1]]",
				"	return(rJava::.jnew('~RLOGICAL~',tmp))",
				"}"
		}//,
		//JNIType = "I"
	)
public class RLogical implements RPrimitive, JNIPrimitive {
	
	private static final long serialVersionUID = RObject.datatypeVersion;
	
	Boolean self;
	
	static final int NA_VALUE = Integer.MIN_VALUE;
	/** Constant <code>NA</code> */
	
	public static final RLogical NA = new RLogical(NA_VALUE);
	public static final RLogical TRUE = new RLogical(Boolean.TRUE);
	public static final RLogical FALSE = new RLogical(Boolean.FALSE);
	
	
	/**
	 * <p>Constructor for RLogical.</p>
	 *
	 * @param value a {@link java.lang.Boolean} object
	 */
	public RLogical(Boolean value) {
		self = value;
	}
	
	/**
	 * <p>Constructor for RLogical.</p>
	 *
	 * @param value a {@link java.lang.Integer} object
	 */
	public RLogical(Integer value) {
		if (value == null || value == NA_VALUE) self=null;
		if(value != 0) {
			self = Boolean.TRUE;
		} else {
			self = Boolean.FALSE;
		}
	}
	
	/**
	 * <p>Constructor for RLogical.</p>
	 *
	 * @param value a int
	 */
	public RLogical(int value) {
		if (value == NA_VALUE) {
			self = null;
		} else {
			if(value != 0) {
				self = Boolean.TRUE;
			} else {
				self = Boolean.FALSE;
			}
		}
	}
	
	/**
	 * <p>Constructor for RLogical.</p>
	 */
	public RLogical() {
		this(NA_VALUE);
	}

	/** {@inheritDoc} */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((self == null) ? 0 : self.hashCode());
		return result;
	}

	/** {@inheritDoc} */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		RLogical other = (RLogical) obj;
		if (self == null) {
			if (other.self != null)
				return false;
		} else if (!self.equals(other.self))
			return false;
		return true;
	}
	
	/**
	 * <p>rPrimitive.</p>
	 *
	 * @return a int
	 */
	public int rPrimitive() {
		return this.isNa() ? NA_VALUE : (self.booleanValue() ? 1 : 0);
	}

	/** {@inheritDoc} */
	@SuppressWarnings("unchecked")
	@Override
	public <X> X get(Class<X> type) throws ClassCastException {
		if (type.isInstance(this)) return (X) this;
		if (type.isInstance(self)) return (X) self;
		throw new ClassCastException("Can't convert to a "+type.getCanonicalName());
	}
	
	/**
	 * <p>toString.</p>
	 *
	 * @return a {@link java.lang.String} object
	 */
	public String toString() {return this.isNa()?"NA":self.toString();}
	
	/**
	 * <p>rCode.</p>
	 *
	 * @return a {@link java.lang.String} object
	 */
	public String rCode() { return this.isNa()?"NA":this.toString().toUpperCase(); }
	
	/**
	 * <p>get.</p>
	 *
	 * @return a {@link java.lang.Boolean} object
	 */
	@SuppressWarnings("unchecked")
	public Boolean get() {return self;}
	
	/** {@inheritDoc} */
	@Override
	public <X> X accept(RObjectVisitor<X> visitor) {return visitor.visit(this);}
	/**
	 * <p>isNa.</p>
	 *
	 * @return a boolean
	 */
	public boolean isNa() {return self == null;}

	/**
	 * <p>from.</p>
	 *
	 * @param value a int
	 * @return a {@link uk.co.terminological.rjava.types.RLogical} object
	 */
	public static RLogical from(int value) {
		return new RLogical(value);
	}
	
	/** {@inheritDoc} */
	@SuppressWarnings("unchecked")
	@Override
	public Optional<Boolean> opt() {return opt(Boolean.class);}
	
}
