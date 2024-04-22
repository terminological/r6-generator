package uk.co.terminological.rjava.types;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.co.terminological.rjava.IncompatibleTypeException;
import uk.co.terminological.rjava.RConverter;

/**
 * <p>Abstract RVector class.</p>
 *
 * @author vp22681
 * @version $Id: $Id
 */
public abstract class RVector<X extends RPrimitive> extends ArrayList<X> implements RObject {

	private static final long serialVersionUID = RObject.datatypeVersion;
	private static Logger log = LoggerFactory.getLogger(RVector.class);
	
	/**
	 * <p>Constructor for RVector.</p>
	 */
	public RVector() {
		super();
	}
	
	/**
	 * <p>Constructor for RVector.</p>
	 *
	 * @param length a int
	 */
	public RVector(int length) {
		super(length);
	}
	
	/**
	 * <p>Constructor for RVector.</p>
	 *
	 * @param subList a {@link java.util.List} object
	 */
	public RVector(List<X> subList) {
		super(subList);
	}

	/**
	 * <p>ofNA.</p>
	 *
	 * @param clazz a {@link java.lang.Class} object
	 * @param length a int
	 * @param <Y> a Y class
	 * @return a {@link uk.co.terminological.rjava.types.RVector} object
	 */
	@SuppressWarnings("unchecked")
	public static <Y extends RPrimitive> RVector<Y> ofNA(Class<Y> clazz, int length) {
		if (RCharacter.class.equals(clazz)) return (RVector<Y>) new RCharacterVector(length).fill(RCharacter.NA, length); 
		if (RInteger.class.equals(clazz)) return (RVector<Y>) new RIntegerVector(length).fill(RInteger.NA, length);
		if (RNumeric.class.equals(clazz)) return (RVector<Y>) new RNumericVector(length).fill(RNumeric.NA, length);
		if (RFactor.class.equals(clazz)) return (RVector<Y>) new RFactorVector(length).fill(RFactor.NA, length);
		if (RLogical.class.equals(clazz)) return (RVector<Y>) new RLogicalVector(length).fill(RLogical.NA, length);
		if (RDate.class.equals(clazz)) return (RVector<Y>) new RDateVector(length).fill(RDate.NA, length);
		if (RUntypedNa.class.equals(clazz)) return (RVector<Y>) new RUntypedNaVector(length).fill(RUntypedNa.NA, length);
		throw new IncompatibleTypeException("No vector defined for: "+clazz.getCanonicalName());
		
	}
	
	/**
	 * <p>empty.</p>
	 *
	 * @param clazz a {@link java.lang.Class} object
	 * @param <Y> a Y class
	 * @return a {@link uk.co.terminological.rjava.types.RVector} object
	 */
	@SuppressWarnings("unchecked")
	public static <Y extends RPrimitive> RVector<Y> empty(Class<Y> clazz) {
		if (RCharacter.class.equals(clazz)) return (RVector<Y>) new RCharacterVector(); 
		if (RInteger.class.equals(clazz)) return (RVector<Y>) new RIntegerVector();
		if (RNumeric.class.equals(clazz)) return (RVector<Y>) new RNumericVector();
		if (RFactor.class.equals(clazz)) return (RVector<Y>) new RFactorVector();
		if (RLogical.class.equals(clazz)) return (RVector<Y>) new RLogicalVector();
		if (RDate.class.equals(clazz)) return (RVector<Y>) new RDateVector();
		if (RUntypedNa.class.equals(clazz)) return (RVector<Y>) new RUntypedNaVector();
		throw new IncompatibleTypeException("No vector defined for: "+clazz.getCanonicalName());
	}
	
