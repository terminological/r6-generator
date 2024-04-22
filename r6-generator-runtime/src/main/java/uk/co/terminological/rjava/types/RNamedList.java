package uk.co.terminological.rjava.types;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import uk.co.terminological.rjava.IncompatibleTypeException;
import uk.co.terminological.rjava.RConverter;
import uk.co.terminological.rjava.RDataType;
import uk.co.terminological.rjava.UnconvertableTypeException;
import uk.co.terminological.rjava.utils.RObjectVisitor;

/**
 * The R named list is a flexible untyped map rather like a JSON document. Any kind of content can be included
 * (as long as it is wrapped as an {@link uk.co.terminological.rjava.types.RObject}). Using content from lists will require type checking.
 *
 * @author terminological
 * @version $Id: $Id
 */
@RDataType(
		JavaToR = {
				"function(jObj) {",
				// get java to serialise code into an evaluatable bit of R code
				"	tmp = eval(parse(text=rJava::.jcall(jObj,'rCode', returnSig='Ljava/lang/String;')))",
				"	return(tmp)",
				"}"
		},
		RtoJava = {
				"function(rObj) {",
				"	if (!is.list(rObj) | is.null(names(rObj))) stop ('expecting a named list')",
				"	jout = rJava::.jnew('~RNAMEDLIST~')",
				"	lapply(names(rObj), function(name) {",
				"		x = rObj[[name]]",
				// If x is a dataframe dispatch it to ~TO_RDATAFRAME~
				"		if (is.null(x)) tmp = ~TO_RNULL~(x)",
				"		else if (is.data.frame(x)) tmp = ~TO_RDATAFRAME~(x)",
				"		else if (is.list(x) & !is.null(names(x))) tmp = ~TO_RNAMEDLIST~(x)",
				"		else if (is.list(x)) tmp = ~TO_RLIST~(x)",
				// TODO: add in matrix
				"		else if (is.array(x) & is.numeric(x)) tmp = ~TO_RNUMERICARRAY~(x)",
				// Length one
				"		else if (length(x) == 1 & is.character(x)) tmp = ~TO_RCHARACTER~(x)",
				"		else if (length(x) == 1 & is.integer(x)) tmp = ~TO_RINTEGER~(x)",
				"		else if (length(x) == 1 & is.factor(x)) tmp = ~TO_RFACTOR~(x)",
				"		else if (length(x) == 1 & is.logical(x)) tmp = ~TO_RLOGICAL~(x)",
				"		else if (length(x) == 1 & is.numeric(x)) tmp = ~TO_RNUMERIC~(x)",
				"		else if (length(x) == 1 & inherits(x,c('Date','POSIXt'))) tmp = ~TO_RDATE~(x)",
				// Vectors
				"		else if (is.character(x)) tmp = ~TO_RCHARACTERVECTOR~(x)",
				"		else if (is.integer(x)) tmp = ~TO_RINTEGERVECTOR~(x)",
				"		else if (is.factor(x)) tmp = ~TO_RFACTORVECTOR~(x)",
				"		else if (is.logical(x)) tmp = ~TO_RLOGICALVECTOR~(x)",
				"		else if (is.numeric(x)) tmp = ~TO_RNUMERICVECTOR~(x)",
				"		else if (inherits(x,c('Date','POSIXt'))) tmp = ~TO_RDATEVECTOR~(x)",
				// If x is a dataframe dispatch it to ~TO_RDATAFRAME~
				// could issue a warning and put in a null?
				"		else stop ('unrecognised type: ',class(x),' with value ',x)",
				// Add to list
				"		rJava::.jcall(jout,returnSig='L~ROBJECT~;',method='put',name,rJava::.jcast(tmp, new.class='~ROBJECT~'))",
				"	})",
				"	return(jout)",
				"}"
		}//,
		//JNIType = "D"
	)
public class RNamedList extends LinkedHashMap<String, RObject> implements RCollection<RNamed<?>>  {

	private static final long serialVersionUID = RObject.datatypeVersion;
	
	/**
	 * <p>put.</p>
	 *
	 * @param s a {@link java.lang.String} object
	 * @param o a {@link uk.co.terminological.rjava.types.RObject} object
	 * @return a {@link uk.co.terminological.rjava.types.RObject} object
	 */
	public RObject put(String s,RObject o) {
		return super.put(s, o);
	}
	
