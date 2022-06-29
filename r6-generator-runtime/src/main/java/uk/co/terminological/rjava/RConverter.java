package uk.co.terminological.rjava;

import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import uk.co.terminological.rjava.Rule.MapRule;
import uk.co.terminological.rjava.Rule.StreamRule;
import uk.co.terminological.rjava.types.RCharacter;
import uk.co.terminological.rjava.types.RCharacterVector;
import uk.co.terminological.rjava.types.RDataframe;
import uk.co.terminological.rjava.types.RDate;
import uk.co.terminological.rjava.types.RDateVector;
import uk.co.terminological.rjava.types.RFactor;
import uk.co.terminological.rjava.types.RFactorVector;
import uk.co.terminological.rjava.types.RInteger;
import uk.co.terminological.rjava.types.RIntegerVector;
import uk.co.terminological.rjava.types.RList;
import uk.co.terminological.rjava.types.RLogical;
import uk.co.terminological.rjava.types.RLogicalVector;
import uk.co.terminological.rjava.types.RNamedList;
import uk.co.terminological.rjava.types.RNamedPrimitives;
import uk.co.terminological.rjava.types.RNumeric;
import uk.co.terminological.rjava.types.RNumericVector;
import uk.co.terminological.rjava.types.RObject;
import uk.co.terminological.rjava.types.RPrimitive;
import uk.co.terminological.rjava.types.RUntypedNa;
import uk.co.terminological.rjava.types.RVector;



/** 
 * Variety of static functions to facilitate data conversion from Java to R.
 * @author terminological
 *
 */
public class RConverter {

	/**
	 * Convert int array to RIntegerVector.
	 *
	 * @param array an array of ints
	 * @return a RIntegerVector
	 */
	public static RIntegerVector convert(int[] array) {	return new RIntegerVector(array); }
	
	
	/**
	 * Convert a double array to a RNumericVector.
	 *
	 * @param array the array
	 * @return the r numeric vector
	 */
	public static RNumericVector convert(double[] array) {	return new RNumericVector(array); }
	
	
	/**
	 * Convert a boolean array to a RLogicalVector.
	 *
	 * @param array the array
	 * @return the r logical vector
	 */
	public static RLogicalVector convert(boolean[] array) {	return new RLogicalVector(array); }
	
	/**
	 * Convert a String array to a RCharacterVector.
	 *
	 * @param array the array
	 * @return the r character vector
	 */
	public static RCharacterVector convert(String[] array) {	return new RCharacterVector(array); }
	
	
	/**
	 * Convert an array of integers and a set of levels to a RFactorVector.
	 *
	 * @param array  the array
	 * @param labels the labels of the factor
	 * @return the r factor vector
	 */
	public static RFactorVector convert(int[] array, String[] labels) {	return new RFactorVector(array, labels); }
	
	
	/**
	 * Convert boxed integer array to R.
	 *
	 * @param array the array
	 * @return the r integer vector
	 */
	public static RIntegerVector convert(Integer[] array) {	return Stream.of(array).collect(integerCollector()); }
	
	
	/**
	 * Convert boxed double array to R.
	 *
	 * @param array the array
	 * @return the r numeric vector
	 */
	public static RNumericVector convert(Double[] array) {	return Stream.of(array).collect(doubleCollector()); }
	
	
	/**
	 * Convert boxed long array to R.
	 *
	 * @param array the array
	 * @return the r numeric vector
	 */
	public static RNumericVector convert(Long[] array) {	return Stream.of(array).collect(longCollector()); }
	
	
	/**
	 * Convert boxed float array to R.
	 *
	 * @param array the array
	 * @return the r numeric vector
	 */
	public static RNumericVector convert(Float[] array) {	return Stream.of(array).collect(floatCollector()); }
	
	/**
	 * Convert BigDecimal array to R.
	 *
	 * @param array the array
	 * @return the r numeric vector
	 */
	public static RNumericVector convert(BigDecimal[] array) {	return Stream.of(array).collect(bigDecimalCollector()); }
	
	/**
	 * Convert boxed Boolean array to R.
	 *
	 * @param array the array
	 * @return the r logical vector
	 */
	public static RLogicalVector convert(Boolean[] array) {	return Stream.of(array).collect(booleanCollector()); }
	
