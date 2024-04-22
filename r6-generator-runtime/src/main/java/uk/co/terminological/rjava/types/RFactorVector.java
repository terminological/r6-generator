package uk.co.terminological.rjava.types;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import uk.co.terminological.rjava.RConverter;
import uk.co.terminological.rjava.RDataType;
import uk.co.terminological.rjava.utils.RObjectVisitor;

/**
 * A Java wrapper for factors. R Factors can be mapped to java enumeration by the {@link uk.co.terminological.rjava.RConverter} class.
 * Factory methods are in {@link uk.co.terminological.rjava.types.RVector}.
 *
 * @author terminological
 * @version $Id: $Id
 */
@RDataType(
		JavaToR = {
				"function(jObj) ordered(",
				"	x = rJava::.jcall(jObj,returnSig='[I',method='rValues'),",
				"	labels = rJava::.jcall(jObj,returnSig='[Ljava/lang/String;',method='rLevels')",
				")"
		},
		RtoJava = {
				"function(rObj) {",
				"	if (is.null(rObj)) return(rJava::.jnew('~RFACTORVECTOR~'))",
				"	if (!is.factor(rObj)) stop('expected a vector of factors')",
				"	tmp = as.integer(rObj)",
				"	return(rJava::.jnew('~RFACTORVECTOR~', rJava::.jarray(tmp), rJava::.jarray(levels(rObj))))",
				"}"
		}
		//JNIType = "[I"
	)
public class RFactorVector extends RVector<RFactor> {
	
	private static final long serialVersionUID = RObject.datatypeVersion;
	// transient HashMap<Integer,List<Integer>> index = new HashMap<>();
	
	private String[] levels;
	/**
	 * <p>Constructor for RFactorVector.</p>
	 *
	 * @param values an array of {@link int} objects
	 * @param levels an array of {@link java.lang.String} objects
	 */
	public RFactorVector(int[] values, String[] levels) {
		super(values.length);
		this.levels = levels;
		for (int i=0; i<values.length; i++) {
			this.add(new RFactor(values[i], levels[values[i]-1]));
//			if(!index.containsKey(values[i])) index.put(values[i], new ArrayList<>());
//			index.get(values[i]).add(i);
		}
		
		//factors are 1 indexed - java arrays zero indexed
	}
	/**
	 * <p>Constructor for RFactorVector.</p>
	 */
	public RFactorVector() {super();}
	/**
	 * <p>Constructor for RFactorVector.</p>
	 *
	 * @param length a int
	 */
	public RFactorVector(int length) {super(length);}
	/**
	 * <p>Constructor for RFactorVector.</p>
	 *
	 * @param levels an array of {@link java.lang.String} objects
	 */
	public RFactorVector(String[] levels) {
		super();
		this.levels = levels;
	}
	/**
	 * <p>rValues.</p>
	 *
	 * @return an array of {@link int} objects
	 */
	public int[] rValues() {
		return this.stream().mapToInt(ri -> ri.rValue()).toArray();
	}
	/**
	 * <p>rLevels.</p>
	 *
	 * @return an array of {@link java.lang.String} objects
	 */
	public String[] rLevels() {
		if (levels != null) return levels;
		LinkedHashMap<Integer,String> tmp = new LinkedHashMap<>();
		this.stream().forEach(f -> tmp.put(f.rValue(), f.rLabel()));
		String[] obs = IntStream.range(0, tmp.size()).mapToObj(i -> tmp.getOrDefault(i+1, "unknown_"+i)).collect(Collectors.toList()).toArray(new String[] {});
		return obs;
	}
	
	/** {@inheritDoc} */
	@Override
	public Class<RFactor> getType() {
		return RFactor.class;
	}

	
	/**
	 * <p>rCode.</p>
	 *
	 * @return a {@link java.lang.String} object
	 */
	public String rCode() {
		return "ordered(x=c("+
				this.stream().map(s -> s==null?"NA":RConverter.rQuote(s.rLabel(), "'")).collect(Collectors.joining(", "))+
				"),labels=c("+
				Stream.of(this.rLevels()).map(s -> RConverter.rQuote(s, "'")).collect(Collectors.joining(", "))
				+"))";
	}
	
	/** {@inheritDoc} */
	@Override
	public <X> X accept(RObjectVisitor<X> visitor) {
		X out = visitor.visit(this);
		this.forEach(c -> c.accept(visitor));
		return out;
	}
	
	/** {@inheritDoc} */
	@SuppressWarnings("unchecked")
	@Override
	public Stream<String> get() {
		return this.stream().map(ri -> ri.get());
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
	public RFactorVector and(RFactor... o) {
		this.addAll(Arrays.asList(o));
		return this;
	}
	/**
	 * <p>empty.</p>
	 *
	 * @return a {@link uk.co.terminological.rjava.types.RFactorVector} object
	 */
	public static RFactorVector empty() {
		return new RFactorVector();
	}
	
	/** {@inheritDoc} */
	public void fillNA(int length) {this.fill(RFactor.NA, length);}
}