	/**
	 * <p>putRaw.</p>
	 *
	 * @param s a {@link java.lang.String} object
	 * @param o a {@link java.lang.Object} object
	 * @return a {@link uk.co.terminological.rjava.types.RObject} object
	 * @throws uk.co.terminological.rjava.UnconvertableTypeException if any.
	 */
	public RObject putRaw(String s,Object o) throws UnconvertableTypeException {
		return super.put(s, RConverter.convertObject(o));
	}
		
	/** {@inheritDoc} */
	@Override
	public Iterator<RNamed<?>> iterator() {
		return new Iterator<RNamed<?>>() {
			Iterator<java.util.Map.Entry<String, RObject>> it = RNamedList.this.entrySet().iterator();
			@Override
			public boolean hasNext() {
				return it.hasNext();
			}

			@Override
			public RNamed<?> next() {
				return RNamed.from(it.next());
			}
		};
	}

	/** {@inheritDoc} */
	@Override
	public String rCode() {
		return "list("+this.entrySet().stream().map(kv -> kv.getKey()+"="+kv.getValue().rCode()).collect(Collectors.joining(", "))+")";
	}
	
	/**
	 * <p>andRaw.</p>
	 *
	 * @param s a {@link java.lang.String} object
	 * @param o a {@link java.lang.Object} object
	 * @return a {@link uk.co.terminological.rjava.types.RNamedList} object
	 * @throws uk.co.terminological.rjava.UnconvertableTypeException if any.
	 */
	public RNamedList andRaw(String s, Object o) throws UnconvertableTypeException {
		this.putRaw(s, o);
		return this;
	}
	
	/**
	 * <p>withRaw.</p>
	 *
	 * @param s a {@link java.lang.String} object
	 * @param o a {@link java.lang.Object} object
	 * @return a {@link uk.co.terminological.rjava.types.RNamedList} object
	 * @throws uk.co.terminological.rjava.UnconvertableTypeException if any.
	 */
	public static RNamedList withRaw(String s, Object o) throws UnconvertableTypeException {
		RNamedList out = new RNamedList();
		out.andRaw(s,o);
		return out;
	}

	/** {@inheritDoc} */
	@Override
	public <X> X accept(RObjectVisitor<X> visitor) {
		X out = visitor.visit(this);
		this.iterator().forEachRemaining(c -> c.accept(visitor));
		return out;
	}

	/**
	 * <p>and.</p>
	 *
	 * @param s a {@link java.lang.String} object
	 * @param o a {@link uk.co.terminological.rjava.types.RObject} object
	 * @return a {@link uk.co.terminological.rjava.types.RNamedList} object
	 */
	public RNamedList and(String s, RObject o) {
		this.put(s, o);
		return this;
	}
	
	/**
	 * <p>with.</p>
	 *
	 * @param s a {@link java.lang.String} object
	 * @param o a {@link uk.co.terminological.rjava.types.RObject} object
	 * @return a {@link uk.co.terminological.rjava.types.RNamedList} object
	 */
	public static RNamedList with(String s, RObject o) {
		RNamedList out = new RNamedList();
		out.and(s,o);
		return out;
	}
	
	/**
	 * <p>stream.</p>
	 *
	 * @param subtype a {@link java.lang.Class} object
	 * @param <X> a X class
	 * @return a {@link java.util.stream.Stream} object
	 */
	public <X extends RObject> Stream<X> stream(Class<X> subtype) {
		try { 
			return this.stream().map(x -> subtype.cast(x.getValue()));
		} catch (ClassCastException e) {
			throw new IncompatibleTypeException(e.getMessage());
		}
		
	}
	
	/**
	 * <p>streamNode.</p>
	 *
	 * @param key a {@link java.lang.String} object
	 * @return a {@link java.util.stream.Stream} object
	 */
	public Stream<RObject> streamNode(String key) {
		RObject node = this.get(key);
		if (node == null) return Stream.empty();
		if (node instanceof RPrimitive) return Stream.of((RPrimitive) node);
		if (node instanceof RVector) return ((RVector<?>) node).stream().map(x -> (RPrimitive) x);
		if (node instanceof RList) return ((RList) node).stream();
		if (node instanceof RNamedList) return Stream.of((RNamedList) node);
		if (node instanceof RArray) return ((RArray<?>) node).stream();
		return Stream.empty();
	}
	
