package uk.co.terminological.rjava.types;

import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import uk.co.terminological.rjava.RObjectVisitor;
import uk.co.terminological.rjava.ZeroDimensionalArrayException;

public abstract class RArray<X extends RPrimitive> implements RObject {

	private static final long serialVersionUID = RObject.datatypeVersion;
	
	public abstract RVector<X> getVector();
	int[] dimensions;
	
	public int getDimensionality() {
		return dimensions.length;
	}
		
	public abstract Stream<? extends RArray<X>> get() throws ZeroDimensionalArrayException;
	
	@Override
	public String rCode() {
		String dims = Arrays.stream(dimensions).boxed().map(n -> n.toString()).collect(Collectors.joining(", "));
		return "array("+getVector().rCode()+", c("+dims+"))";
	}
	
	@Override
	public <Y> Y accept(RObjectVisitor<Y> visitor) {
		Y out = visitor.visit(this);
		if (getDimensionality() == 0) this.getVector().get(0).accept(visitor);
		if (getDimensionality() == 1) this.getVector().accept(visitor);
		try {
			this.get().forEach(visitor::visit);
		} catch (ZeroDimensionalArrayException e) {
			throw new RuntimeException(e);
		}
		return out;
	}

	public abstract Class<RNumeric> getType();
	
	public Stream<RObject> stream() {
		if (getDimensionality() == 0) return Stream.of(this.getVector().get(0));
		if (getDimensionality() == 1) return this.getVector().stream().map(x -> (RPrimitive) x);
		try {
			return this.get().map(x->(RObject) x);
		} catch (ZeroDimensionalArrayException e) {
			throw new RuntimeException(e);
		}
	}
	
	//TODO: integerate this.
}
