package uk.co.terminological.rjava.types;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.invoke.MethodHandles.Lookup;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import uk.co.terminological.rjava.RName;
import uk.co.terminological.rjava.UnconvertableTypeException;

/**
 * A RBoundDataframe is a dataframe bound to an annotated POJO interface type.
 *
 * @param <X> the generic type
 * @author vp22681
 * @version $Id: $Id
 */
public class RBoundDataframe<X> extends RDataframe {

	private Class<X> type;
	private transient Map<Method,Function<RDataframeRow,RPrimitive>> getterMap;
	private transient Map<Method,Function<RDataframeRow,RPrimitive>> setterMap;
	private transient List<Function<RDataframeRow,RPrimitive>> paramList;
	private boolean strict;

	/**
	 * <p>Constructor for RBoundDataframe.</p>
	 *
	 * @param interfaceType a {@link java.lang.Class} object
	 * @param dataframe a {@link uk.co.terminological.rjava.types.RDataframe} object
	 * @throws uk.co.terminological.rjava.UnconvertableTypeException if any.
	 */
	public RBoundDataframe(Class<X> interfaceType, RDataframe dataframe) throws UnconvertableTypeException {
		this(interfaceType,dataframe,true);
	}

	private boolean isJavaBean(Class<X> type) {
		if (type.isInterface()) return false;
		// if (type.getConstructors().length == 0) return true;
		try {
			type.getConstructor(); 
			return true;
		} catch (Exception e) {}
		return false;
	}
	
	private boolean isRecordStyle(Class<X> type) {
		if (type.isInterface()) return false;
		if (type.getDeclaredConstructors().length == 1 && this.setterMap.isEmpty()) return true;
		return false;
	}
	
	/**
	 * <p>Constructor for RBoundDataframe.</p>
	 *
	 * @param interfaceType a {@link java.lang.Class} object
	 * @param dataframe a {@link uk.co.terminological.rjava.types.RDataframe} object
	 * @param strict a boolean
	 * @throws uk.co.terminological.rjava.UnconvertableTypeException if any.
	 */
	public RBoundDataframe(Class<X> interfaceType, RDataframe dataframe, boolean strict) throws UnconvertableTypeException {
		super(dataframe);
		this.type = interfaceType;
		this.createMap();
		this.strict = strict;
		if (!interfaceType.isInterface() && !isJavaBean(interfaceType) &&!isRecordStyle(interfaceType)) {
			 throw new UnconvertableTypeException("Data frame cannot be bound to a `"+interfaceType.getName()+"` which must be either an interface or a Java Bean class or a Java 17 record.");
		}
	};

	private static Pattern getSet = Pattern.compile("^(?:g|s)et([A-Z])");
	protected static String fieldName(String methodName) {
		Matcher m = getSet.matcher(methodName);

		StringBuilder sb = new StringBuilder();
		int last = 0;

		while (m.find()) {
			sb.append(methodName.substring(last, m.start()));
			sb.append(m.group(1).toLowerCase());
			last = m.end();
		}
		sb.append(methodName.substring(last));
		return sb.toString();
	}

	private void createMap() throws UnconvertableTypeException {
		createGetterMap();
		createSetterMap();
		createParamList();
	}
	
