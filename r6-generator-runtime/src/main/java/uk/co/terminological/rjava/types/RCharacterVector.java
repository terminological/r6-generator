package uk.co.terminological.rjava.types;

import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import uk.co.terminological.rjava.RDataType;
import uk.co.terminological.rjava.utils.RObjectVisitor;

/**
 * A java representation of an R character vector. Factory methods are in {@link uk.co.terminological.rjava.types.RVector}.
 *
 * @author terminological
 * @version $Id: $Id
 */
@RDataType(
		JavaToR = {
				"function(jObj) as.character(rJava::.jcall(jObj,returnSig='[Ljava/lang/String;',method='rPrimitive'))",
		},
		RtoJava = {
				"function(rObj) {",
				"	if (is.null(rObj)) return(rJava::.jnew('~RCHARACTERVECTOR~'))",
				"	if (!is.character(rObj)) stop('expected a vector of characters')",
				"	tmp = as.character(rObj)",
				"	return(rJava::.jnew('~RCHARACTERVECTOR~',rJava::.jarray(tmp)))",
				"}"
		}
		//JNIType = "[[C"
	)
public class RCharacterVector extends RVector<RCharacter> implements JNIPrimitive {
	
	private static final long serialVersionUID = RObject.datatypeVersion;
	
	/**
	 * <p>Constructor for RCharacterVector.</p>
	 *
	 * @param primitives an array of {@link java.lang.String} objects
	 */
	public RCharacterVector(String[] primitives) {
		super(primitives.length);
		for (int i=0; i<primitives.length; i++) this.add(new RCharacter(primitives[i]));
	}
	/**
	 * <p>Constructor for RCharacterVector.</p>
	 */
	public RCharacterVector() {super();}
	/**
	 * <p>Constructor for RCharacterVector.</p>
	 *
	 * @param length a int
	 */
	public RCharacterVector(int length) {super(length);}
	
	/**
	 * <p>rPrimitive.</p>
	 *
	 * @return an array of {@link java.lang.String} objects
	 */
	public String[] rPrimitive() {
		return this.stream().map(ri -> ri.rPrimitive()).collect(Collectors.toList()).toArray(new String[] {});
	}
	
	/** {@inheritDoc} */
	@Override
	public Class<RCharacter> getType() {
		return RCharacter.class;
	}
	
	/** {@inheritDoc} */
	@Override
	public <X> X accept(RObjectVisitor<X> visitor) {
		X out = visitor.visit(this);
		this.forEach(c -> c.accept(visitor));
		return out;
	}
	
	/**
	 * <p>get.</p>
	 *
	 * @return a {@link java.util.stream.Stream} object
	 */
	@SuppressWarnings("unchecked")
	public Stream<String> get() {
		return this.stream().map(s -> s.get());
	}
	
	/**
	 * <p>opt.</p>
	 *
	 * @return a {@link java.util.stream.Stream} object
	 */
	@SuppressWarnings("unchecked")
	public Stream<Optional<String>> opt() {
		return this.stream().map(s -> s.opt());
	}
	
	/** {@inheritDoc} */
	@SuppressWarnings("unchecked")
	@Override
	public RCharacterVector and(RCharacter... o) {
		this.addAll(Arrays.asList(o));
		return this;
	}
	
	/**
	 * <p>empty.</p>
	 *
	 * @return a {@link uk.co.terminological.rjava.types.RCharacterVector} object
	 */
	public static RCharacterVector empty() {
		return new RCharacterVector();
	}
	
	
	
	/** {@inheritDoc} */
	public void fillNA(int length) {this.fill(RCharacter.NA, length);}
	
}
