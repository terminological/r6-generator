package uk.co.terminological.rjava.types;

import uk.co.terminological.rjava.RDataType;
import uk.co.terminological.rjava.utils.RObjectVisitor;

/**
 * In general you won;t see this class. Its possible to do things in R like x=list(a=NULL) in which case
 * the null value needs a placeholder in java. In general it is not like a java null which is used more like
 * an R NA value. For methods that return nothing use java void and not this class.
 *
 * @author terminological
 * @version $Id: $Id
 */
@RDataType(
		JavaToR = {
				"function(jObj) return(NULL)",
		},
		RtoJava = {
				"function(rObj) {",
				"	if (!is.null(rObj)) stop('input expected to be NULL')",
				"	return(rJava::.jnew('~RNULL~'))",
				"}"
		}//,
		//JNIType = "Luk/co/terminological/rjava/types/RNull;"
	)
public class RNull implements RObject {

	private static final long serialVersionUID = RObject.datatypeVersion;
	
	/**
	 * <p>Constructor for RNull.</p>
	 */
	public RNull() {}
	
	/**
	 * <p>toString.</p>
	 *
	 * @return a {@link java.lang.String} object
	 */
	public String toString() {return "NULL";}

	/** {@inheritDoc} */
	@Override
	public String rCode() {
		return "NULL";
	}
	
	/** {@inheritDoc} */
	@Override
	public <X> X accept(RObjectVisitor<X> visitor) {return visitor.visit(this);}
	
	/** {@inheritDoc} */
	public boolean equals(Object other) {
		return (other instanceof RNull);
	}
}
