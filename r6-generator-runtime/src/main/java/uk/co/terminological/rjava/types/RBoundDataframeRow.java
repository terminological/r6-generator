package uk.co.terminological.rjava.types;

/**
 * <p>RBoundDataframeRow class.</p>
 *
 * @author vp22681
 * @version $Id: $Id
 */
public class RBoundDataframeRow<X> extends RDataframeRow {

	RBoundDataframe<X> boundDataframe;
	
	/**
	 * <p>Constructor for RBoundDataframeRow.</p>
	 *
	 * @param rDataframe a {@link uk.co.terminological.rjava.types.RBoundDataframe} object
	 * @param i a int
	 */
	public RBoundDataframeRow(RBoundDataframe<X> rDataframe, int i) {
		super(rDataframe, i);
		this.boundDataframe = rDataframe;
	}

	/**
	 * <p>coerce.</p>
	 *
	 * @return a X object
	 */
	public X coerce() {
		return this.boundDataframe.proxyFrom(this);
	}
	
	/**
	 * <p>lagCoerce.</p>
	 *
	 * @return a X object
	 */
	public X lagCoerce() {
		return this.boundDataframe.proxyFrom(this.lag());
	}
	
	/**
	 * <p>leadCoerce.</p>
	 *
	 * @return a X object
	 */
	public X leadCoerce() {
		return this.boundDataframe.proxyFrom(this.lead());
	}
	
	/**
	 * <p>lagCoerce.</p>
	 *
	 * @param i a int
	 * @return a X object
	 */
	public X lagCoerce(int i) {
		return this.boundDataframe.proxyFrom(this.lag(i));
	}
	
	/**
	 * <p>leadCoerce.</p>
	 *
	 * @param i a int
	 * @return a X object
	 */
	public X leadCoerce(int i) {
		return this.boundDataframe.proxyFrom(this.lead(i));
	}
	
	/** {@inheritDoc} */
	public RBoundDataframeRow<X> lag(int before) {
		return this.boundDataframe.getRow(this.getRowNumber()-before);
	}
	
	/** {@inheritDoc} */
	public RBoundDataframeRow<X> lead(int after) {
		return this.boundDataframe.getRow(this.getRowNumber()+after);
	}
	
	/**
	 * <p>lag.</p>
	 *
	 * @return a {@link uk.co.terminological.rjava.types.RBoundDataframeRow} object
	 */
	public RBoundDataframeRow<X> lag() {
		return this.lag(1);
	}
	
	/**
	 * <p>lead.</p>
	 *
	 * @return a {@link uk.co.terminological.rjava.types.RBoundDataframeRow} object
	 */
	public RBoundDataframeRow<X> lead() {
		return this.lead(1);
	}
}






















