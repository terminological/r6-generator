package uk.co.terminological.rjava.utils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import uk.co.terminological.rjava.types.*;

/**
 * Visitor patterns for R object tree.
 * The visitor will perform a depth first tree traversal. By default this will not detect
 * cycles in the object graph as these are quite hard to create in R, and only really an
 * issue for things created in java. If this is needed at {@link OnceOnly} visitor is also 
 * defined that will only execute for the first instance of an object in the graph.
 * 
 * @see OnceOnly
 * @see DefaultOptional
 * @see Default
 * 
 * @author terminological
 *
 * @param <X> the output of the visitor
 */
public interface RObjectVisitor<X> {

	public X visit(RCharacter c);
	public X visit(RCharacterVector c);
	public X visit(RDataframe c);
	public X visit(RDataframeRow c);
	public X visit(RDate c);
	public X visit(RDateVector c);
	public X visit(RFactor c);
	public X visit(RFactorVector c);
	public X visit(RInteger c);
	public X visit(RIntegerVector c);
	public X visit(RList c);
	public X visit(RLogical c);
	public X visit(RLogicalVector c);
	//public X visit(RMatrix<?> c);
	public X visit(RNamed<?> c);
	public X visit(RNamedList c);
	public X visit(RNull c);
	public X visit(RNumeric c);
	public X visit(RNumericVector c);
	public X visit(RArray<?> rArray);
	public X visit(RUntypedNa rna);
	public X visit(RUntypedNaVector rUntypedNaVector);
	
	
	/** Default visitor implemementation that returns an optional empty value for every visit.
	 * Override this to get a specific value.
	 * 
	 * @see DefaultOnceOnly
	 * 
	 * @author terminological
	 *
	 * @param <Y> the output of the visitor
	 */
	public static class DefaultOptional<Y> implements RObjectVisitor<Optional<Y>> {
		public Optional<Y> visit(RCharacter c) {return Optional.empty();}
		public Optional<Y> visit(RCharacterVector c) {return Optional.empty();}
		public Optional<Y> visit(RDataframe c) {return Optional.empty();}
		public Optional<Y> visit(RDataframeRow c) {return Optional.empty();}
		public Optional<Y> visit(RDate c) {return Optional.empty();}
		public Optional<Y> visit(RDateVector c) {return Optional.empty();}
		public Optional<Y> visit(RFactor c) {return Optional.empty();}
		public Optional<Y> visit(RFactorVector c) {return Optional.empty();}
		public Optional<Y> visit(RInteger c) {return Optional.empty();}
		public Optional<Y> visit(RIntegerVector c) {return Optional.empty();}
		public Optional<Y> visit(RList c) {return Optional.empty();}
		public Optional<Y> visit(RLogical c) {return Optional.empty();}
		public Optional<Y> visit(RLogicalVector c) {return Optional.empty();}
		//public Optional<Y> visit(RMatrix<?> c) {return Optional.empty();}
		public Optional<Y> visit(RNamed<?> c) {return Optional.empty();}
		public Optional<Y> visit(RNamedList c) {return Optional.empty();}
		public Optional<Y> visit(RNull c) {return Optional.empty();}
		public Optional<Y> visit(RNumeric c) {return Optional.empty();}
		public Optional<Y> visit(RNumericVector c) {return Optional.empty();}
		public Optional<Y> visit(RArray<?> c) {return Optional.empty();}
		public Optional<Y> visit(RUntypedNa c) {return Optional.empty();}
		public Optional<Y> visit(RUntypedNaVector c) {return Optional.empty();}
	}
	
	public static class Default implements RObjectVisitor<Void> {
		public Void visit(RCharacter c) {return null;}
		public Void visit(RCharacterVector c) {return null;}
		public Void visit(RDataframe c) {return null;}
		public Void visit(RDataframeRow c) {return null;}
		public Void visit(RDate c) {return null;}
		public Void visit(RDateVector c) {return null;}
		public Void visit(RFactor c) {return null;}
		public Void visit(RFactorVector c) {return null;}
		public Void visit(RInteger c) {return null;}
		public Void visit(RIntegerVector c) {return null;}
		public Void visit(RList c) {return null;}
		public Void visit(RLogical c) {return null;}
		public Void visit(RLogicalVector c) {return null;}
		//public Void visit(RMatrix<?> c) {return null;}
		public Void visit(RNamed<?> c) {return null;}
		public Void visit(RNamedList c) {return null;}
		public Void visit(RNull c) {return null;}
		public Void visit(RNumeric c) {return null;}
		public Void visit(RNumericVector c) {return null;}
		public Void visit(RArray<?> c) {return null;}
		public Void visit(RUntypedNa c) {return null;}
		public Void visit(RUntypedNaVector c) {return null;}
	}
	