	/**
	 * Convert LocalDate array to R.
	 *
	 * @param array the array
	 * @return the r date vector
	 */
	public static RDateVector convert(LocalDate[] array) {	return Stream.of(array).collect(dateCollector()); }
	
	/**
	 * Convert an array of enumerated type X, to a factor vector .
	 *
	 * @param       <X> the generic type of the enum
	 * @param array the array
	 * @return the r factor vector
	 */
	@SuppressWarnings("unchecked")
	public static <X extends Enum<?>> RFactorVector convert(X[] array) {
		Class<X> cls = (Class<X>) array[0].getClass();
		return (RFactorVector) Stream.of(array).collect(enumCollector(cls)); }
	
	
	/**
	 * Convert integer to R.
	 *
	 * @param boxed the boxed
	 * @return the r integer
	 */
	public static RInteger convert(Integer boxed) {return new RInteger(boxed);}
	
	/**
	 * Convert long to R.
	 *
	 * @param boxed the boxed
	 * @return the r numeric
	 */
	public static RNumeric convert(Long boxed) {return new RNumeric(boxed);}
	
	/**
	 * Convert double to R.
	 *
	 * @param boxed the boxed
	 * @return the r numeric
	 */
	public static RNumeric convert(Double boxed) {return new RNumeric(boxed);}
	
	/**
	 * Convert float to R.
	 *
	 * @param boxed the boxed
	 * @return the r numeric
	 */
	public static RNumeric convert(Float boxed) {return new RNumeric(boxed);}
	
	/**
	 * Convert BigDecimal to R.
	 *
	 * @param boxed the boxed
	 * @return the r numeric
	 */
	public static RNumeric convert(BigDecimal boxed) {return new RNumeric(boxed);}
	
	/**
	 * Convert.
	 *
	 * @param boxed the boxed
	 * @return the r logical
	 */
	public static RLogical convert(Boolean boxed) {return new RLogical(boxed);}
	
	/**
	 * Convert.
	 *
	 * @param boxed the boxed
	 * @return the r character
	 */
	public static RCharacter convert(String boxed) {return new RCharacter(boxed);}
	
	/**
	 * Convert single enum to R.
	 *
	 * @param boxed the boxed
	 * @return the r factor
	 */
	public static RFactor convert(Enum<?> boxed) {return new RFactor(boxed);}
	
	/**
	 * Convert Local Date to R.
	 *
	 * @param boxed the boxed
	 * @return the r date
	 */
	public static RDate convert(LocalDate boxed) {return new RDate(boxed);}
	
	/**
	 * Convert object to primitive.
	 *
	 * @param   <X> the generic type of the resulting R value. This will be filled in by the context
	 * @param o the object that is to be converted
	 * @return a duck typed RPrimitive of some unknown type.
	 * @throws UnconvertableTypeException the unconvertable type exception if o cannot be converted to some form of RPrimitive.
	 */
	@SuppressWarnings("unchecked")
	public static <X extends RPrimitive> X convertObjectToPrimitive(Object o) throws UnconvertableTypeException {
		if (o instanceof Integer) return (X) convert((Integer) o);
		if (o instanceof Long) return (X) convert((Long) o);
		if (o instanceof Double) return (X) convert((Double) o);
		if (o instanceof Float) return (X) convert((Float) o);
		if (o instanceof BigDecimal) return (X) convert((BigDecimal) o);
		if (o instanceof Boolean) return (X) convert((Boolean) o);
		if (o instanceof Enum) return (X) convert((Enum<?>) o);
		if (o instanceof String) return (X) convert((String) o);
		if (o instanceof LocalDate) return (X) convert((LocalDate) o);
		if (o == null) return (X) new RUntypedNa();
		throw new UnconvertableTypeException("Don't know how to convert a: "+o.getClass());
	}
	
	/**
	 * Try convert object to primitive, and returning an Optional&lt;RPrimitive&gt; vaue.
	 *
	 * @param   <X> the generic type of the resulting R value. This will be filled in by the context
	 * @param v the object that is to be converted
	 * @return the optional duck typed RPrimitive of some unknown type.
	 */
	public static <X extends RPrimitive> Optional<X> tryConvertObjectToPrimitive(Object v) {
		try {
			X out = convertObjectToPrimitive(v);
			return Optional.ofNullable(out);
		} catch (UnconvertableTypeException e) {
			return Optional.empty();
		}
	}
	
