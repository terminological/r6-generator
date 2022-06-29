package uk.co.terminological.rjava.types;

import java.util.stream.Collectors;

import uk.co.terminological.rjava.utils.RObjectVisitor;

public class RDataframeRow extends RNamedPrimitives implements RObject {

	
	private int row;
	private RDataframe dataframe;
		
	public RDataframeRow(RDataframe rDataframe, int i) {
		super();
		this.row = i;
		this.dataframe = rDataframe;
		rDataframe.keySet().forEach(k -> {
			this.put(k, rDataframe.get(k).get(i));
		});
	}
	public int getRowNumber() {return row;}
	
	public RDataframeRow lag(int before) {return dataframe.getRow(row-before);}
	public RDataframeRow lead(int after) {return dataframe.getRow(row+after);}
	
	public RDataframeRow lag() {return lag(1);}
	public RDataframeRow lead() {return lead(1);}
	
	
	
	@Override
	public String rCode() {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public <X> X accept(RObjectVisitor<X> visitor) {
		X out = visitor.visit(this);
		this.iterator().forEachRemaining(c -> RNamed.from(c).accept(visitor));
		return out;
	}
	
	
	@Override
	public int hashCode() {
		return super.hashCode();
	}
	
	@Override
	public boolean equals(Object obj) {
		return super.equals(obj);
	}
	public RNamedPrimitives rowGroup() {
		RNamedPrimitives out = new RNamedPrimitives();
		dataframe.groupSet().forEach(k -> out.put(k, this.get(k)));
		return out;
	}
	public String asCsv() {
		return this.values().stream().map(v -> v.asCsv()).collect(Collectors.joining(","))+"\n";
	}
	
}