	@SuppressWarnings("unchecked")
	private void createGetterMap() throws UnconvertableTypeException {
		// if(!type.isInterface()) throw new UnsupportedOperationException("type must be an interface: "+type.getCanonicalName());
		getterMap = new HashMap<>();
		for (Method m : type.getMethods()) {
			if(
					!m.isDefault() && 
					RPrimitive.class.isAssignableFrom(m.getReturnType()) &&
					m.getParameterCount() == 0 					
					) {
				String colName;
				if (m.isAnnotationPresent(RName.class)) {
					colName = m.getAnnotation(RName.class).value();
				} else {
					colName = fieldName(m.getName());
				}

				// Check the column exists
				if (!this.containsKey(colName)) {
					if (strict) {
						throw new UnconvertableTypeException("Expected column '"+colName+"' but it was missing from this dataframe.");
					} else {
						if (!RPrimitive.class.isAssignableFrom(m.getReturnType())) throw new UnconvertableTypeException("Interface methods must extend from primitive R types");
						getterMap.put(m, 
								o -> RPrimitive.na((Class<? extends RPrimitive>) m.getReturnType())
								);
					}

				} else {

					// Check its of the right type
					if (!m.getReturnType().isAssignableFrom(this.getTypeOfColumn(colName))) throw new UnconvertableTypeException(
							"The type of column: "+colName+" is not compatible. It is a "+
									this.getTypeOfColumn(colName).getSimpleName()+" and we wanted a "+
									m.getReturnType().getSimpleName()
							);

					getterMap.put(m, 
							o -> (RPrimitive) m.getReturnType().cast(o.get(colName))
							);
				}
			}
		}
		
	}
	
	@SuppressWarnings("unchecked")
	private void createSetterMap() throws UnconvertableTypeException {
		// if(!type.isInterface()) throw new UnsupportedOperationException("type must be an interface: "+type.getCanonicalName());
		setterMap = new HashMap<>();
		for (Method m : type.getMethods()) {
			if(
					m.getParameterCount() == 1 &&
					RPrimitive.class.isAssignableFrom(m.getParameters()[0].getType())
					) {
				String colName;
				if (m.isAnnotationPresent(RName.class)) {
					colName = m.getAnnotation(RName.class).value();
				} else {
					colName = fieldName(m.getName());
				}

				// Check the column exists
				if (!this.containsKey(colName)) {
					if (strict) {
						throw new UnconvertableTypeException("Expected column '"+colName+"' but it was missing from this dataframe.");
					} else {
						if (!RPrimitive.class.isAssignableFrom(m.getParameters()[0].getType())) throw new UnconvertableTypeException("Interface methods must extend from primitive R types");
						setterMap.put(m, 
								o -> RPrimitive.na((Class<? extends RPrimitive>) m.getReturnType())
								);
					}

				} else {

					// Check its of the right type
					if (!m.getParameters()[0].getType().isAssignableFrom(this.getTypeOfColumn(colName))) throw new UnconvertableTypeException(
							"The type of column: "+colName+" is not compatible. It is a "+
									this.getTypeOfColumn(colName).getSimpleName()+" and we wanted a "+
									m.getReturnType().getSimpleName()
							);

					setterMap.put(m, 
							o -> (RPrimitive) m.getParameters()[0].getType().cast(o.get(colName))
							);
				}
			}
		}
		
	}
	
	@SuppressWarnings("unchecked")
	private void createParamList() throws UnconvertableTypeException {
		paramList = new ArrayList<>();
		if (type.getConstructors().length == 1) {
			Constructor<?> constructor = type.getConstructors()[0];
			for (Parameter p :constructor.getParameters()) {
				RName annotation = p.getAnnotation(RName.class);
				if (annotation == null) throw new UnconvertableTypeException("Record or immutable classes must have a constructor with all parameters with @RName anntoations");
				String colName = annotation.value();
				if (!this.containsKey(colName)) {
					if (strict) {
						throw new UnconvertableTypeException("Expected column '"+colName+"' but it was missing from this dataframe.");
					} else {
						if (!RPrimitive.class.isAssignableFrom(p.getType())) throw new UnconvertableTypeException("Interface methods must extend from primitive R types");
						paramList.add( 
								o -> RPrimitive.na((Class<? extends RPrimitive>) p.getType())
								);
					}
				} else {
					if (!p.getType().isAssignableFrom(this.getTypeOfColumn(colName))) throw new UnconvertableTypeException(
						"The type of column: "+colName+" is not compatible. It is a "+
								this.getTypeOfColumn(colName).getSimpleName()+" and we wanted a "+
								p.getType().getSimpleName()
						);	
					paramList.add(o -> (RPrimitive) p.getType().cast(o.get(colName)));
				}
			}
		}
	}

