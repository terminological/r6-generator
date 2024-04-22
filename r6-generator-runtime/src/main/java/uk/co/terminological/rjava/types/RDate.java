package uk.co.terminological.rjava.types;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Optional;

import uk.co.terminological.rjava.RDataType;
import uk.co.terminological.rjava.utils.RObjectVisitor;

/**
 * A java representation of the R base Date class (will also be used for POSIXt classes)
 *
 * @author terminological
 * @version $Id: $Id
 */
@RDataType(
		JavaToR = {
				"function(jObj) as.Date(rJava::.jcall(jObj,returnSig='Ljava/lang/String;',method='rPrimitive'),'%Y-%m-%d')",
		},
		RtoJava = {
				"function(rObj) {",
				"	if (is.na(rObj)) return(rJava::.jnew('~RDATE~'))",
				"	if (length(rObj) > 1) stop('input too long')",
				"   if (rObj<'0001-01-01') message('dates smaller than 0001-01-01 will be converted to NA')",
				"	tmp = format(rObj,format='%C%y-%m-%d')[[1]]",
				"	return(rJava::.jnew('~RDATE~',tmp))",
				"}"
		}
		//JNIType = "D"
	)
public class RDate implements RPrimitive, JNIPrimitive  {

	private static final long serialVersionUID = RObject.datatypeVersion;
	
	static final String NA_VALUE = null;
	/** Constant <code>NA</code> */
	public static final RDate NA = new RDate(NA_VALUE);
	
	LocalDate self;
	
	static DateTimeFormatter isoformatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
	static DateTimeFormatter rformatter = DateTimeFormatter.ofPattern("yyy-MM-dd");
		
	/**
	 * <p>get.</p>
	 *
	 * @return a {@link java.time.LocalDate} object
	 */
	@SuppressWarnings("unchecked")
	public LocalDate get() {
		return self;
	}
	
	/**
	 * <p>Constructor for RDate.</p>
	 *
	 * @param value a {@link java.lang.String} object
	 */
	public RDate(String value) {
		if (value == null) self=null;
		else {
			if (value.startsWith("-")) {
				self = null;
			} else {
				try {
					self = LocalDate.parse(value,rformatter);
				} catch (DateTimeParseException e) {
					self = null;
				}
			}
		}
	}
	
	/**
	 * <p>Constructor for RDate.</p>
	 */
	public RDate() {
		self = null;
	}
	
	/**
	 * <p>Constructor for RDate.</p>
	 *
	 * @param boxed a {@link java.time.LocalDate} object
	 */
	public RDate(LocalDate boxed) {
		self = boxed;
	}
	
	/**
	 * <p>from.</p>
	 *
	 * @param s a {@link java.lang.String} object
	 * @return a {@link uk.co.terminological.rjava.types.RDate} object
	 */
	public static RDate from(String s) {
		return new RDate(s);
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
		RDate other = (RDate) obj;
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
	public String rPrimitive() {return self == null ? "NA" : self.format(rformatter);} 
	
	/** {@inheritDoc} */
	@SuppressWarnings("unchecked")
	@Override
	public <X> Optional<X> opt(Class<X> type) {
		if (type.isInstance(this)) return Optional.ofNullable((X) this);
		if (type.isInstance(self)) return Optional.ofNullable((X) self);
		return Optional.empty();
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
	public String toString() {return self==null ? "NA" : self.format(isoformatter);}
	
	/** {@inheritDoc} */
	@Override
	public String rCode() {
		return this.isNa() ? "NA": "as.Date('"+self.format(rformatter)+"','%Y-%m-%d')";
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
	 * <p>asCsv.</p>
	 *
	 * @return a {@link java.lang.String} object
	 */
	public String asCsv() {
		return toString();
	}
	
	/** {@inheritDoc} */
	@SuppressWarnings("unchecked")
	@Override
	public Optional<LocalDate> opt() {return opt(LocalDate.class);}
	
}
