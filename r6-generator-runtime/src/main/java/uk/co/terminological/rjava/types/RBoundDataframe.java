package uk.co.terminological.rjava.types;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.invoke.MethodHandles.Lookup;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Stream;

import uk.co.terminological.rjava.RName;
import uk.co.terminological.rjava.UnconvertableTypeException;

/**
 * A RBoundDataframe is a datafram bount to an annotated POJO interface type.
 *
 * @param <X> the generic type
 */
public class RBoundDataframe<X> extends RDataframe {

	private Class<X> type;
	private transient Map<Method,Function<RDataframeRow,RPrimitive>> methodMap;
	private boolean strict;

	public RBoundDataframe(Class<X> interfaceType, RDataframe dataframe) throws UnconvertableTypeException {
		this(interfaceType,dataframe,true);
	}
	
	public RBoundDataframe(Class<X> interfaceType, RDataframe dataframe, boolean strict) throws UnconvertableTypeException {
		super(dataframe);
		this.type = interfaceType;
		this.methodMap = this.createMap();
		this.strict = strict;
	};
	
	
	
	@SuppressWarnings("unchecked")
	private Map<Method,Function<RDataframeRow,RPrimitive>> createMap() throws UnconvertableTypeException {
		if(!type.isInterface()) throw new UnsupportedOperationException("type must be an interface: "+type.getCanonicalName());
		Map<Method,Function<RDataframeRow,RPrimitive>> methodMap = new HashMap<>();
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
					colName = m.getName();
				}
				
				// Check the column exists
				if (!this.containsKey(colName)) {
					if (strict) {
						throw new UnconvertableTypeException("Expected column '"+colName+"' but it was missing from this dataframe.");
					} else {
						if (!RPrimitive.class.isAssignableFrom(m.getReturnType())) throw new UnconvertableTypeException("Interface methods must extend from primitive R types");
						methodMap.put(m, 
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
					
					methodMap.put(m, 
						o -> (RPrimitive) m.getReturnType().cast(o.get(colName))
					);
				}
			}
		}
		return methodMap;
	}
	
    private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException {
        ois.defaultReadObject(); // Calling the default deserialization logic
        try {
			this.methodMap = this.createMap();
		} catch (UnconvertableTypeException e) {
			throw new IOException("This should not have happened",e);
		}
    }
    
    public X getCoercedRow(int i) {
    	return proxyFrom(this.getRow(i));
    }
    
    public RBoundDataframeRow<X> getRow(int i) {
    	return new RBoundDataframeRow<X>(this, i);
    }
    
    public Stream<X> streamCoerce() {
    	return super.stream().map(this::proxyFrom);
    }
    
    @SuppressWarnings("unchecked")
	protected X proxyFrom(RDataframeRow nl) {
    	return (X) Proxy.newProxyInstance(type.getClassLoader(), new Class[] {type}, new InvocationHandler() {
			@Override
			public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
				if (method.isDefault()) {
					// https://stackoverflow.com/questions/22614746/how-do-i-invoke-java-8-default-methods-reflectively
					// https://blog.jooq.org/2018/03/28/correct-reflective-access-to-interface-default-methods-in-java-8-9-10/
					// maybe good idea to use jOOQ
					try {
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
					} catch (IllegalAccessException e) {
						//Java 8
						Constructor<Lookup> constructor = Lookup.class
			                    .getDeclaredConstructor(Class.class);
			                constructor.setAccessible(true);
			                return constructor.newInstance(type)
			                    .in(type)
			                    .unreflectSpecial(method, type)
			                    .bindTo(proxy)
			                    .invokeWithArguments();
					}
					
				}
				if (method.getName().equals("toString")) return "bound "+type.getSimpleName()+": "+nl.asCsv().trim();
				// don't support any other methods (n.b. hashcode and equals)
				if (!methodMap.containsKey(method)) throw new UnsupportedOperationException();
				RPrimitive value = methodMap.get(method).apply(nl);
				return value;
			}
		});
    }
}