	protected static RPrimitive convertObjectToPrimitiveUnsafe(Object tmp2) {
		try {	
			return convertObjectToPrimitive(tmp2);
		} catch (UnconvertableTypeException e) {
			throw new IncompatibleTypeException("Unsupported type: "+tmp2.getClass().getSimpleName(), e);
		}
	}
	
	/**
	 * Convert a array object to vector.
	 *
	 * @param   <X> the generic type of the resulting RVector
	 * @param o the array of java primitives, boxed, BigDecimal, LocalDates, or enumeration.
	 * @return a subtype of RVector of undefined type 
	 * @throws UnconvertableTypeException the unconvertable type exception
	 */
	@SuppressWarnings("unchecked")
	public static <X extends RVector<?>> X convertObjectToVector(Object o) throws UnconvertableTypeException {
		
		if (o instanceof int[]) return (X) convert((int[]) o);
		if (o instanceof double[]) return (X) convert((double[]) o);
		if (o instanceof boolean[]) return (X) convert((boolean[]) o);
		if (o instanceof String[]) return (X) convert((String[]) o);
		
		if (o instanceof Integer[]) return (X) convert((Integer[]) o);
		if (o instanceof Double[]) return (X) convert((Double[]) o);
		if (o instanceof Boolean[]) return (X) convert((Boolean[]) o);
		if (o instanceof BigDecimal[]) return (X) convert((BigDecimal[]) o);
		if (o instanceof Long[]) return (X) convert((Long[]) o);
		if (o instanceof Float[]) return (X) convert((Float[]) o);
		if (o instanceof LocalDate[]) return (X) convert((LocalDate[]) o);
		if (o instanceof Enum<?>[]) return (X) convert((Enum<?>[]) o);
		
		throw new UnconvertableTypeException("Don't know how to convert a: "+o.getClass());
	}
	
	/**
	 * Convert a generic java object to some kind of RObject.
	 *
	 * @param obj the java object of unknown type. Should be something that can be converted to a RPrimitive or RVector
	 * @return the resulting r object
	 * @throws UnconvertableTypeException the unconvertable type exception
	 */
	public static RObject convertObject(Object obj) throws UnconvertableTypeException {
		if (obj instanceof RObject) return (RObject) obj;
		try {
			return convertObjectToPrimitive(obj);
		} catch (UnconvertableTypeException e) {
			return convertObjectToVector(obj);
		}
	} 
	
	// COLLECTOR BASED CONVERSION
	
	/**
	 * The ConvertingCollector interface allows us to reuse stream collectors for other non stream data types by
	 * apply these to streams, iterators, arrays, collections, and iterables, as well as plain
	 * instances. By default all of these conversions are handled through streams. The target data type is in our case usually
	 * an RVector
	 * 
	 * @author terminological
	 *
	 * @param <X> - source datatype which will usually be a java primitive type or similar
	 * @param <Y> - target datatype which will usually be an RVector of some kind
	 */
	public static class CollectingConverter<X,Y> {	
		
		/** The collector. */
		Collector<X,?,Y> collector;
		
		/**
		 * Instantiates a new collecting converter.
		 *
		 * @param collector the collector
		 */
		public CollectingConverter(Collector<X,?,Y> collector) {
			this.collector = collector;
		}
		
		/**
		 * Convert an instance of type X to Y.
		 *
		 * @param instance a singleton of type X
		 * @return a list or array
		 */
		public Y convert(X instance) {
			return convert(Stream.of(instance));
		}
		
		/**
		 * Convert a stream of type X to Y.
		 *
		 * @param stream a stream of type X
		 * @return a list or array
		 */
		public Y convert(Stream<X> stream) {
			return stream.collect(collector);
		}
		
		/**
		 * Convert a collection of type X to Y.
		 *
		 * @param collection a collection of type X
		 * @return a list or array
		 */
		public Y convert(Collection<X> collection) {
			return convert(collection.stream());
		}
		