	/**
	 * <p>rep.</p>
	 *
	 * @param primitive a {@link uk.co.terminological.rjava.types.RCharacter} object
	 * @param length a int
	 * @return a {@link uk.co.terminological.rjava.types.RCharacterVector} object
	 */
	public static RCharacterVector rep(RCharacter primitive, int length) {return (RCharacterVector) new RCharacterVector(length).fill(primitive, length);}
	/**
	 * <p>rep.</p>
	 *
	 * @param primitive a {@link uk.co.terminological.rjava.types.RNumeric} object
	 * @param length a int
	 * @return a {@link uk.co.terminological.rjava.types.RNumericVector} object
	 */
	public static RNumericVector rep(RNumeric primitive, int length) {return (RNumericVector) new RNumericVector(length).fill(primitive, length);}
	/**
	 * <p>rep.</p>
	 *
	 * @param primitive a {@link uk.co.terminological.rjava.types.RInteger} object
	 * @param length a int
	 * @return a {@link uk.co.terminological.rjava.types.RIntegerVector} object
	 */
	public static RIntegerVector rep(RInteger primitive, int length) {return (RIntegerVector) new RIntegerVector(length).fill(primitive, length);}
	/**
	 * <p>rep.</p>
	 *
	 * @param primitive a {@link uk.co.terminological.rjava.types.RFactor} object
	 * @param length a int
	 * @return a {@link uk.co.terminological.rjava.types.RFactorVector} object
	 */
	public static RFactorVector rep(RFactor primitive, int length) {return (RFactorVector) new RFactorVector(length).fill(primitive, length);}
	/**
	 * <p>rep.</p>
	 *
	 * @param primitive a {@link uk.co.terminological.rjava.types.RLogical} object
	 * @param length a int
	 * @return a {@link uk.co.terminological.rjava.types.RLogicalVector} object
	 */
	public static RLogicalVector rep(RLogical primitive, int length) {return (RLogicalVector) new RLogicalVector(length).fill(primitive, length);}
	/**
	 * <p>rep.</p>
	 *
	 * @param primitive a {@link uk.co.terminological.rjava.types.RDate} object
	 * @param length a int
	 * @return a {@link uk.co.terminological.rjava.types.RDateVector} object
	 */
	public static RDateVector rep(RDate primitive, int length) {return (RDateVector) new RDateVector(length).fill(primitive, length);}
	/**
	 * <p>rep.</p>
	 *
	 * @param primitive a {@link uk.co.terminological.rjava.types.RUntypedNa} object
	 * @param length a int
	 * @return a {@link uk.co.terminological.rjava.types.RUntypedNaVector} object
	 */
	public static RUntypedNaVector rep(RUntypedNa primitive, int length) {return (RUntypedNaVector) new RUntypedNaVector(length).fill(primitive, length);}
	
