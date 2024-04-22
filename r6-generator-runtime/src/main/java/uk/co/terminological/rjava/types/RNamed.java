package uk.co.terminological.rjava.types;

import java.util.Map;
import java.util.Map.Entry;

import uk.co.terminological.rjava.utils.RObjectVisitor;


/**
 * A named object. R seems to be able to attach names to most things.
 * This is somewhat impractical to implement in java, as it conflicts a bit
 * with the idea of map structures. This class is generally used
 * when converting vector based views of the data (R like) to row based views of the data
 * (java like). This is a work in progress.
 *
 * @author terminological
 * @param <X> an RObject
 * @version $Id: $Id
 */
public class RNamed<X extends RObject> implements RObject, Map.Entry<String,X> {

	private static final long serialVersionUID = RObject.datatypeVersion;
	
	private String name;
	private X object;

	/**
	 * <p>Constructor for RNamed.</p>
	 *
	 * @param name a {@link java.lang.String} object
	 * @param object a X object
	 */
	public RNamed(String name, X object) {
		this.name = name;
		this.object = object;
	}

	/** {@inheritDoc} */
	@Override
	public String getKey() {
		return name;
	}

	/** {@inheritDoc} */
	@Override
	public X getValue() {
		return object;
	}

	/** {@inheritDoc} */
	@Override
	public X setValue(X value) {
		return object = value;
	}

	/** {@inheritDoc} */
	@Override
	public String rCode() {
		return name+"="+object.rCode();
	}
	
	/** {@inheritDoc} */
	@Override
	public <Y> Y accept(RObjectVisitor<Y> visitor) {return visitor.visit(this);}

	/**
	 * <p>from.</p>
	 *
	 * @param entry a {@link java.util.Map.Entry} object
	 * @param <Y> a Y class
	 * @return a {@link uk.co.terminological.rjava.types.RNamed} object
	 */
	public static <Y extends RObject> RNamed<Y> from(Entry<String, Y> entry) {
		return new RNamed<Y>(entry.getKey(), entry.getValue());
	}
	
}
