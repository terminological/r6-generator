package uk.co.terminological.rjava.types;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import uk.co.terminological.rjava.IncompatibleTypeException;
import uk.co.terminological.rjava.RConverter;

/**
 * <p>RPrimitive interface.</p>
 *
 * @author vp22681
 * @version $Id: $Id
 */
public interface RPrimitive extends RObject {
	
	
	
	/**
	 * Gets the value of this R object as the type X.
	 *
	 * @param <X> the desired type to convert the R data to
	 * @param type the type of X
	 * @return the value as an instance of X
	 * @throws java.lang.ClassCastException the class cast exception is thrown if this R primitiva cannot be coered to X
	 */
	public <X extends Object> X get(Class<X> type) throws ClassCastException;
	/**
	 * <p>get.</p>
	 *
	 * @param <X> a X class
	 * @return a X object
	 */
	public <X extends Object> X get();
	
	
	/**
	 * Gets the value of this R object as the type Optional&lt;X&gt; if possible or else and empty optional.
	 *
	 * @param <X> the desired output Java type
	 * @param type the desired output Java type
	 * @return the optional value
	 */
	public default <X extends Object> Optional<X> opt(Class<X> type) {
		try {
			return Optional.ofNullable(get(type));
		} catch (ClassCastException e) {
			return Optional.empty();
		}
	};
	/**
	 * <p>opt.</p>
	 *
	 * @param <X> a X class
	 * @return a {@link java.util.Optional} object
	 */
	public default <X extends Object> Optional<X> opt() {
		return Optional.ofNullable(get());
	};
	
	/**
	 * NA values of a specific RPrimitive type.
	 *
	 * @param <Y> the generic RPrimitive type
	 * @param clazz the clazz
	 * @return the NA value
	 */
	@SuppressWarnings("unchecked")
	public static <Y extends RPrimitive> Y na(Class<? extends RPrimitive> clazz) {
		if (RCharacter.class.equals(clazz)) return (Y) new RCharacter(); 
		if (RInteger.class.equals(clazz)) return (Y) new RInteger();
		if (RNumeric.class.equals(clazz)) return (Y) new RNumeric();
		if (RFactor.class.equals(clazz)) return (Y) new RFactor();
		if (RLogical.class.equals(clazz)) return (Y) new RLogical();
		if (RDate.class.equals(clazz)) return (Y) new RDate();
		throw new IncompatibleTypeException("No primitive defined for: "+clazz.getCanonicalName());
	}
		
	
	/**
	 * Checks if the value is NA.
	 *
	 * @return true, if isNA
	 */
	public boolean isNa();
	
	/**
	 * <p>of.</p>
	 *
	 * @param o a {@link java.lang.String} object
	 * @return a {@link uk.co.terminological.rjava.types.RCharacter} object
	 */
	public static RCharacter of(String o) {return (RCharacter) RConverter.convert(o);}
	/**
	 * <p>of.</p>
	 *
	 * @param o a {@link java.lang.Double} object
	 * @return a {@link uk.co.terminological.rjava.types.RNumeric} object
	 */
	public static RNumeric of(Double o) {return RConverter.convert(o);}
	/**
	 * <p>of.</p>
	 *
	 * @param o a {@link java.lang.Long} object
	 * @return a {@link uk.co.terminological.rjava.types.RNumeric} object
	 */
	public static RNumeric of(Long o) {return RConverter.convert(o);}
	/**
	 * <p>of.</p>
	 *
	 * @param o a {@link java.math.BigDecimal} object
	 * @return a {@link uk.co.terminological.rjava.types.RNumeric} object
	 */
	public static RNumeric of(BigDecimal o) {return RConverter.convert(o);}
	/**
	 * <p>of.</p>
	 *
	 * @param o a {@link java.lang.Float} object
	 * @return a {@link uk.co.terminological.rjava.types.RNumeric} object
	 */
	public static RNumeric of(Float o) {return RConverter.convert(o);}
	
	/**
	 * <p>of.</p>
	 *
	 * @param o a {@link java.lang.Integer} object
	 * @return a {@link uk.co.terminological.rjava.types.RInteger} object
	 */
	public static RInteger of(Integer o) {return RConverter.convert(o);}
	/**
	 * <p>of.</p>
	 *
	 * @param o a {@link java.lang.Boolean} object
	 * @return a {@link uk.co.terminological.rjava.types.RLogical} object
	 */
	public static RLogical of(Boolean o) {return RConverter.convert(o);}
	/**
	 * <p>of.</p>
	 *
	 * @param o a {@link java.time.LocalDate} object
	 * @return a {@link uk.co.terminological.rjava.types.RDate} object
	 */
	public static RDate of(LocalDate o) {return RConverter.convert(o);}
	
	/**
	 * <p>of.</p>
	 *
	 * @param o a {@link java.lang.Enum} object
	 * @return a {@link uk.co.terminological.rjava.types.RFactor} object
	 */
	public static RFactor of(Enum<?> o) {return RConverter.convert(o);}
	
//	public static RCharacter na(RCharacter v) {return new RCharacter();}
//	public static RInteger na(RInteger v) {return new RInteger();}
//	public static RNumeric na(RNumeric v) {return new RNumeric();}
//	public static RFactor na(RFactor v) {return new RFactor();}
//	public static RLogical na(RLogical v) {return new RLogical();}
//	public static RDate na(RDate v) {return new RDate();}
	
	/**
	 * <p>asCsv.</p>
	 *
	 * @return a {@link java.lang.String} object
	 */
	public default String asCsv() {
		return rCode();
	}

}