		/**
		 * Convert a iterable of type X to Y.
		 *
		 * @param iterable the iterable
		 * @return the y
		 */
		public Y convert(Iterable<X> iterable) {
			return convert(StreamSupport.stream(iterable.spliterator(), false));
		}
		
		/**
		 * Convert a iterator of type X to Y.
		 *
		 * @param iterator the iterator
		 * @return the y
		 */
		public Y convert(Iterator<X> iterator) {
			return convert(() -> iterator);
		}
		
		/**
		 * Convert a array of type X to Y.
		 *
		 * @param array the array
		 * @return the y
		 */
		public Y convert(X[] array) {
			return convert(Arrays.asList(array));
		}

		/**
		 * Static constructor using a collector.
		 *
		 * @param <A> the generic type
		 * @param <B> the generic type
		 * @param collector the collector
		 * @return the collecting converter
		 */
		public static <A,B> CollectingConverter<A,B> from(Collector<A,?,B> collector) {
			return new CollectingConverter<A,B>(collector);
		}
		
		
		
		
		
	}
	
	/**
	 * This constructs a converting collector that can be used directly to collect the results. 
	 * 
	 * RConverter.using(integerCollector()).convert(... lots of different types supported, e.g. stream or instance or iterable etc)
	 *
	 * @param           <X> the input Java type will allow any collection or stream type to be converted
	 * @param           <Y> the output RObect type which will be filled in by the collector
	 * @param collector the collector this is generally expected to be one of RConverter.integerCollector(), longCollector() etc.
	 * @return the collecting converter whose convert() method can 
	 */
	public static <X, Y extends RObject> CollectingConverter<X,Y> using(Collector<X,?,Y> collector) {
		return CollectingConverter.from(collector);
	}
	
	// VECTOR COLLECTORS
	
	/**
	 * VectorCollectors can be used to collect a stream of java objects into an RVector with defined type.
	 *
	 * @param <T> type the input Java Class type
	 * @param <X> the intermediate accumulator type
	 * @param <Y> the output RPrimitive type of the resulting RVector
	 */
	public static interface VectorCollector<T,X extends RPrimitive, Y extends RVector<X>> extends Collector<T,Y,Y> {}
	
	private static <T,X extends RPrimitive, Y extends RVector<X>> VectorCollector<T,X,Y> vectorCollector(Supplier<Y> supplier, BiConsumer<Y, T> accumulator, Class<Y> type) {
		return new VectorCollector<T,X,Y>() {

			@Override
			public Supplier<Y> supplier() {
				return supplier;
			}

			@Override
			public BiConsumer<Y, T> accumulator() {
				return accumulator;
			}

			@Override
			public BinaryOperator<Y> combiner() {
				return (r1,r2) -> {
					Y out = supplier().get();
					out.addAll(r1);
					out.addAll(r2);
					return out;
				};
			}

			@Override
			public Function<Y, Y> finisher() {
				return r -> r;
			}

			@Override
			public Set<Characteristics> characteristics() {
				return new HashSet<>(
						Arrays.asList(
								Characteristics.UNORDERED,
								Characteristics.CONCURRENT,
								Characteristics.IDENTITY_FINISH
								));
			}
			
		};
	};
	
	/**
	 * Integer collector converts a stream of integers to a RVector.
	 *
	 * @return the vector collector
	 */
	public static VectorCollector<Integer,RInteger,RIntegerVector> integerCollector() {
		return vectorCollector(
			() -> new RIntegerVector(), 
			(r,i) -> r.add(RConverter.convert(i)), 
			RIntegerVector.class
		);
	}
	
	/**
	 * Long collector converts a stream of long to a RVector.
	 *
	 * @return the vector collector
	 */
	public static VectorCollector<Long,RNumeric,RNumericVector> longCollector() {
		return vectorCollector(
			() -> new RNumericVector(), 
			(r,i) -> r.add(RConverter.convert(i)),
			RNumericVector.class
		);}
	
	/**
	 * Double collector converts a stream of doubles to a RVector.
	 *
	 * @return the vector collector
	 */
	public static VectorCollector<Double,RNumeric,RNumericVector> doubleCollector() {
		return vectorCollector(
				() -> new RNumericVector(), 
				(r,i) -> r.add(RConverter.convert(i)),
				RNumericVector.class
		);}
	