	/**
	 * <p>rep.</p>
	 *
	 * @param v a Y object
	 * @param rows a int
	 * @param <Y> a Y class
	 * @return a {@link uk.co.terminological.rjava.types.RVector} object
	 */
	@SuppressWarnings("unchecked")
	public static <Y extends RPrimitive> RVector<Y> rep(Y v, int rows) {
		if (v instanceof RCharacter) return (RVector<Y>) rep((RCharacter) v, rows);
		if (v instanceof RNumeric) return (RVector<Y>) rep((RNumeric) v, rows);
		if (v instanceof RInteger) return (RVector<Y>) rep((RInteger) v, rows);
		if (v instanceof RFactor) return (RVector<Y>) rep((RFactor) v, rows);
		if (v instanceof RLogical) return (RVector<Y>) rep((RLogical) v, rows);
		if (v instanceof RDate) return (RVector<Y>) rep((RDate) v, rows);
		if (v instanceof RUntypedNa) return (RVector<Y>) rep((RUntypedNa) v, rows);
		throw new IncompatibleTypeException("No vector defined for: "+v.getClass().getCanonicalName());
	}
	
//	@SuppressWarnings("unchecked")
//	private static <Y extends RPrimitive> RVector<Y> create(Class<? extends RPrimitive> clazz, int length) {
//		if (RCharacter.class.equals(clazz)) return (RVector<Y>) new RCharacterVector(length); 
//		if (RInteger.class.equals(clazz)) return (RVector<Y>) new RIntegerVector(length);
//		if (RNumeric.class.equals(clazz)) return (RVector<Y>) new RNumericVector(length);
//		if (RFactor.class.equals(clazz)) return (RVector<Y>) new RFactorVector(length);
//		if (RLogical.class.equals(clazz)) return (RVector<Y>) new RLogicalVector(length);
//		if (RDate.class.equals(clazz)) return (RVector<Y>) new RDateVector(length);
//		throw new IncompatibleTypeException("No vector defined for: "+clazz.getCanonicalName());
//	}

	
	
	
	
	
	/**
	 * <p>singleton.</p>
	 *
	 * @param primitive a {@link uk.co.terminological.rjava.types.RCharacter} object
	 * @return a {@link uk.co.terminological.rjava.types.RCharacterVector} object
	 */
	public static RCharacterVector singleton(RCharacter primitive) {return new RCharacterVector().and(primitive);}
	/**
	 * <p>singleton.</p>
	 *
	 * @param primitive a {@link uk.co.terminological.rjava.types.RNumeric} object
	 * @return a {@link uk.co.terminological.rjava.types.RNumericVector} object
	 */
	public static RNumericVector singleton(RNumeric primitive) {return new RNumericVector().and(primitive);}
	/**
	 * <p>singleton.</p>
	 *
	 * @param primitive a {@link uk.co.terminological.rjava.types.RInteger} object
	 * @return a {@link uk.co.terminological.rjava.types.RIntegerVector} object
	 */
	public static RIntegerVector singleton(RInteger primitive) {return new RIntegerVector().and(primitive);}
	/**
	 * <p>singleton.</p>
	 *
	 * @param primitive a {@link uk.co.terminological.rjava.types.RFactor} object
	 * @return a {@link uk.co.terminological.rjava.types.RFactorVector} object
	 */
	public static RFactorVector singleton(RFactor primitive) {return new RFactorVector().and(primitive);}
	/**
	 * <p>singleton.</p>
	 *
	 * @param primitive a {@link uk.co.terminological.rjava.types.RLogical} object
	 * @return a {@link uk.co.terminological.rjava.types.RLogicalVector} object
	 */
	public static RLogicalVector singleton(RLogical primitive) {return new RLogicalVector().and(primitive);}
	/**
	 * <p>singleton.</p>
	 *
	 * @param primitive a {@link uk.co.terminological.rjava.types.RDate} object
	 * @return a {@link uk.co.terminological.rjava.types.RDateVector} object
	 */
	public static RDateVector singleton(RDate primitive) {return new RDateVector().and(primitive);}
	/**
	 * <p>singleton.</p>
	 *
	 * @param primitive a {@link uk.co.terminological.rjava.types.RUntypedNa} object
	 * @return a {@link uk.co.terminological.rjava.types.RUntypedNaVector} object
	 */
	public static RUntypedNaVector singleton(RUntypedNa primitive) {return new RUntypedNaVector().and(primitive);}
	
	/**
	 * <p>singleton.</p>
	 *
	 * @param v a Y object
	 * @param <Y> a Y class
	 * @return a {@link uk.co.terminological.rjava.types.RVector} object
	 */
	@SuppressWarnings("unchecked")
	public static <Y extends RPrimitive> RVector<Y> singleton(Y v) {
		if (v instanceof RCharacter) return (RVector<Y>) singleton((RCharacter) v);
		if (v instanceof RNumeric) return (RVector<Y>) singleton((RNumeric) v);
		if (v instanceof RInteger) return (RVector<Y>) singleton((RInteger) v);
		if (v instanceof RFactor) return (RVector<Y>) singleton((RFactor) v);
		if (v instanceof RLogical) return (RVector<Y>) singleton((RLogical) v);
		if (v instanceof RDate) return (RVector<Y>) singleton((RDate) v);
		if (v instanceof RUntypedNa) return (RVector<Y>) singleton((RUntypedNa) v);
		throw new IncompatibleTypeException("No vector defined for: "+v.getClass().getCanonicalName());
	}
	