	/** This abstract visitor will visit each node once and collect the result into a 
	 * traversal order list. This can be used to find all the nodes that match a particular
	 * criteria for example.
	 * 
	 * @see DefaultOnceOnly
	 * 
	 * @author terminological
	 *
	 * @param <Y> the output of the visitor
	 */
	public static abstract class OnceOnly<Y> implements RObjectVisitor<Optional<Y>> {
		
		Set<RObject> visited = new HashSet<>();
		List<Y> collection = new ArrayList<>();
		
		public List<Y> getResult() {
			return collection;
		}
		
		public Optional<Y> visit(RCharacter c) {
			if (visited.contains(c)) return Optional.empty();
			else {
				visited.add(c);
				Optional<Y> tmp = visitOnce(c);
				tmp.ifPresent(collection::add);
				return tmp;
			}
		}
		
		public Optional<Y> visit(RCharacterVector c) {
			if (visited.contains(c)) return Optional.empty();
			else {
				visited.add(c);
				Optional<Y> tmp = visitOnce(c);
				tmp.ifPresent(collection::add);
				return tmp;
			}
		}
		public Optional<Y> visit(RDataframe c) {
			if (visited.contains(c)) return Optional.empty();
			else {
				visited.add(c);
				Optional<Y> tmp = visitOnce(c);
				tmp.ifPresent(collection::add);
				return tmp;
			}
		}
		public Optional<Y> visit(RDataframeRow c) {
			if (visited.contains(c)) return Optional.empty();
			else {
				visited.add(c);
				Optional<Y> tmp = visitOnce(c);
				tmp.ifPresent(collection::add);
				return tmp;
			}
		}
		public Optional<Y> visit(RDate c) {
			if (visited.contains(c)) return Optional.empty();
			else {
				visited.add(c);
				Optional<Y> tmp = visitOnce(c);
				tmp.ifPresent(collection::add);
				return tmp;
			}
		}
		public Optional<Y> visit(RDateVector c) {
			if (visited.contains(c)) return Optional.empty();
			else {
				visited.add(c);
				Optional<Y> tmp = visitOnce(c);
				tmp.ifPresent(collection::add);
				return tmp;
			}
		}
		public Optional<Y> visit(RFactor c) {
			if (visited.contains(c)) return Optional.empty();
			else {
				visited.add(c);
				Optional<Y> tmp = visitOnce(c);
				tmp.ifPresent(collection::add);
				return tmp;
			}}
		public Optional<Y> visit(RFactorVector c) {if (visited.contains(c)) return Optional.empty();
			else {
				visited.add(c);
				Optional<Y> tmp = visitOnce(c);
				tmp.ifPresent(collection::add);
				return tmp;
			}}
		public Optional<Y> visit(RInteger c) {if (visited.contains(c)) return Optional.empty();
			else {
				visited.add(c);
				Optional<Y> tmp = visitOnce(c);
				tmp.ifPresent(collection::add);
				return tmp;
			}}
		public Optional<Y> visit(RIntegerVector c) {if (visited.contains(c)) return Optional.empty();
			else {
				visited.add(c);
				Optional<Y> tmp = visitOnce(c);
				tmp.ifPresent(collection::add);
				return tmp;
			}}
		public Optional<Y> visit(RList c) {if (visited.contains(c)) return Optional.empty();
			else {
				visited.add(c);
				Optional<Y> tmp = visitOnce(c);
				tmp.ifPresent(collection::add);
				return tmp;
			}}
		public Optional<Y> visit(RLogical c) {if (visited.contains(c)) return Optional.empty();
			else {
				visited.add(c);
				Optional<Y> tmp = visitOnce(c);
				tmp.ifPresent(collection::add);
				return tmp;
			}}
		public Optional<Y> visit(RLogicalVector c) {if (visited.contains(c)) return Optional.empty();
			else {
				visited.add(c);
				Optional<Y> tmp = visitOnce(c);
				tmp.ifPresent(collection::add);
				return tmp;
			}}
		//public Optional<Y> visit(RMatrix<?> c) {if (visited.contains(c)) return Optional.empty();
//			else {
//				visited.add(c);
//				Optional<Y> tmp = visitOnce(c);
//				tmp.ifPresent(collection::add);
//				return tmp;
//			}}
		public Optional<Y> visit(RNamed<?> c) {if (visited.contains(c)) return Optional.empty();
			else {
				visited.add(c);
				Optional<Y> tmp = visitOnce(c);
				tmp.ifPresent(collection::add);
				return tmp;
			}}
		public Optional<Y> visit(RNamedList c) {if (visited.contains(c)) return Optional.empty();
			else {
				visited.add(c);
				Optional<Y> tmp = visitOnce(c);
				tmp.ifPresent(collection::add);
				return tmp;
			}}
		public Optional<Y> visit(RNull c) {if (visited.contains(c)) return Optional.empty();
			else {
				visited.add(c);
				Optional<Y> tmp = visitOnce(c);
				tmp.ifPresent(collection::add);
				return tmp;
			}}
		public Optional<Y> visit(RUntypedNa c) {if (visited.contains(c)) return Optional.empty();
		else {
			visited.add(c);
			Optional<Y> tmp = visitOnce(c);
			tmp.ifPresent(collection::add);
			return tmp;
		}}
		public Optional<Y> visit(RUntypedNaVector c) {if (visited.contains(c)) return Optional.empty();
		else {
			visited.add(c);
			Optional<Y> tmp = visitOnce(c);
			tmp.ifPresent(collection::add);
			return tmp;
		}}
		public Optional<Y> visit(RNumeric c) {if (visited.contains(c)) return Optional.empty();
			else {
				visited.add(c);
				Optional<Y> tmp = visitOnce(c);
				tmp.ifPresent(collection::add);
				return tmp;
			}}
		public Optional<Y> visit(RNumericVector c) {if (visited.contains(c)) return Optional.empty();
			else {
				visited.add(c);
				Optional<Y> tmp = visitOnce(c);
				tmp.ifPresent(collection::add);
				return tmp;
			}}
		public Optional<Y> visit(RArray<?> c) {if (visited.contains(c)) return Optional.empty();
		else {
			visited.add(c);
			Optional<Y> tmp = visitOnce(c);
			tmp.ifPresent(collection::add);
			return tmp;
		}}
		