	/**
	 * Big decimal collector converts a stream of BigDecimals to a RVector.
	 *
	 * @return the vector collector
	 */
	public static VectorCollector<BigDecimal,RNumeric,RNumericVector> bigDecimalCollector() {
		return vectorCollector(
				() -> new RNumericVector(), 
				(r,i) -> r.add(RConverter.convert(i)),
				RNumericVector.class
		);}
	
	/**
	 * Float collector converts a stream of floats to a RVector..
	 *
	 * @return the vector collector
	 */
	public static VectorCollector<Float,RNumeric,RNumericVector> floatCollector() {
		return vectorCollector(
				() -> new RNumericVector(), 
				(r,i) -> r.add(RConverter.convert(i)),
				RNumericVector.class
		);}
	
	/**
	 * Boolean collector converts a stream of booleans to a RVector.
	 *
	 * @return the vector collector
	 */
	public static VectorCollector<Boolean,RLogical,RLogicalVector> booleanCollector() {return vectorCollector(
			() -> new RLogicalVector(), 
			(r,i) -> r.add(RConverter.convert(i)),
			RLogicalVector.class
	);}
	
	/**
	 * String collector converts a stream of string to a RVector.
	 *
	 * @return the vector collector
	 */
	public static VectorCollector<String,RCharacter,RCharacterVector> stringCollector() {return vectorCollector(
			() -> new RCharacterVector(), 
			(r,i) -> r.add(RConverter.convert(i)),
			RCharacterVector.class
	);}
	
	/**
	 * Date collector converts a stream of localDates to a RVector.
	 *
	 * @return the vector collector
	 */
	public static VectorCollector<LocalDate,RDate,RDateVector> dateCollector() {
		return vectorCollector(
				() -> new RDateVector(), 
				(r,i) -> r.add(RConverter.convert(i)),
				RDateVector.class
		);}
	
	/**
	 * Date from string collector.
	 *
	 * @return the vector collector
	 */
	public static VectorCollector<String,RDate,RDateVector> dateFromStringCollector() {
		return vectorCollector(
				() -> new RDateVector(), 
				(r,i) -> r.add(RConverter.convert(i==null?(LocalDate) null:LocalDate.parse(i))),
				RDateVector.class
		);}
	
	/**
	 * Enum collector converts a stream of enumerated types to a RVector.
	 *
	 * @param           <X> the generic type
	 * @param enumClass the enum class
	 * @return the vector collector
	 */
	public static <X extends Enum<?>> VectorCollector<X,RFactor,RFactorVector> enumCollector(Class<X> enumClass) {
		String[] labels = Stream.of(enumClass.getEnumConstants()).map(x -> x.toString()).collect(Collectors.toList()).toArray(new String[] {});
		return vectorCollector(
				() -> new RFactorVector(labels), 
				(r,i) -> r.add(RConverter.convert(i)),
				RFactorVector.class
				);}
	
	
	/**
	 * Annotated collector converts a stream of objects of a class which has getters annotated with @RName annotations into a RDataframe.
	 * The column names are defined by the @RName annotations
	 *
	 * @param      <X> the generic type of the annotated class
	 * @param type defines the class that this collector will convert to a dataframe (mostly needed for type hinting to the compiler)
	 * @return the collector which can be used to Stream.of(X...).collect(annotatedCollector(X.class))
	 */
	@SuppressWarnings("unchecked")
	public static <X> Collector<X,?,RDataframe> annotatedCollector(Class<X> type) {
		List<MapRule<X>> rules = new ArrayList<>();
		for (Method m :type.getMethods()) {
			if(m.isAnnotationPresent(RName.class)) {
				rules.add(
					mapping(
						m.getAnnotation(RName.class).value(),
						o -> {
							try {
								return m.invoke(o);
							} catch (Exception e) {
								throw new RuntimeException(e);
							}
						})
				);
			}
		}
		return dataframeCollector((MapRule<X>[]) rules.toArray(new MapRule[0]));
	}
	
