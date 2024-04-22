package uk.co.terminological.rjava.types;

import java.util.stream.Collectors;

import uk.co.terminological.rjava.utils.RObjectVisitor;

/**
 * <p>RDataframeRow class.</p>
 *
 * @author vp22681
 * @version $Id: $Id
 */
public class RDataframeRow extends RNamedPrimitives implements RObject {

	
	private int row;
	private RDataframe dataframe;
		
	/**
	 * <p>Constructor for RDataframeRow.</p>
	 *
	 * @param rDataframe a {@link uk.co.terminological.rjava.types.RDataframe} object
	 * @param i a int
	 */
	public RDataframeRow(RDataframe rDataframe, int i) {
		super();
		this.row = i;
		this.dataframe = rDataframe;
		rDataframe.keySet().forEach(k -> {
			this.put(k, rDataframe.get(k).get(i));
		});
	}
	/**
	 * <p>getRowNumber.</p>
	 *
	 * @return a int
	 */
	public int getRowNumber() {return row;}
	
	/**
	 * <p>lag.</p>
	 *
	 * @param before a int
	 * @return a {@link uk.co.terminological.rjava.types.RDataframeRow} object
	 */
	public RDataframeRow lag(int before) {return dataframe.getRow(row-before);}
	/**
	 * <p>lead.</p>
	 *
	 * @param after a int
	 * @return a {@link uk.co.terminological.rjava.types.RDataframeRow} object
	 */
	public RDataframeRow lead(int after) {return dataframe.getRow(row+after);}
	
	/**
	 * <p>lag.</p>
	 *
	 * @return a {@link uk.co.terminological.rjava.types.RDataframeRow} object
	 */
	public RDataframeRow lag() {return lag(1);}
	/**
	 * <p>lead.</p>
	 *
	 * @return a {@link uk.co.terminological.rjava.types.RDataframeRow} object
	 */
	public RDataframeRow lead() {return lead(1);}
	
	
	
	/** {@inheritDoc} */
	@Override
	public String rCode() {
		// TODO Auto-generated method stub
		return null;
	}
	/** {@inheritDoc} */
	@Override
	public <X> X accept(RObjectVisitor<X> visitor) {
		X out = visitor.visit(this);
		this.iterator().forEachRemaining(c -> RNamed.from(c).accept(visitor));
		return out;
	}
	
	
	/** {@inheritDoc} */
	@Override
	public int hashCode() {
		return super.hashCode();
	}
	
	/** {@inheritDoc} */
	@Override
	public boolean equals(Object obj) {
		return super.equals(obj);
	}
	/**
	 * <p>rowGroup.</p>
	 *
	 * @return a {@link uk.co.terminological.rjava.types.RNamedPrimitives} object
	 */
	public RNamedPrimitives rowGroup() {
		RNamedPrimitives out = new RNamedPrimitives();
		dataframe.groupSet().forEach(k -> out.put(k, this.get(k)));
		return out;
	}
	/**
	 * <p>asCsv.</p>
	 *
	 * @return a {@link java.lang.String} object
	 */
	public String asCsv() {
		return this.values().stream().map(v -> v.asCsv()).collect(Collectors.joining(","))+"\n";
	}
	
}
