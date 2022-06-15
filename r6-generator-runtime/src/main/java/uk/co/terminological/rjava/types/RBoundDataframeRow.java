package uk.co.terminological.rjava.types;

public class RBoundDataframeRow<X> extends RDataframeRow {

	RBoundDataframe<X> boundDataframe;
	
	public RBoundDataframeRow(RBoundDataframe<X> rDataframe, int i) {
		super(rDataframe, i);
		this.boundDataframe = rDataframe;
	}

	public X coerce() {
		return this.boundDataframe.proxyFrom(this);
	}
	
	public X lagCoerce() {
		return this.boundDataframe.proxyFrom(this.lag());
	}
	
	public X leadCoerce() {
		return this.boundDataframe.proxyFrom(this.lead());
	}
	
	public X lagCoerce(int i) {
		return this.boundDataframe.proxyFrom(this.lag(i));
	}
	
	public X leadCoerce(int i) {
		return this.boundDataframe.proxyFrom(this.lead(i));
	}
	
	public RBoundDataframeRow<X> lag(int before) {
		return this.boundDataframe.getRow(this.getRowNumber()-before);
	}
	
	public RBoundDataframeRow<X> lead(int after) {
		return this.boundDataframe.getRow(this.getRowNumber()+after);
	}
	
	public RBoundDataframeRow<X> lag() {
		return this.lag(1);
	}
	
	public RBoundDataframeRow<X> lead() {
		return this.lead(1);
	}
}






