	/**
	 * A stream collector that collects a stream of maps and assembles it into a column
	 * major dataframe. The keys of the maps define the dataframe column and the values
	 * the column values. In this case each Map&lt;K,V&gt; represents a single row in the resulting
	 * dataframe. 
	 *
	 * @return A collector that works in a
	 *         streamOfMaps.collect(RConvert.mapsToDataFrame())
	 */
	public static Collector<Map<String,Object>,?,RDataframe> dataframeCollector() {
		return new Collector<Map<String,Object>,RDataframe,RDataframe>() {
	
			@Override
			public Supplier<RDataframe> supplier() {
				return () -> new RDataframe();
			}
	
			@Override
			public BiConsumer<RDataframe, Map<String,Object>> accumulator() {
				return (lhm, o) -> lhm.addRow(o);
			}
	
			@Override
			public BinaryOperator<RDataframe> combiner() {
				return (lhm,rhm) -> {
					synchronized(this) {
						RDataframe out = new RDataframe();
						out.bindRows(lhm);
						out.bindRows(rhm);
						return out;
					}
				};
			}
	
			@Override
			public Function<RDataframe, RDataframe> finisher() {
				return a -> a;
				
			}
	
			@Override
			public Set<Characteristics> characteristics() {
				return new HashSet<>(
						Arrays.asList(
								Characteristics.UNORDERED,
								Characteristics.CONCURRENT,
								Characteristics.IDENTITY_FINISH
								));
			}
	
			
			
		};
	}
	
	/**
	 * A stream collector that applies mapping rules to a stream of objects of arbitary type X and coverts a them
	 * into a dataframe. The mapping rules define a column name and a mapping function that takes an instance of X and
	 * generates a value. This function may just use a getter to pull a single value out or do an arbitatry complex operation on 
	 * a single X instance.
	 *
	 * @param       <X> the generic input
	 * @param rules - an array of mapping(X.class, "colname", x -&gt; x.getValue()) entries that define the data frame columns
	 * @return A collector that works in the following idiom: streamOfX.collect(RConvert.toDataFrame(mapping1, mapping2, ...))
	 */
	@SafeVarargs
	public static <X> Collector<X,?,RDataframe> dataframeCollector(final MapRule<X>... rules) {
		return new Collector<X,RDataframe,RDataframe>() {
	
			@Override
			public Supplier<RDataframe> supplier() {
				return () -> new RDataframe();
			}
	
			@Override
			public BiConsumer<RDataframe, X> accumulator() {
				return (lhm, o) -> {
					synchronized(lhm) {
						RNamedPrimitives tmp = new RNamedPrimitives();
						for (MapRule<X> rule: rules) {
							//TODO: this woudl possibly be quicker with an interim List<RVector<?>> collectors and a finisher which assembled them.
							//Maybe wouldn't make huge difference....
							Object tmp2 = rule.rule().apply(o);
							try {
								tmp.put(rule.label(), RConverter.convertObjectToPrimitive(tmp2));
							} catch (UnconvertableTypeException e) {
								// Fall back to string 
								tmp.put(rule.label(), RConverter.convertObjectToPrimitiveUnsafe(tmp2.toString()));
							}
						}
						lhm.addRow(tmp);
					}
				};
			}
	
			@Override
			public BinaryOperator<RDataframe> combiner() {
				return (lhm,rhm) -> {
					RDataframe out = new RDataframe();
					out.bindRows(lhm);
					out.bindRows(rhm);
					return out;
				};
			}
	
			@Override
			public Function<RDataframe, RDataframe> finisher() {
				return a -> a;
			}
	
			@Override
			public Set<Characteristics> characteristics() {
				return new HashSet<>(
						Arrays.asList(
								Characteristics.UNORDERED,
								Characteristics.CONCURRENT,
								Characteristics.IDENTITY_FINISH
								));
			}
		};
	}

