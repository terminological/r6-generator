package uk.co.terminological.rjava.types;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;

/**
 * <p>RNamedPrimitives class.</p>
 *
 * @author vp22681
 * @version $Id: $Id
 */
public class RNamedPrimitives extends LinkedHashMap<String,RPrimitive> {

	/**
	 * <p>Constructor for RNamedPrimitives.</p>
	 *
	 * @param tmp a {@link uk.co.terminological.rjava.types.RNamedPrimitives} object
	 */
	public RNamedPrimitives(RNamedPrimitives tmp) {
		super(tmp);
	}
	
	/**
	 * <p>Constructor for RNamedPrimitives.</p>
	 */
	public RNamedPrimitives() {
		super();
	}

	/**
	 * <p>hashCode.</p>
	 *
	 * @return a int
	 */
	public int hashCode() {
		return super.hashCode();
	}
	
	/** {@inheritDoc} */
	public boolean equals(Object another) {
		return super.equals(another);
	}
	
	/**
	 * <p>reverse.</p>
	 *
	 * @return a {@link uk.co.terminological.rjava.types.RNamedPrimitives} object
	 */
	public RNamedPrimitives reverse() {
		List<String> reverseOrderedKeys = new ArrayList<String>(this.keySet());
		Collections.reverse(reverseOrderedKeys);
		RNamedPrimitives out = new RNamedPrimitives();;
		reverseOrderedKeys.forEach(key -> out.put(key,this.get(key)));
		return out;
	}
	
	/**
	 * <p>toDataframe.</p>
	 *
	 * @param length a int
	 * @return a {@link uk.co.terminological.rjava.types.RDataframe} object
	 */
	public RDataframe toDataframe(int length) {
		RDataframe out = new RDataframe();
		this.forEach((k,v) -> {
			RVector<?> tmp = RVector.rep(v, length);
			out.addCol(k, tmp);
		});
		return out;
	}
	
	/**
	 * <p>iterator.</p>
	 *
	 * @return a {@link java.util.Iterator} object
	 */
	public Iterator<Entry<String,RPrimitive>> iterator() {
		return this.entrySet().iterator();
//		return new Iterator<RNamed<RPrimitive>>() {
//			Iterator<Entry<String, RPrimitive>> it = RNamedPrimitives.this.entrySet().iterator();
//			
//			@Override
//			public boolean hasNext() {
//				return it.hasNext();
//			}
//
//			@Override
//			public RNamed<RPrimitive> next() {
//				return RNamed.from(it.next());
//			}
//		
//		};
	}
}
