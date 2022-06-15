package uk.co.terminological.rjava;

import java.util.Arrays;
import java.util.function.BiPredicate;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import uk.co.terminological.rjava.types.RCharacter;
import uk.co.terminological.rjava.types.RInteger;
import uk.co.terminological.rjava.types.RNumeric;
import uk.co.terminological.rjava.types.RPrimitive;
import uk.co.terminological.rjava.types.RVector;

public class RFunctions {

	/** Type cast a Numeric to Integer
	 * @param i RInteger Value
	 * @return RNumeric with the same value
	 */
	public static RNumeric asNumeric(RInteger i) {
		return new RNumeric((double) i.get());
	}
	
	/**
	 * @param sep any sepearating string
	 * @param objects the objects to join which will be converted using the toString of the underlying primitive 
	 * @return a String
	 */
	public static RCharacter paste(String sep, RPrimitive... objects) {
		return new RCharacter(
				Arrays.stream(objects).map(obj -> obj.get().toString()).collect(Collectors.joining(sep))
				);
	}
	
	/**
	 * @param tester - a predicate of RPrimtive pairs
	 * @param lhs - the lhs RVector
	 * @param rhs - the rhs RVector
	 * @return true if both vectors are either empty or all value pairs return true. false if the vectors are not the same length or any are false;
	 */
	public static <X extends RPrimitive> boolean all(BiPredicate<X,X> tester, RVector<X> lhs, RVector<X> rhs) {
		if (lhs.size() != rhs.size()) return false;
		if (lhs.size() == 0) return true;
		return IntStream.range(0,lhs.size())
				.mapToObj(i -> tester.test(lhs.get(i),rhs.get(i)))
				.reduce(Boolean.TRUE, (b1,b2) -> Boolean.logicalAnd(b1, b2));
	}
	
	/**
	 * @param tester - a predicate of RPrimtive pairs
	 * @param lhs - the lhs RVector
	 * @param rhs - the rhs RVector
	 * @return true if both vectors are either empty or all value pairs return true. false if the vectors are not the same length or any are false;
	 */
	public static <X extends RPrimitive> boolean all(Predicate<X> tester, RVector<X> lhs) {
		if (lhs.size() == 0) return true;
		return IntStream.range(0,lhs.size())
				.mapToObj(i -> tester.test(lhs.get(i)))
				.reduce(Boolean.TRUE, (b1,b2) -> Boolean.logicalAnd(b1, b2));
	}
	
	
	/**
	 * @param tester - a predicate of RPrimtive pairs
	 * @param lhs - the lhs RVector
	 * @param rhs - the rhs RVector
	 * @return true if both vectors are either empty or all value pairs return true. false if the vectors are not the same length or any are false;
	 */
	public static <X extends RPrimitive> boolean any(BiPredicate<X,X> tester, RVector<X> lhs, RVector<X> rhs) {
		if (lhs.size() != rhs.size()) return false;
		if (lhs.size() == 0) return true;
		return IntStream.range(0,lhs.size())
				.mapToObj(i -> tester.test(lhs.get(i),rhs.get(i)))
				.reduce(Boolean.FALSE, (b1,b2) -> Boolean.logicalOr(b1, b2));
	}
	
	/**
	 * @param tester - a predicate of RPrimtive pairs
	 * @param lhs - the lhs RVector
	 * @param rhs - the rhs RVector
	 * @return true if both vectors are either empty or all value pairs return true. false if the vectors are not the same length or any are false;
	 */
	public static <X extends RPrimitive> boolean any(Predicate<X> tester, RVector<X> lhs) {
		if (lhs.size() == 0) return true;
		return IntStream.range(0,lhs.size())
				.mapToObj(i -> tester.test(lhs.get(i)))
				.reduce(Boolean.FALSE, (b1,b2) -> Boolean.logicalOr(b1, b2));
	}
	
	public static boolean precisionEquals(RNumeric v1, RNumeric v2, Double epsilon) {
		if (v1.equals(v2)) return true;
		if (Double.doubleToRawLongBits(v1.get()) == Double.doubleToRawLongBits(v2.get())) return true;
		if (Math.abs(v1.get()-v2.get()) < epsilon) return true;
		return false;
	}
	
	public static boolean isNa(RPrimitive x) {return x.isNa();}
	
	public static boolean isFinite(RNumeric x) {
		return !x.isNa() && !x.get().isNaN() && !x.get().isInfinite();
	}
	
}
