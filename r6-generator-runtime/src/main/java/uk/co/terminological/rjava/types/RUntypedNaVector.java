package uk.co.terminological.rjava.types;

import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Stream;

import uk.co.terminological.rjava.IncompatibleTypeException;
import uk.co.terminological.rjava.RDataType;
import uk.co.terminological.rjava.RObjectVisitor;

@RDataType(
		JavaToR = { 
				"function(jObj) rep(NA, rJava::.jcall(jObj,returnSig='I',method='size'))",
		}, 
		RtoJava = { 
				"function(rObj) {", 
				"	if (is.null(rObj)) return(rJava::.new('~RUNTYPEDNAVECTOR~'))",
				"	return(rJava::.jnew('~RUNTYPEDNAVECTOR~',length(rObj)))", 
				"}"
		}
		//JNIType = "[I"
	)
public class RUntypedNaVector extends RVector<RUntypedNa> {

	@Override
	public <X> X accept(RObjectVisitor<X> visitor) {
		return visitor.visit(this);
	}

	public RUntypedNaVector() {super();}
	public RUntypedNaVector(int length) {
		super(length);
		this.fillNA(length);
	}
	
	@Override
	public void fillNA(int length) {
		this.fill(RUntypedNa.NA, length);
	}
	
	@SuppressWarnings("unchecked")
	protected <Y extends RPrimitive> RVector<Y> addAllUnsafe(RVector<? extends RPrimitive> rVector) {
		try {
			if (rVector instanceof RUntypedNaVector) {
				this.addAll((RUntypedNaVector) rVector);
				return (RVector<Y>) this;
			} else {
				RVector<Y> out = (RVector<Y>) RVector.ofNA(rVector.getType(),this.size());
				out.addAll((RVector<Y>) rVector);
				return(out);
			}
		} catch (ClassCastException e) {
			throw new IncompatibleTypeException("Tried to append a "+rVector.getClass().getSimpleName()+" to a "+this.getClass().getSimpleName());
		}
	}
	
	@SuppressWarnings("unchecked")
	protected <Y extends RPrimitive> RVector<Y> addUnsafe(RPrimitive v) {
		
		if (v instanceof RUntypedNa) {
			try {
				this.add((RUntypedNa) v);
				return (RVector<Y>) this;
			} catch (ClassCastException e) {
				throw new IncompatibleTypeException("Tried to append a "+v.getClass().getSimpleName()+" to a "+this.getClass().getSimpleName());
			}
		} else {
			try {
				RVector<Y> out = (RVector<Y>) RVector.ofNA(v.getClass(),this.size());
				out.add(v.get(out.getType()));
				return(out);
			} catch (ClassCastException e) {
				throw new IncompatibleTypeException("Tried to append a "+v.getClass().getSimpleName()+" to a "+this.getClass().getSimpleName());
			}
		}
		
	}

	@SuppressWarnings("unchecked")
	@Override
	public RUntypedNaVector and(RUntypedNa... o) {
		this.addAll(Arrays.asList(o));
		return this;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Stream<RUntypedNa> get() {
		return this.stream();
	}

	@SuppressWarnings("unchecked")
	@Override
	public Stream<Optional<RUntypedNa>> opt() {
		return this.stream().map(o -> Optional.of(o));
	}

	@Override
	public Class<RUntypedNa> getType() {
		return RUntypedNa.class;
	}

}
