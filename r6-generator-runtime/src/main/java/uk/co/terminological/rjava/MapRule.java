package uk.co.terminological.rjava;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

import uk.co.terminological.rjava.types.RNull;
import uk.co.terminological.rjava.types.RObject;



/**
 * The MapRule interface allows a label to be associated with a data mapping
 * @author terminological
 *
 * @param <Y> - the input data type that will be mapped
 */
public interface MapRule<Y> {
	
	String label();
	Function<Y,Object> rule();
	
	@SuppressWarnings("unchecked")
	static <X> MapRule<X>[] reflectionRules(Class<X> clazz) {
		List<MapRule<X>> out = new ArrayList<>();
		for (Method method: clazz.getMethods()) {
			if (method.isAccessible() &&
					method.getParameterCount() == 0 
					&& (
							ConvertibleTypes.contains(method.getReturnType())
							|| RObject.class.isAssignableFrom(method.getReturnType()) 
						)
					) {
				out.add(MapRule.mapping(method));
			}
		}
		return (MapRule<X>[]) out.toArray(new MapRule[out.size()]);
	}

	/**
	 * generates a mapping using reflection from a method specification
	 * @param method - the java method
	 * @param <Z> the input type
	 * @return - a mapping that associates the method name with the method. 
	 */
	static <Z> MapRule<Z> mapping(final Method method) {
		return new MapRule<Z>() {

			@Override
			public String label() {
				return method.getName();
			}

			@Override
			public Function<Z, Object> rule() {
				return new Function<Z,Object>() {
					@Override
					public RObject apply(Z t) {
						try {
							return RConverter.convertObject(method.invoke(t));
						} catch (IllegalAccessException | IllegalArgumentException |InvocationTargetException e) {
							throw new RuntimeException(e);
						} catch (UnconvertableTypeException e) {
							return new RNull();
						}
					}
				};
			}
		};
	}
	
	static List<Class<?>> ConvertibleTypes = Arrays.asList(
		String.class,
		Integer.class,
		Enum.class,
		BigDecimal.class,
		Float.class,
		Long.class,
		Double.class,
		Boolean.class,
		LocalDate.class,
		int[].class,
		double[].class,
		boolean[].class,
		String[].class
	);
}