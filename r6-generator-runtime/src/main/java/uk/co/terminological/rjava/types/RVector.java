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

public abstract class RVector<X extends RPrimitive> extends ArrayList<X> implements RObject {

	private static final long serialVersionUID = RObject.datatypeVersion;
	private static Logger log = LoggerFactory.getLogger(RVector.class);
	
	public RVector() {
		super();
	}
	
	public RVector(int length) {
		super(length);
	}
	
	public RVector(List<X> subList) {
		super(subList);
	}

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
	
	public static RCharacterVector rep(RCharacter primitive, int length) {return (RCharacterVector) new RCharacterVector(length).fill(primitive, length);}
	public static RNumericVector rep(RNumeric primitive, int length) {return (RNumericVector) new RNumericVector(length).fill(primitive, length);}
	public static RIntegerVector rep(RInteger primitive, int length) {return (RIntegerVector) new RIntegerVector(length).fill(primitive, length);}
	public static RFactorVector rep(RFactor primitive, int length) {return (RFactorVector) new RFactorVector(length).fill(primitive, length);}
	public static RLogicalVector rep(RLogical primitive, int length) {return (RLogicalVector) new RLogicalVector(length).fill(primitive, length);}
	public static RDateVector rep(RDate primitive, int length) {return (RDateVector) new RDateVector(length).fill(primitive, length);}
	public static RUntypedNaVector rep(RUntypedNa primitive, int length) {return (RUntypedNaVector) new RUntypedNaVector(length).fill(primitive, length);}
	
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

	
	
	
	
	
	public static RCharacterVector singleton(RCharacter primitive) {return new RCharacterVector().and(primitive);}
	public static RNumericVector singleton(RNumeric primitive) {return new RNumericVector().and(primitive);}
	public static RIntegerVector singleton(RInteger primitive) {return new RIntegerVector().and(primitive);}
	public static RFactorVector singleton(RFactor primitive) {return new RFactorVector().and(primitive);}
	public static RLogicalVector singleton(RLogical primitive) {return new RLogicalVector().and(primitive);}
	public static RDateVector singleton(RDate primitive) {return new RDateVector().and(primitive);}
	public static RUntypedNaVector singleton(RUntypedNa primitive) {return new RUntypedNaVector().and(primitive);}
	
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
	
	public static RCharacterVector padded(RCharacter primitive, int length) { 
		if (length == 0) return RVector.singleton(primitive);
		return RVector.rep(RCharacter.NA,length-1).and(primitive);
	}
	public static RNumericVector padded(RNumeric primitive, int length) {
		if (length == 0) return RVector.singleton(primitive);
		return RVector.rep(RNumeric.NA,length-1).and(primitive);
	}
	public static RIntegerVector padded(RInteger primitive, int length) { 
		if (length == 0) return RVector.singleton(primitive);
		return RVector.rep(RInteger.NA,length-1).and(primitive);
	}
	public static RFactorVector padded(RFactor primitive, int length) { 
		if (length == 0) return RVector.singleton(primitive);
		return RVector.rep(RFactor.NA,length-1).and(primitive);
	}
	public static RLogicalVector padded(RLogical primitive, int length) { 
		if (length == 0) return RVector.singleton(primitive);
		return RVector.rep(RLogical.NA,length-1).and(primitive);
	}
	public static RDateVector padded(RDate primitive, int length) { 
		if (length == 0) return RVector.singleton(primitive);
		return RVector.rep(RDate.NA,length-1).and(primitive);		
	}
	public static RUntypedNaVector padded(RUntypedNa primitive, int length) { 
		if (length == 0) return RVector.singleton(primitive);
		return RVector.rep(RUntypedNa.NA,length-1).and(primitive);
	}
	
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

	public String toString() {
		return "<"+this.getType().getSimpleName().toLowerCase()+"["+this.size()+"]>{"+
				this.stream().limit(10).map(v-> (v == null? "NULL": v.toString()))
						.collect(Collectors.joining(", "))+", ...}";
	}
	
	public String rCode() {
		return "c("+this.stream().map(s -> s.rCode()).collect(Collectors.joining(", "))+")";
	}
	
	public static RCharacterVector with(String... o) {return RConverter.convert(o);}
	
	public static RNumericVector with(Double... o) {return RConverter.convert(o);}
	public static RNumericVector with(Long... o) {return RConverter.convert(o);}
	public static RNumericVector with(BigDecimal... o) {return RConverter.convert(o);}
	public static RNumericVector with(Float... o) {return RConverter.convert(o);}
	
	public static RIntegerVector with(Integer... o) {return RConverter.convert(o);}
	public static RLogicalVector with(Boolean... o) {return RConverter.convert(o);}
	public static RDateVector with(LocalDate... o) {return RConverter.convert(o);}
	
	public static RFactorVector with(Enum<?>... o) {return RConverter.convert(o);}
	
	
	public abstract Class<X> getType();
	
	public abstract <Y extends RVector<X>> Y and(@SuppressWarnings("unchecked") X... o);
	
	public Set<X> distinct() {
		return new LinkedHashSet<>(this);
	}
	
	public BitSet matches(RPrimitive value) {
		BitSet out = new BitSet(this.size());
		for (int i=0;i<this.size();i++) {
			out.set(i,value.equals(this.get(i)));
		}
		return out;
	}
	
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

	public <Y extends RVector<?>> Y as(Class<Y> vectorClass) {
		try {
			return vectorClass.cast(this);
		} catch (ClassCastException e) {
			throw new IncompatibleTypeException("Requested a "+vectorClass.getSimpleName()+" but found a "+this.getClass().getSimpleName());
		}
	}
	
	public abstract <Y extends Object> Stream<Y> get();
	
	public abstract <Y extends Object> Stream<Optional<Y>> opt();

	@SuppressWarnings("unchecked")
	protected <Y extends RPrimitive> RVector<Y> addAllUnsafe(RVector<? extends RPrimitive> rVector) {
		try {
			this.addAll((RVector<X>) rVector);
			return (RVector<Y>) this;
		} catch (ClassCastException e) {
			throw new IncompatibleTypeException("Tried to append a "+rVector.getClass().getSimpleName()+" to a "+this.getClass().getSimpleName());
		}
	}

	@SuppressWarnings("unchecked")
	protected <Y extends RPrimitive> RVector<Y> addUnsafe(RPrimitive v) {
		try {
			this.add(v.get(getType()));
			return (RVector<Y>) this;
		} catch (ClassCastException e) {
			throw new IncompatibleTypeException("Tried to append a "+v.getClass().getSimpleName()+" to a "+this.getClass().getSimpleName());
		}
	}

	public abstract void fillNA(int appendNrow);

	

	
	
}