	/**
	 * <p>padded.</p>
	 *
	 * @param primitive a {@link uk.co.terminological.rjava.types.RCharacter} object
	 * @param length a int
	 * @return a {@link uk.co.terminological.rjava.types.RCharacterVector} object
	 */
	public static RCharacterVector padded(RCharacter primitive, int length) { 
		if (length == 0) return RVector.singleton(primitive);
		return RVector.rep(RCharacter.NA,length-1).and(primitive);
	}
	/**
	 * <p>padded.</p>
	 *
	 * @param primitive a {@link uk.co.terminological.rjava.types.RNumeric} object
	 * @param length a int
	 * @return a {@link uk.co.terminological.rjava.types.RNumericVector} object
	 */
	public static RNumericVector padded(RNumeric primitive, int length) {
		if (length == 0) return RVector.singleton(primitive);
		return RVector.rep(RNumeric.NA,length-1).and(primitive);
	}
	/**
	 * <p>padded.</p>
	 *
	 * @param primitive a {@link uk.co.terminological.rjava.types.RInteger} object
	 * @param length a int
	 * @return a {@link uk.co.terminological.rjava.types.RIntegerVector} object
	 */
	public static RIntegerVector padded(RInteger primitive, int length) { 
		if (length == 0) return RVector.singleton(primitive);
		return RVector.rep(RInteger.NA,length-1).and(primitive);
	}
	/**
	 * <p>padded.</p>
	 *
	 * @param primitive a {@link uk.co.terminological.rjava.types.RFactor} object
	 * @param length a int
	 * @return a {@link uk.co.terminological.rjava.types.RFactorVector} object
	 */
	public static RFactorVector padded(RFactor primitive, int length) { 
		if (length == 0) return RVector.singleton(primitive);
		return RVector.rep(RFactor.NA,length-1).and(primitive);
	}
	/**
	 * <p>padded.</p>
	 *
	 * @param primitive a {@link uk.co.terminological.rjava.types.RLogical} object
	 * @param length a int
	 * @return a {@link uk.co.terminological.rjava.types.RLogicalVector} object
	 */
	public static RLogicalVector padded(RLogical primitive, int length) { 
		if (length == 0) return RVector.singleton(primitive);
		return RVector.rep(RLogical.NA,length-1).and(primitive);
	}
	/**
	 * <p>padded.</p>
	 *
	 * @param primitive a {@link uk.co.terminological.rjava.types.RDate} object
	 * @param length a int
	 * @return a {@link uk.co.terminological.rjava.types.RDateVector} object
	 */
	public static RDateVector padded(RDate primitive, int length) { 
		if (length == 0) return RVector.singleton(primitive);
		return RVector.rep(RDate.NA,length-1).and(primitive);		
	}
	/**
	 * <p>padded.</p>
	 *
	 * @param primitive a {@link uk.co.terminological.rjava.types.RUntypedNa} object
	 * @param length a int
	 * @return a {@link uk.co.terminological.rjava.types.RUntypedNaVector} object
	 */
	public static RUntypedNaVector padded(RUntypedNa primitive, int length) { 
		if (length == 0) return RVector.singleton(primitive);
		return RVector.rep(RUntypedNa.NA,length-1).and(primitive);
	}
	
	/**
	 * <p>padded.</p>
	 *
	 * @param v a Y object
	 * @param length a int
	 * @param <Y> a Y class
	 * @return a {@link uk.co.terminological.rjava.types.RVector} object
	 */
	@SuppressWarnings("unchecked")
	public static <Y extends RPrimitive> RVector<Y> padded(Y v, int length) {
		if (v instanceof RCharacter) return (RVector<Y>) padded((RCharacter) v,length);
		if (v instanceof RNumeric) return (RVector<Y>) padded((RNumeric) v,length);
		if (v instanceof RInteger) return (RVector<Y>) padded((RInteger) v,length);
		if (v instanceof RFactor) return (RVector<Y>) padded((RFactor) v,length);
		if (v instanceof RLogical) return (RVector<Y>) padded((RLogical) v,length);
		if (v instanceof RDate) return (RVector<Y>) padded((RDate) v,length);
		if (v instanceof RUntypedNa) return (RVector<Y>) padded((RUntypedNa) v,length);
		throw new IncompatibleTypeException("No vector defined for: "+v.getClass().getCanonicalName());
	}
	
