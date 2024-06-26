package uk.co.terminological.rjava.types;

import java.util.Optional;

import uk.co.terminological.rjava.RConverter;
import uk.co.terminological.rjava.RDataType;
import uk.co.terminological.rjava.utils.RObjectVisitor;

/**
 * A wrapper for character classes in R. This is not very different from a raw String. If you are not
 * using a structure (such as a list or vector) you can substitute this for a raw String.
 *
 * @author terminological
 * @version $Id: $Id
 */
@RDataType(
		JavaToR = {
				"function(jObj) as.character(rJava::.jcall(jObj,returnSig='Ljava/lang/String;',method='rPrimitive'))",
		},
		RtoJava = {
				"function(rObj) {",
				"	if (is.na(rObj)) return(rJava::.jnew('~RCHARACTER~'))",
				"	tmp = as.character(rObj)[[1]]",
				"	return(rJava::.jnew('~RCHARACTER~',tmp))",
				"}"
		}
		//JNIType = "[C"
	)
public class RCharacter implements RPrimitive, CharSequence, JNIPrimitive {

	private static final long serialVersionUID = RObject.datatypeVersion;
	
	static final String NA_VALUE = null;
	/** Constant <code>NA</code> */
	public static final RCharacter NA = new RCharacter(NA_VALUE);
	
	String self;
	
	/**
	 * <p>Constructor for RCharacter.</p>
	 *
	 * @param value a {@link java.lang.String} object
	 */
	public RCharacter(String value) {
		self = value;
	}
	
	/**
	 * <p>Constructor for RCharacter.</p>
	 */
	public RCharacter() {
		self = null;
	}
	
	/** {@inheritDoc} */
	@Override
	public int length() {
		return self==null ? 0 : self.length();
	}

	/** {@inheritDoc} */
	@Override
	public char charAt(int index) {
		if (self==null) throw new StringIndexOutOfBoundsException(index);
		return self.charAt(index);
	}

	/** {@inheritDoc} */
	@Override
	public CharSequence subSequence(int start, int end) {
		if (self==null) throw new StringIndexOutOfBoundsException(start);
		return self.subSequence(start, end);
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
		RCharacter other = (RCharacter) obj;
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
	 * @return a {@link java.lang.String} object
	 */
	public String rPrimitive() {return self;} //.toCharArray();}

	/** {@inheritDoc} */
	@SuppressWarnings("unchecked")
	@Override
	public <X> Optional<X> opt(Class<X> type) {
		if (type.isInstance(this)) return (Optional<X>) Optional.ofNullable((X) this);
		if (type.isInstance(self)) return (Optional<X>) Optional.ofNullable((X) self);
		return Optional.empty();
	}
	
	/**
	 * <p>toString.</p>
	 *
	 * @return a {@link java.lang.String} object
	 */
	public String toString() {return self==null?"NA":self.toString();}

	/** {@inheritDoc} */
	@Override
	public String rCode() {
		return this.isNa()?"NA": RConverter.rQuote(self,"'");
	}

	/** {@inheritDoc} */
	@SuppressWarnings("unchecked")
	@Override
	public String get() {return self;}
	
	/** {@inheritDoc} */
	@SuppressWarnings("unchecked")
	@Override
	public Optional<String> opt() {return opt(String.class);}
	
	/** {@inheritDoc} */
	@SuppressWarnings("unchecked")
	@Override
	public <X> X get(Class<X> type) throws ClassCastException {
		if (type.isInstance(this)) return (X) this;
		if (type.isInstance(self)) return (X) self;
		throw new ClassCastException("Can't convert to a "+type.getCanonicalName());
	}
	
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
	 * @param value a {@link java.lang.String} object
	 * @return a {@link uk.co.terminological.rjava.types.RCharacter} object
	 */
	public static RCharacter from(String value) {
		return new RCharacter(value);
	}

	
	

}