	private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException {
		ois.defaultReadObject(); // Calling the default deserialization logic
		try {
			this.createMap();
		} catch (UnconvertableTypeException e) {
			throw new IOException("This should not have happened",e);
		}
	}

	/**
	 * <p>getCoercedRow.</p>
	 *
	 * @param i a int
	 * @return a X object
	 */
	public X getCoercedRow(int i) {
		return coerce(this.getRow(i));
	}

	/** {@inheritDoc} */
	public RBoundDataframeRow<X> getRow(int i) {
		return new RBoundDataframeRow<X>(this, i);
	}

	/**
	 * <p>streamCoerce.</p>
	 *
	 * @return a {@link java.util.stream.Stream} object
	 */
	public Stream<X> streamCoerce() {
		return super.stream().map(this::coerce);
	}

	protected X coerce(RDataframeRow nl) {
		if (type.isInterface()) return proxyFrom(nl);
		if (isRecordStyle(type)) return recordFrom(nl);
		return beanFrom(nl);
	}
	
	@SuppressWarnings("unchecked")
	protected X recordFrom(RDataframeRow nl) {
		X out;
		Object[] args = paramList.stream()
				.map(fn -> fn.apply(nl))
				.collect(Collectors.toList())
				.toArray();
		try {
			out = (X) type.getDeclaredConstructors()[0].newInstance(args);
			return out;
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException
				| InvocationTargetException | SecurityException e) {
			throw new RuntimeException("`type` should already have been checked to contain a single public constructor of the correct signature", e);
		}
	}
	
	protected X beanFrom(RDataframeRow nl) {
		X out;
		try {
			out = type.getDeclaredConstructor().newInstance();
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException | SecurityException e) {
			throw new RuntimeException("`type` should already have been checked to contain a public no-args constructor", e);
		}
		for (Method m : type.getMethods()) {
			if (setterMap.containsKey(m)) {
				RPrimitive value = setterMap.get(m).apply(nl);
				try {
					m.invoke(out, value);
				} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
					throw new RuntimeException("setter methods on `type` should be public", e);
				}
			}
		}
		return out;
	}
	
	/**
	 * <p>proxyFrom.</p>
	 *
	 * @param nl a {@link uk.co.terminological.rjava.types.RDataframeRow} object
	 * @return a X object
	 */
	@SuppressWarnings("unchecked")
	protected X proxyFrom(RDataframeRow nl) {
		return (X) Proxy.newProxyInstance(type.getClassLoader(), new Class[] {type}, new InvocationHandler() {
			@Override
			public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
				if (method.isDefault()) {
					// https://stackoverflow.com/questions/22614746/how-do-i-invoke-java-8-default-methods-reflectively
					// https://blog.jooq.org/2018/03/28/correct-reflective-access-to-interface-default-methods-in-java-8-9-10/
					// maybe good idea to use jOOQ
					final float version = Float.parseFloat(System.getProperty("java.class.version"));
					if (version <= 52) {
						//Java 8
						Constructor<Lookup> constructor = Lookup.class
								.getDeclaredConstructor(Class.class);
						constructor.setAccessible(true);
						return constructor.newInstance(type)
								.in(type)
								.unreflectSpecial(method, type)
								.bindTo(proxy)
								.invokeWithArguments();
					} else {
						//Java 9 onwards
						return MethodHandles
								.lookup()
								.findSpecial(
										type, 
										method.getName(),
										MethodType.methodType(method.getReturnType(), method.getParameterTypes()), 
										type)
								.bindTo(proxy)
								.invokeWithArguments(args);
					}
				}
				if (method.getName().equals("toString")) return "bound "+type.getSimpleName()+": "+nl.asCsv().trim();
				// don't support any other methods (n.b. hashcode and equals)
				if (!getterMap.containsKey(method)) throw new UnsupportedOperationException();
				RPrimitive value = getterMap.get(method).apply(nl);
				return value;
			}
		});
	}
}