	/**
	 * A stream collector that applies mapping rules and coverts a stream of objects
	 * into a dataframe.
	 *
	 * @param            <X> - the type of the object before the mapping
	 * @param            <W> - the interim type of the stream after the streamRule is applied
	 * @param streamRule - a lambda mapping an instance of clazz to a stream of W objects
	 * @param rules      - an array of mapping(W.class, "colname", w -&gt; w.getValue()) entries that define the data frame columns
	 * @return A collector that works in a stream.collect(RConvert.toDataFrame(flatMapping1, mapping1, flatMapping2, ...))
	 */
	@SafeVarargs
	public static <X,W> Collector<X,?,RDataframe> flatteningDataframeCollector(final StreamRule<X,W> streamRule, final MapRule<X>... rules) {
		return new Collector<X,RDataframe,RDataframe>() {
	
			@Override
			public Supplier<RDataframe> supplier() {
				return () -> new RDataframe();
			}
	
			@Override
			public BiConsumer<RDataframe, X> accumulator() {
				return (lhm, o) -> {
					synchronized(lhm) {
						RNamedPrimitives tmp = new RNamedPrimitives();
						for (MapRule<X> rule: rules) {
							//TODO: this woudl possibly be quicker with an interim List<RVector<?>> collectors and a finisher which assembled them.
							//Maybe wouldn't make huge difference....
							Object tmp2 = rule.rule().apply(o);
							try {
								tmp.put(rule.label(), RConverter.convertObjectToPrimitive(tmp2));
							} catch (UnconvertableTypeException e) {
								// Fall back to string 
								tmp.put(rule.label(), RConverter.convertObjectToPrimitiveUnsafe(tmp2.toString()));
							}
						}
						Stream<W> st = streamRule.streamRule().apply(o);
						st.forEach(w -> {
							RNamedPrimitives tmp2 = new RNamedPrimitives(tmp);
							for (MapRule<W> rule: streamRule.mapRules()) {
								Object tmp3 = rule.rule().apply(w);
								try {
									tmp2.put(rule.label(), RConverter.convertObjectToPrimitive(tmp3));
								} catch (UnconvertableTypeException e) {
									// Fall back to string 
									tmp2.put(rule.label(), RConverter.convertObjectToPrimitiveUnsafe(tmp3.toString()));
								}
							}
							lhm.addRow(tmp2);
						});
					}
				};
			}
	
			@Override
			public BinaryOperator<RDataframe> combiner() {
				return (lhm,rhm) -> {
					RDataframe out = new RDataframe();
					out.bindRows(lhm);
					out.bindRows(rhm);
					return out;
				};
			}
	
			@Override
			public Function<RDataframe, RDataframe> finisher() {
				return a -> a;
			}
	
			@Override
			public Set<Characteristics> characteristics() {
				return new HashSet<>(
						Arrays.asList(
								Characteristics.UNORDERED,
								Characteristics.CONCURRENT,
								Characteristics.IDENTITY_FINISH
								));
			}
		};
	}

	
	
	/**
	 * R quote. R strings can be either defined using single or double quotes.
	 * Quoting a string depends on which outer quotes are being used.
	 *
	 * @param in    the String that needs to be quoted
	 * @param quote the quote mark, either ' or "
	 * @return the string quoted
	 */
	public static String rQuote(String in, String quote) {
		String escaped = in;
	    escaped = escaped.replace("\\", "\\\\");
	    escaped = escaped.replace(quote, "\\"+quote);
	    escaped = escaped.replace("\b", "\\b");
	    escaped = escaped.replace("\f", "\\f");
	    escaped = escaped.replace("\n", "\\n");
	    escaped = escaped.replace("\r", "\\r");
	    escaped = escaped.replace("\t", "\\t");
	    // TODO: escape other non-printing characters using uXXXX notation
	    return quote+escaped+quote;
	}
	
	/**
	 * Unconvert a.
	 *
	 * @param rPrimitive the r primitive
	 * @return the object
	 */
	public static Object unconvert(RPrimitive rPrimitive) {
		return rPrimitive.get();
	}
	
	/**
	 * Unconvert.
	 *
	 * @param rObject the r object
	 * @return the object
	 */
	public static Object unconvert(RObject rObject) {
		if (rObject instanceof RPrimitive) return unconvert((RPrimitive) rObject);
		if (rObject instanceof RVector) return unconvert((RVector<?>) rObject);
		if (rObject instanceof RList) return unconvert((RList) rObject);
		if (rObject instanceof RNamedList) return unconvert((RNamedList) rObject);
		throw new IncompatibleTypeException("could not unconvert "+rObject.getClass().getSimpleName());
	}
	
	
	
