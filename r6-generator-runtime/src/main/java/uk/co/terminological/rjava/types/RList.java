package uk.co.terminological.rjava.types;

import java.util.ArrayList;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import uk.co.terminological.rjava.RConverter;
import uk.co.terminological.rjava.RDataType;
import uk.co.terminological.rjava.UnconvertableTypeException;
import uk.co.terminological.rjava.utils.RObjectVisitor;

/** The R List is a flexible untyped list rather like a JSON document. Any kind of content can be included 
 * (as long as it is wrapped as an {@link RObject}). Using content from lists will require type checking
 * 
 * @author terminological
 *
 */
@RDataType(
		JavaToR = { 
				"function(jObj) {",
				// get java to serialise code into an evaluatable bit of R code
				"	tmp = eval(parse(text=rJava::.jcall(jObj,'rCode', returnSig='Ljava/lang/String;')))",
				"	return(tmp)",
				"}"
		}, 
		RtoJava = { 
				"function(rObj) {",
				"   if (!is.list(rObj)) stop ('expecting a list ')",
				"   if (!is.null(names(rObj))) warning('not expecting list to be named')",
				"	jout = rJava::.jnew('~RLIST~')",
				"	lapply(rObj, function(x) {",
				// If x is a dataframe dispatch it to ~TO_RDATAFRAME~
				"		if (is.null(x)) tmp = ~TO_RNULL~(x)",
				"		else if (is.data.frame(x)) tmp = ~TO_RDATAFRAME~(x)",
				"		else if (is.list(x) & !is.null(names(x))) tmp = ~TO_RNAMEDLIST~(x)",
				"		else if (is.list(x)) tmp = ~TO_RLIST~(x)",
				// TODO: add in matrix
				"		else if (is.array(x) & is.numeric(x)) tmp = ~TO_RNUMERICARRAY~(x)",	
				// Length one
				"		else if (length(x) == 1 & is.character(x)) tmp = ~TO_RCHARACTER~(x)",
				"		else if (length(x) == 1 & is.integer(x)) tmp = ~TO_RINTEGER~(x)",
				"		else if (length(x) == 1 & is.factor(x)) tmp = ~TO_RFACTOR~(x)",
				"		else if (length(x) == 1 & is.logical(x)) tmp = ~TO_RLOGICAL~(x)",
				"		else if (length(x) == 1 & is.numeric(x)) tmp = ~TO_RNUMERIC~(x)",
				"		else if (length(x) == 1 & inherits(x,c('Date','POSIXt'))) tmp = ~TO_RDATE~(x)",
				// Vectors
				"		else if (is.character(x)) tmp = ~TO_RCHARACTERVECTOR~(x)",
				"		else if (is.integer(x)) tmp = ~TO_RINTEGERVECTOR~(x)",
				"		else if (is.factor(x)) tmp = ~TO_RFACTORVECTOR~(x)",
				"		else if (is.logical(x)) tmp = ~TO_RLOGICALVECTOR~(x)",
				"		else if (is.numeric(x)) tmp = ~TO_RNUMERICVECTOR~(x)",
				"		else if (inherits(x,c('Date','POSIXt'))) tmp = ~TO_RDATEVECTOR~(x)",
				// If x is a dataframe dispatch it to ~TO_RDATAFRAME~
				// could issue a warning and put in a null?
				"		else stop ('unrecognised type: ',class(x),' with value ',x)",		
				// Add to list
				"		rJava::.jcall(jout,returnSig='Z',method='add',rJava::.jcast(tmp, new.class='~ROBJECT~'))",		
				"	})",
				"	return(jout)",
				"}"
		}
		//JNIType = "D"
	)
public class RList extends ArrayList<RObject> implements RCollection<RObject> {

	private static final long serialVersionUID = RObject.datatypeVersion;
	
	public boolean add(RObject o) {
		return super.add(o);
	}
	
	public boolean addRaw(Object o) throws UnconvertableTypeException {
		return this.add(RConverter.convertObject(o));
	}
	
	@Override
	public String rCode() {
		return "list("+this.stream().map(v -> v.rCode()).collect(Collectors.joining(", "))+")";
	}
	
	public RList andRaw(Object... o) throws UnconvertableTypeException {
		for (Object obj: o) {
			this.addRaw(obj);
		}
		return this;
	}
	
	public static RList withRaw(Object... o) throws UnconvertableTypeException {
		RList out = new RList();
		out.andRaw(o);
		return out;
	}
	
	public static RList with(RObject... o) {
		RList out = new RList();
		out.and(o);
		return out;
	}
	
	public RList and(RObject... o) {
		Stream.of(o).forEach(this::add);
		return this;
	}

	@Override
	public <X> X accept(RObjectVisitor<X> visitor) {
		X out = visitor.visit(this);
		this.forEach(c -> c.accept(visitor));
		return out;
	}

	@Override
	public Stream<RObject> stream() {
		return super.stream();
	}

	@Override
	public Stream<RObject> parallelStream() {
		return super.parallelStream();
	}
	
	public String toString() {
		return "<rlist>{\n"+this.stream().map(kv -> kv.toString() ).collect(Collectors.joining(",\n"))+"\n}";
	}
	
}
