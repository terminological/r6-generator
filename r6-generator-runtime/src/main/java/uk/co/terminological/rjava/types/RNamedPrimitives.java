package uk.co.terminological.rjava.types;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;

public class RNamedPrimitives extends LinkedHashMap<String,RPrimitive> {

	public RNamedPrimitives(RNamedPrimitives tmp) {
		super(tmp);
	}
	
	public RNamedPrimitives() {
		super();
	}

	public int hashCode() {
		return super.hashCode();
	}
	
	public boolean equals(Object another) {
		return super.equals(another);
	}
	
	public RNamedPrimitives reverse() {
		List<String> reverseOrderedKeys = new ArrayList<String>(this.keySet());
		Collections.reverse(reverseOrderedKeys);
		RNamedPrimitives out = new RNamedPrimitives();;
		reverseOrderedKeys.forEach(key -> out.put(key,this.get(key)));
		return out;
	}
	
	public RDataframe toDataframe(int length) {
		RDataframe out = new RDataframe();
		this.forEach((k,v) -> {
			RVector<?> tmp = RVector.rep(v, length);
			out.addCol(k, tmp);
		});
		return out;
	}
	
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