	/**
	 * Unconvert an R vector to a list of undefined objects (which will be a java primitive, or local date, or similar).
	 *
	 * @param rVector the r vector
	 * @return the list
	 */
	public static List<Object> unconvert(RVector<?> rVector) {
		return rVector.stream().map(x -> RConverter.unconvert(x)).collect(Collectors.toList());
	}
	
	/**
	 * Unconvert an R list to a list of undefined objects (which will be a java primitive, or local date, or similar).
	 *
	 * @param rVector the r vector
	 * @return the list
	 */
	public static List<Object> unconvert(RList rVector) {
		return rVector.stream().map(x -> RConverter.unconvert(x)).collect(Collectors.toList());
	}
	
	/**
	 * Unconvert an R named list to a map of named undefined objects (which will be a java primitive, or local date, or similar).
	 *
	 * @param rVector the r vector
	 * @return the map
	 */
	public static Map<String,Object> unconvert(RNamedList rVector) {
		HashMap<String,Object> out = new HashMap<>();
		rVector.stream().forEach(x -> out.put(x.getKey(), RConverter.unconvert(x.getValue())));
		return out;
	}
	
	/**
	 * Create a mapping using a to allow us to extract data from an object of type
	 * defined by clazz and associate it with a label. This can be used to create a
	 * custom data mapping. e.g.
	 * 
	 * mapping("absolutePath", f -&gt; f.getAbsolutePath())
	 * 
	 * If the compiler cannot work out the type from the context it may be necessary
	 * to use the 3 parameter version of this method.
	 *
	 * @param       <Z> - the input type that is being mapped
	 * @param label - the target column label in the R dataframe
	 * @param rule  - a lambda mapping an instance of clazz to the value of the
	 *              column
	 * @return a mapping rule
	 */
	public static <Z> MapRule<Z> mapping(final String label, final Function<Z,Object> rule) {
		return new MapRule<Z>() {
			@Override
			public String label() {
				return label;
			}
			@Override
			public Function<Z, Object> rule() {
				return rule;
			}
		};
	}
	
	/**
	 * Create a mapping using a to allow us to extract a set of items of type W from object of type
	 * Z and flatten a nested data structure, and then mapping each W to columns in a data frame by associated 
	 * a column name with a function that extracts a value from W. This can be used to create a custom data mapping. e.g.
	 * 
	 * 	flatMapping(dir -&gt; dir.getFiles().stream(), 
	 * 		mapping("absolutePath", f -&gt; f.getAbsolutePath()),
	 * 		mapping("readable", f -&gt; f.canRead())
	 *  )
	 * 
	 * 
	 * If the compiler cannot work out the type from the context it may be necessary
	 * to use the 3 parameter version of this method.
	 *
	 * @param            <Z> - the input type
	 * @param            <W> - the nested type that will form the input for the
	 *                   nested rules
	 * @param streamRule - a lambda mapping an instance of clazz to a stream of W
	 *                   objects
	 * @param rule       - the rules mapping W to final data frame outputs
	 * @return a nested mapping rule
	 */
	@SafeVarargs
	public static <Z,W> StreamRule<Z,W> flatMapping(final Function<Z,Stream<W>> streamRule, final MapRule<W>... rule) {
		return new StreamRule<Z,W>() {

			@Override
			public Function<Z, Stream<W>> streamRule() {
				return streamRule;
			}

			@Override
			public List<MapRule<W>> mapRules() {
				return Arrays.asList(rule);
			}
			
		};
		
	}
	
	/**
	 * Create a mapping using a to allow us to extract data from an object of type
	 * defined by clazz and associate it with a label. This can be used to create a
	 * custom data mapping. This 3 parameter form is sometime needed if the compiler has lost the
	 * plot and we need to give it some help e.g.
	 * 
	 * mapping(File.class, "absolutePath", f -&gt; f.getAbsolutePath())
	 *
	 * @param       <Z> - the input type
	 * @param clazz - a class maybe required to guide the compiler to use the correct lambda function
	 * @param label - the target column label in the R dataframe
	 * @param rule  - a lambda mapping an instance of clazz to the value of the column
	 * @return a set of nested mapping rules
	 */
	public static <Z> MapRule<Z> mapping(final Class<Z> clazz, final String label, final Function<Z,Object> rule) {
		return mapping(label,rule);
	}
	
}
