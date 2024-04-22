package uk.co.terminological.rjava.types;

import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import uk.co.terminological.rjava.RDataType;
import uk.co.terminological.rjava.ZeroDimensionalArrayException;

/**
 * The vector of numerics is needed to ensure that NA values are correctly handled and
 * allow flexibility of a java List structure for easy manipulation. Factory methods are in {@link uk.co.terminological.rjava.types.RVector}.
 *
 * @author terminological
 * @version $Id: $Id
 */
@RDataType(
		JavaToR = {
				"function(jObj) {",
				"	tmpVec = as.numeric(rJava::.jcall(jObj,returnSig='[D',method='rPrimitive'))",
				"	tmpDim = as.integer(rJava::.jcall(jObj,returnSig='[I',method='rDim'))",
				"   if (length(tmpDim)==2) return(matrix(tmpVec,tmpDim))",
				"	return(array(tmpVec,tmpDim))",
				"}"
		},
		RtoJava = {
				"function(rObj) {",
				"	if (is.null(rObj)) return(rJava::.jnew('~RNUMERICARRAY~'))",
				"	if (!is.numeric(rObj)) stop('expected a numeric')",
				"	if (!is.array(rObj)) stop('expected an array')",
				"	tmpVec = as.vector(as.numeric(rObj))",
				"	tmpDim = dim(rObj)",
				"	return(rJava::.jnew('~RNUMERICARRAY~',rJava::.jarray(tmpVec),rJava::.jarray(tmpDim)))",
				"}"
		}//,
		//JNIType = "[D"
	)
public class RNumericArray extends RArray<RNumeric> implements JNIPrimitive {
	
	private static final long serialVersionUID = RObject.datatypeVersion;
	private RNumericVector vector = new RNumericVector();
	
	/**
	 * <p>Constructor for RNumericArray.</p>
	 *
	 * @param primitives an array of {@link double} objects
	 * @param dimensions an array of {@link int} objects
	 */
	public RNumericArray(double[] primitives, int[] dimensions) {
		int elements = 1;
		for (int dim: dimensions) elements *= dim;
		if (primitives.length != elements) throw new IndexOutOfBoundsException("expected "+elements+" elements but found "+primitives.length);
		for (int i=0; i<primitives.length; i++) this.vector.add(new RNumeric(primitives[i]));
		this.dimensions = dimensions;
	}
	
	/**
	 * <p>Getter for the field <code>vector</code>.</p>
	 *
	 * @return a {@link uk.co.terminological.rjava.types.RNumericVector} object
	 */
	public RNumericVector getVector() {return vector;}
	
	/**
	 * <p>Constructor for RNumericArray.</p>
	 */
	public RNumericArray() {super();}
	/**
	 * <p>Constructor for RNumericArray.</p>
	 *
	 * @param length a int
	 */
	public RNumericArray(int length) {
		vector = new RNumericVector(length);}
	
	/**
	 * <p>Constructor for RNumericArray.</p>
	 *
	 * @param sublist a {@link uk.co.terminological.rjava.types.RNumericVector} object
	 * @param newDims an array of {@link int} objects
	 */
	public RNumericArray(RNumericVector sublist, int[] newDims) {
		this.vector = sublist;
		this.dimensions = newDims;
	}

	/**
	 * <p>rPrimitive.</p>
	 *
	 * @return an array of {@link double} objects
	 */
	public double[] rPrimitive() {
		return this.vector.stream().mapToDouble(ri -> ri.rPrimitive()).toArray();
	}
	
	/**
	 * <p>rDim.</p>
	 *
	 * @return an array of {@link int} objects
	 */
	public int[] rDim() {
		return this.dimensions;
	}
	
	/** {@inheritDoc} */
	@Override
	public Class<RNumeric> getType() {
		return RNumeric.class;
	}
	
//	@SuppressWarnings("unchecked")
//	public Stream<Double> get() {
//		return this.stream().map(r -> r.get());
//	}
//	
//	@SuppressWarnings("unchecked")
//	public Stream<Optional<Double>> opt() {
//		return this.stream().map(s -> s.opt());
//	}
	
	/**
	 * <p>get.</p>
	 *
	 * @param zeroBasedIndices a int
	 * @return a {@link java.lang.Double} object
	 */
	public Double get(int... zeroBasedIndices) {
		if (zeroBasedIndices.length != this.dimensions.length) throw new IndexOutOfBoundsException("Dimensionality mismatch: given "+zeroBasedIndices.length+" dimensions expected "+this.dimensions.length);
		int multiplier = 1;
		int index=0;
		for (int dimension=0; dimension<this.dimensions.length; dimension+=1) {
			if (zeroBasedIndices[dimension] <0 || zeroBasedIndices[dimension] >= this.dimensions[dimension]) throw new IndexOutOfBoundsException("Index out of bounds: "+zeroBasedIndices[dimension]+" should be >=0 and <"+this.dimensions.length);
			index += zeroBasedIndices[dimension]*multiplier;
			multiplier *= this.dimensions[dimension];
		}
		return vector.get(index).get();
	}
	