	/**
	 * <p>fill.</p>
	 *
	 * @param x a X object
	 * @param length a int
	 * @return a {@link uk.co.terminological.rjava.types.RVector} object
	 */
	public RVector<X> fill(X x, int length) {
		for (int i=0; i<length; i++) {
			this.add(x);
		}
		return this;
	}
	
//	public boolean addAll(RVector<X> r1) {
//		return super.addAll(r1);
//	}
//	
//	public boolean add(X r1) {
//		return super.add(r1);
//	}

	/**
	 * <p>toString.</p>
	 *
	 * @return a {@link java.lang.String} object
	 */
	public String toString() {
		return "<"+this.getType().getSimpleName().toLowerCase()+"["+this.size()+"]>{"+
				this.stream().limit(10).map(v-> (v == null? "NULL": v.toString()))
						.collect(Collectors.joining(", "))+", ...}";
	}
	
	/**
	 * <p>rCode.</p>
	 *
	 * @return a {@link java.lang.String} object
	 */
	public String rCode() {
		return "c("+this.stream().map(s -> s.rCode()).collect(Collectors.joining(", "))+")";
	}
	
	/**
	 * <p>with.</p>
	 *
	 * @param o a {@link java.lang.String} object
	 * @return a {@link uk.co.terminological.rjava.types.RCharacterVector} object
	 */
	public static RCharacterVector with(String... o) {return RConverter.convert(o);}
	
	/**
	 * <p>with.</p>
	 *
	 * @param o a {@link java.lang.Double} object
	 * @return a {@link uk.co.terminological.rjava.types.RNumericVector} object
	 */
	public static RNumericVector with(Double... o) {return RConverter.convert(o);}
	/**
	 * <p>with.</p>
	 *
	 * @param o a {@link java.lang.Long} object
	 * @return a {@link uk.co.terminological.rjava.types.RNumericVector} object
	 */
	public static RNumericVector with(Long... o) {return RConverter.convert(o);}
	/**
	 * <p>with.</p>
	 *
	 * @param o a {@link java.math.BigDecimal} object
	 * @return a {@link uk.co.terminological.rjava.types.RNumericVector} object
	 */
	public static RNumericVector with(BigDecimal... o) {return RConverter.convert(o);}
	/**
	 * <p>with.</p>
	 *
	 * @param o a {@link java.lang.Float} object
	 * @return a {@link uk.co.terminological.rjava.types.RNumericVector} object
	 */
	public static RNumericVector with(Float... o) {return RConverter.convert(o);}
	
	/**
	 * <p>with.</p>
	 *
	 * @param o a {@link java.lang.Integer} object
	 * @return a {@link uk.co.terminological.rjava.types.RIntegerVector} object
	 */
	public static RIntegerVector with(Integer... o) {return RConverter.convert(o);}
	/**
	 * <p>with.</p>
	 *
	 * @param o a {@link java.lang.Boolean} object
	 * @return a {@link uk.co.terminological.rjava.types.RLogicalVector} object
	 */
	public static RLogicalVector with(Boolean... o) {return RConverter.convert(o);}
	/**
	 * <p>with.</p>
	 *
	 * @param o a {@link java.time.LocalDate} object
	 * @return a {@link uk.co.terminological.rjava.types.RDateVector} object
	 */
	public static RDateVector with(LocalDate... o) {return RConverter.convert(o);}
	
	/**
	 * <p>with.</p>
	 *
	 * @param o a {@link java.lang.Enum} object
	 * @return a {@link uk.co.terminological.rjava.types.RFactorVector} object
	 */
	public static RFactorVector with(Enum<?>... o) {return RConverter.convert(o);}
	
	
	/**
	 * <p>getType.</p>
	 *
	 * @return a {@link java.lang.Class} object
	 */
	public abstract Class<X> getType();
	
	/**
	 * <p>and.</p>
	 *
	 * @param o a X object
	 * @param <Y> a Y class
	 * @return a Y object
	 */
	public abstract <Y extends RVector<X>> Y and(@SuppressWarnings("unchecked") X... o);
	
	/**
	 * <p>distinct.</p>
	 *
	 * @return a {@link java.util.Set} object
	 */
	public Set<X> distinct() {
		return new LinkedHashSet<>(this);
	}
	