	/**
	 * <p>streamPath.</p>
	 *
	 * @param keys a {@link java.lang.String} object
	 * @return a {@link java.util.stream.Stream} object
	 */
	public Stream<RObject> streamPath(String... keys) {
		Stream<RObject> out = Stream.of(this);
		for (String key: keys) {
			out = out.flatMap(t -> ((RNamedList) t).streamNode(key));
		}
		return out;
	}
	
	/**
	 * <p>asString.</p>
	 *
	 * @param key a {@link java.lang.String} object
	 * @return a {@link java.util.Optional} object
	 */
	public Optional<String> asString(String key) {
		RObject tmp = this.get(key);
		if (tmp instanceof RCharacter) return ((RCharacter) tmp).opt();
		else return Optional.empty();
	}
	
	/**
	 * <p>asDouble.</p>
	 *
	 * @param key a {@link java.lang.String} object
	 * @return a {@link java.util.Optional} object
	 */
	public Optional<Double> asDouble(String key) {
		RObject tmp = this.get(key);
		if (tmp instanceof RNumeric) return ((RNumeric) tmp).opt();
		else return Optional.empty();
	}
	
	/**
	 * <p>asInteger.</p>
	 *
	 * @param key a {@link java.lang.String} object
	 * @return a {@link java.util.Optional} object
	 */
	public Optional<Integer> asInteger(String key) {
		RObject tmp = this.get(key);
		if (tmp instanceof RInteger) return ((RInteger) tmp).opt();
		else return Optional.empty();
	}
	
	//TODO: Other types
	
	/**
	 * <p>asStringVector.</p>
	 *
	 * @param key a {@link java.lang.String} object
	 * @return a {@link java.util.stream.Stream} object
	 */
	public Stream<String> asStringVector(String key) {
		RObject tmp = this.get(key);
		if (tmp instanceof RCharacterVector) return ((RCharacterVector) tmp).get();
		else return Stream.empty();
	}
	
	/**
	 * <p>asDoubleVector.</p>
	 *
	 * @param key a {@link java.lang.String} object
	 * @return a {@link java.util.stream.Stream} object
	 */
	public Stream<Double> asDoubleVector(String key) {
		RObject tmp = this.get(key);
		if (tmp instanceof RNumericVector) return ((RNumericVector) tmp).get();
		else return Stream.empty();
	}
	
	/**
	 * <p>asIntegerVector.</p>
	 *
	 * @param key a {@link java.lang.String} object
	 * @return a {@link java.util.stream.Stream} object
	 */
	public Stream<Integer> asIntegerVector(String key) {
		RObject tmp = this.get(key);
		if (tmp instanceof RIntegerVector) return ((RIntegerVector) tmp).get();
		else return Stream.empty();
	}

	/**
	 * <p>asMap.</p>
	 *
	 * @return a {@link java.util.Map} object
	 */
	public Map<String,Object> asMap() {
		Map<String,Object> out = new HashMap<>();
		this.entrySet().stream().forEach(kv -> out.put(kv.getKey(), RConverter.unconvert(kv.getValue())));
		return out;
	}

	/**
	 * <p>create.</p>
	 *
	 * @return a {@link uk.co.terminological.rjava.types.RNamedList} object
	 */
	public static RNamedList create() {
		return new RNamedList();
	}

	/**
	 * <p>getAs.</p>
	 *
	 * @param string a {@link java.lang.String} object
	 * @param type a {@link java.lang.Class} object
	 * @param <X> a X class
	 * @return a X object
	 * @throws uk.co.terminological.rjava.IncompatibleTypeException if any.
	 */
	public <X extends RObject> X getAs(String string, Class<X> type) throws IncompatibleTypeException {
		try {
			return type.cast(this.get(string));
		} catch (ClassCastException e) {
			throw new IncompatibleTypeException("Expected a "+type.getSimpleName()+" and found a "+this.get(string).getClass().getSimpleName());
		}
	}
	
	/**
	 * <p>toString.</p>
	 *
	 * @return a {@link java.lang.String} object
	 */
	public String toString() {
		return "<rnamedlist>{\n"+this.entrySet().stream().map(kv -> kv.getKey()+": "+kv.getValue() ).collect(Collectors.joining(",\n"))+"\n}";
	}
}