	/**
	 * <p>getR.</p>
	 *
	 * @param oneBasedIndices a int
	 * @return a {@link uk.co.terminological.rjava.types.RNumeric} object
	 */
	public RNumeric getR(int... oneBasedIndices) {
		if (oneBasedIndices.length != this.dimensions.length) throw new IndexOutOfBoundsException("Dimensionality mismatch: given "+oneBasedIndices.length+" dimensions expected "+this.dimensions.length);
		int multiplier = 1;
		int index=0;
		for (int dimension=0; dimension<this.dimensions.length; dimension+=1) {
			if (oneBasedIndices[dimension] < 1 || oneBasedIndices[dimension] > this.dimensions[dimension]) throw new IndexOutOfBoundsException("Index out of bounds: "+oneBasedIndices[dimension]+" should be >0 and <="+this.dimensions.length);
			index += (oneBasedIndices[dimension]-1)*multiplier;
			multiplier *= this.dimensions[dimension];
		}
		return vector.get(index);
	}
	
	/**
	 * <p>toString.</p>
	 *
	 * @return a {@link java.lang.String} object
	 */
	public String toString() {
		if (this.dimensions.length < 2) {
			return this.getVector().toString();
		} else {
			try {
				return "<rnumeric["+majorDimension()+"]>{\n"+this.get().limit(10).map(arr -> arr.toString()).collect(Collectors.joining(",\n"))+",\n...}";
			} catch (ZeroDimensionalArrayException e) {
				throw new RuntimeException(e);
			}
		}
	}
	
	/**
	 * <p>majorDimension.</p>
	 *
	 * @return a int
	 * @throws uk.co.terminological.rjava.ZeroDimensionalArrayException if any.
	 */
	public int majorDimension() throws ZeroDimensionalArrayException {
		if (this.dimensions.length == 0) throw new ZeroDimensionalArrayException("Zero dimensional array");
		return this.dimensions[this.dimensions.length-1];
	}
	
	/**
	 * <p>get.</p>
	 *
	 * @param majorZeroBasedIndex a int
	 * @return a {@link uk.co.terminological.rjava.types.RNumericArray} object
	 * @throws uk.co.terminological.rjava.ZeroDimensionalArrayException if any.
	 */
	public RNumericArray get(int majorZeroBasedIndex) throws ZeroDimensionalArrayException {
		if (majorZeroBasedIndex < 0 || majorZeroBasedIndex >= majorDimension()) throw new IndexOutOfBoundsException("index was "+majorZeroBasedIndex+"; expected >=0 and <"+majorDimension());
		if (this.dimensions.length==0) throw new ZeroDimensionalArrayException("Zero dimensional array cannot be subset");
		int newDimLength = this.dimensions.length-1;
		int[] newDims = new int[newDimLength];
		int multiplier = 1;
		//int[] newArray = Arrays.copyOfRange(oldArray, startIndex, endIndex);
		for (int i=newDimLength-1; i>=0; i--) {
			newDims[i] = this.dimensions[i];
			multiplier *= this.dimensions[i];
		}
		RNumericVector sublist = new RNumericVector(this.vector.subList(majorZeroBasedIndex*multiplier, (majorZeroBasedIndex+1)*multiplier));
		return new RNumericArray(sublist, newDims);
	}
	
	/**
	 * <p>empty.</p>
	 *
	 * @return a {@link uk.co.terminological.rjava.types.RNumericArray} object
	 */
	public static RNumericArray empty() {
		return new RNumericArray();
	}
	
	private RNumericArray getUnsafe(int majorZeroBasedIndex) {
		try {
			return get(majorZeroBasedIndex);
		} catch (ZeroDimensionalArrayException e) {
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * <p>get.</p>
	 *
	 * @return a {@link java.util.stream.Stream} object
	 * @throws uk.co.terminological.rjava.ZeroDimensionalArrayException if any.
	 */
	public Stream<RNumericArray> get() throws ZeroDimensionalArrayException  {
		return IntStream.range(0, majorDimension()).boxed().map(i -> this.getUnsafe(i));
	}
	
	/**
	 * <p>getUnsafe.</p>
	 *
	 * @return a {@link java.util.stream.Stream} object
	 */
	public Stream<RNumericArray> getUnsafe() {
		try {
			return get();
		} catch (ZeroDimensionalArrayException e) {
			throw new RuntimeException(e);
		}
	}

	

	
}