	/**
	 * <p>matches.</p>
	 *
	 * @param value a {@link uk.co.terminological.rjava.types.RPrimitive} object
	 * @return a {@link java.util.BitSet} object
	 */
	public BitSet matches(RPrimitive value) {
		BitSet out = new BitSet(this.size());
		for (int i=0;i<this.size();i++) {
			out.set(i,value.equals(this.get(i)));
		}
		return out;
	}
	
	/**
	 * <p>matches.</p>
	 *
	 * @param criteria a {@link java.util.function.Predicate} object
	 * @return a {@link java.util.BitSet} object
	 */
	@SuppressWarnings("unchecked")
	public BitSet matches(Predicate<?> criteria) {
		BitSet out = new BitSet(this.size());
		try {
			for (int i=0;i<this.size();i++) {
				out.set(i,((Predicate<X>) criteria).test(this.get(i)));
			}
		} catch (Exception e) {
			log.debug("Vector filter did not complete correctly, assuming no match: "+criteria.toString());
		}
		return out;
	}
	
	/**
	 * <p>subset.</p>
	 *
	 * @param filter a {@link java.util.BitSet} object
	 * @return a {@link uk.co.terminological.rjava.types.RVector} object
	 */
	public RVector<X> subset(BitSet filter) {
		if(filter.length() > this.size()) throw new IndexOutOfBoundsException("Filter length greater than vector length");
		RVector<X> out = RVector.empty(this.getType());
		for (int i = 0; i<this.size(); i++) {
			if (filter.get(i)) {
				out.add(this.get(i));
			}
		}
		return out;
	}

	/**
	 * <p>as.</p>
	 *
	 * @param vectorClass a {@link java.lang.Class} object
	 * @param <Y> a Y class
	 * @return a Y object
	 */
	public <Y extends RVector<?>> Y as(Class<Y> vectorClass) {
		try {
			return vectorClass.cast(this);
		} catch (ClassCastException e) {
			throw new IncompatibleTypeException("Requested a "+vectorClass.getSimpleName()+" but found a "+this.getClass().getSimpleName());
		}
	}
	
	/**
	 * <p>get.</p>
	 *
	 * @param <Y> a Y class
	 * @return a {@link java.util.stream.Stream} object
	 */
	public abstract <Y extends Object> Stream<Y> get();
	
	/**
	 * <p>opt.</p>
	 *
	 * @param <Y> a Y class
	 * @return a {@link java.util.stream.Stream} object
	 */
	public abstract <Y extends Object> Stream<Optional<Y>> opt();

	/**
	 * <p>addAllUnsafe.</p>
	 *
	 * @param rVector a {@link uk.co.terminological.rjava.types.RVector} object
	 * @param <Y> a Y class
	 * @return a {@link uk.co.terminological.rjava.types.RVector} object
	 */
	@SuppressWarnings("unchecked")
	protected <Y extends RPrimitive> RVector<Y> addAllUnsafe(RVector<? extends RPrimitive> rVector) {
		try {
			this.addAll((RVector<X>) rVector);
			return (RVector<Y>) this;
		} catch (ClassCastException e) {
			throw new IncompatibleTypeException("Tried to append a "+rVector.getClass().getSimpleName()+" to a "+this.getClass().getSimpleName());
		}
	}

	/**
	 * <p>addUnsafe.</p>
	 *
	 * @param v a {@link uk.co.terminological.rjava.types.RPrimitive} object
	 * @param <Y> a Y class
	 * @return a {@link uk.co.terminological.rjava.types.RVector} object
	 */
	@SuppressWarnings("unchecked")
	protected <Y extends RPrimitive> RVector<Y> addUnsafe(RPrimitive v) {
		try {
			this.add(v.get(getType()));
			return (RVector<Y>) this;
		} catch (ClassCastException e) {
			throw new IncompatibleTypeException("Tried to append a "+v.getClass().getSimpleName()+" to a "+this.getClass().getSimpleName());
		}
	}

	/**
	 * <p>fillNA.</p>
	 *
	 * @param appendNrow a int
	 */
	public abstract void fillNA(int appendNrow);

	

	
	
}