		public abstract Optional<Y> visitOnce(RCharacter c);
		public abstract Optional<Y> visitOnce(RCharacterVector c);
		public abstract Optional<Y> visitOnce(RDataframe c);
		public abstract Optional<Y> visitOnce(RDataframeRow c);
		public abstract Optional<Y> visitOnce(RDate c);
		public abstract Optional<Y> visitOnce(RDateVector c);
		public abstract Optional<Y> visitOnce(RFactor c);
		public abstract Optional<Y> visitOnce(RFactorVector c);
		public abstract Optional<Y> visitOnce(RInteger c);
		public abstract Optional<Y> visitOnce(RIntegerVector c);
		public abstract Optional<Y> visitOnce(RList c);
		public abstract Optional<Y> visitOnce(RLogical c);
		public abstract Optional<Y> visitOnce(RLogicalVector c);
		//public abstract Optional<Y> visitOnce(RMatrix<?> c);
		public abstract Optional<Y> visitOnce(RNamed<?> c);
		public abstract Optional<Y> visitOnce(RNamedList c);
		public abstract Optional<Y> visitOnce(RNull c);
		public abstract Optional<Y> visitOnce(RNumeric c);
		public abstract Optional<Y> visitOnce(RNumericVector c);
		public abstract Optional<Y> visitOnce(RArray<?> c);
		public abstract Optional<Y> visitOnce(RUntypedNa c);
		public abstract Optional<Y> visitOnce(RUntypedNaVector c);
	}
	
	public static class DefaultOnceOnly<Y> extends OnceOnly<Y> {
		public Optional<Y> visitOnce(RCharacter c) {return Optional.empty();}
		public Optional<Y> visitOnce(RCharacterVector c) {return Optional.empty();}
		public Optional<Y> visitOnce(RDataframe c) {return Optional.empty();}
		public Optional<Y> visitOnce(RDataframeRow c) {return Optional.empty();}
		public Optional<Y> visitOnce(RDate c) {return Optional.empty();}
		public Optional<Y> visitOnce(RDateVector c) {return Optional.empty();}
		public Optional<Y> visitOnce(RFactor c) {return Optional.empty();}
		public Optional<Y> visitOnce(RFactorVector c) {return Optional.empty();}
		public Optional<Y> visitOnce(RInteger c) {return Optional.empty();}
		public Optional<Y> visitOnce(RIntegerVector c) {return Optional.empty();}
		public Optional<Y> visitOnce(RList c) {return Optional.empty();}
		public Optional<Y> visitOnce(RLogical c) {return Optional.empty();}
		public Optional<Y> visitOnce(RLogicalVector c) {return Optional.empty();}
		//public Optional<Y> visitOnce(RMatrix<?> c) {return Optional.empty();}
		public Optional<Y> visitOnce(RNamed<?> c) {return Optional.empty();}
		public Optional<Y> visitOnce(RNamedList c) {return Optional.empty();}
		public Optional<Y> visitOnce(RNull c) {return Optional.empty();}
		public Optional<Y> visitOnce(RNumeric c) {return Optional.empty();}
		public Optional<Y> visitOnce(RNumericVector c) {return Optional.empty();}
		public Optional<Y> visitOnce(RArray<?> c) {return Optional.empty();}
		public Optional<Y> visitOnce(RUntypedNa rna) {return Optional.empty();}
		public Optional<Y> visitOnce(RUntypedNaVector rna) {return Optional.empty();}
	}

	

	
}
