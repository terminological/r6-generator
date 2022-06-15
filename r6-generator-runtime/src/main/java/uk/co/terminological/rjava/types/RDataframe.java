package uk.co.terminological.rjava.types;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.co.terminological.rjava.AfterLastElementException;
import uk.co.terminological.rjava.BeforeFirstElementException;
import uk.co.terminological.rjava.IncompatibleTypeException;
import uk.co.terminological.rjava.NameNotFoundException;
import uk.co.terminological.rjava.RConverter;
import uk.co.terminological.rjava.RDataType;
import uk.co.terminological.rjava.RObjectVisitor;
import uk.co.terminological.rjava.UnconvertableTypeException;


/**
 * A java equivalent of the R Dataframe organised in column format. This method has various accessors for iterating
 * over or streaming the contents
 * @author terminological
 *
 */
/**
 * @author terminological
 *
 */
@RDataType(
		JavaToR = { 
				"function(jObj) {",
				// dynamically construct a local conversion function based on structure of dataframe
				"	convDf = eval(parse(text=rJava::.jcall(jObj,'rConversion', returnSig='Ljava/lang/String;')))",
				"	groups = rJava::.jcall(jObj,returnSig='[Ljava/lang/String;',method='getGroups')",
				"	return(dplyr::group_by(convDf(jObj),!!!sapply(groups,as.symbol)))",
				"}"
		}, 
		RtoJava = { 
				"function(rObj) {", 
				"	jout = rJava::.jnew('~RDATAFRAME~')",
				"	lapply(colnames(rObj), function(x) {",
				"		rcol = rObj[[x]]",
				// select correct conversion function using naming convention
				"		if(is.character(rcol)) jvec = ~TO_RCHARACTERVECTOR~(rcol)",
				"		else if(is.integer(rcol)) jvec = ~TO_RINTEGERVECTOR~(rcol)",
				"		else if(is.factor(rcol)) jvec = ~TO_RFACTORVECTOR~(rcol)",
				"		else if(is.logical(rcol)) jvec = ~TO_RLOGICALVECTOR~(rcol)",
				"		else if(is.numeric(rcol)) jvec = ~TO_RNUMERICVECTOR~(rcol)",
				"		else if(inherits(rcol,c('Date','POSIXt'))) jvec = ~TO_RDATEVECTOR~(rcol)",
				"		else stop('unsupported data type in column: ',x)",
				// do the call to add columns by name and rvector wrapper
				"		rJava::.jcall(jout,returnSig='V',method='addCol',x,rJava::.jcast(jvec,new.class='~RVECTOR~'))",		
				"	})",
				"	rJava::.jcall(jout,returnSig='L~RDATAFRAME~;',method='groupBy',rJava::.jarray(dplyr::group_vars(rObj)))",
				"	return(jout)",
				"}"
		}
		//JNIType = "Luk/co/terminological/rjava/types/RDataframe;"
	)
public class RDataframe extends LinkedHashMap<String, RVector<? extends RPrimitive>> implements RCollection<RDataframeRow> {
	
	private static final long serialVersionUID = RObject.datatypeVersion;
	private static Logger log = LoggerFactory.getLogger(RVector.class);
	
	private LinkedHashSet<String> groups = new LinkedHashSet<>();
	
	//TODO: some form of indexing to speed up group operations would be desirable.
	//However it would need to keep up to date with the underlying data structure.
	//Questionable whether it should be part of serialisation... Maybe it should...
	//transient Map<RNamedPrimitives,RDataframe> groupData = new HashMap<RNamedPrimitives,RDataframe>();
	//The answer I think is to map this directly from R into an in memory H2 database table (indexed on grouping structures).
	
	public RDataframe() {
		super();
	}

	public RDataframe(RDataframe dataframe) {
		super(dataframe);
	}

	public static RDataframe create() {
		return new RDataframe();
	}
	
	public String[] getGroups() {return groups.toArray(new String[] {});}
	public Set<String> groupSet() {return groups;}
	
	public RDataframe groupBy(String... groups) {
		this.groups = new LinkedHashSet<String>();
		return groupByAdditional(groups);
	}
	
	public RDataframe groupByAdditional(String... groups) {
		if (groups==null) return this;
		Arrays.asList(groups).stream().filter(s -> s!=null & this.containsKey(s)).forEach(this.groups::add);
		return this;
	}

	public String toString() {
		return "groups: "+this.groups+"\n"+
			this.entrySet().stream()
			.map(
				kv -> kv.getKey() +": "+ kv.getValue().toString()
			).collect(Collectors.joining("\n"));
	}

	public int nrow() {
		if (this.size()==0) return 0;
		String key = this.keySet().iterator().next();
		return this.get(key).size();
	}
	
	public int ncol() {
		return this.size();
	}
	
	public Class<? extends RPrimitive> getTypeOfColumn(String name) {
		return this.get(name).getType();
	}
	
	@SuppressWarnings("unchecked")
	public Class<? extends RVector<?>> getVectorTypeOfColumn(String name) { //throws IncompatibleTypeException {
		return (Class<? extends RVector<?>>) this.get(name).getClass();
//		if (this.get(name).getType().equals(RCharacter.class)) return RCharacterVector.class;
//		if (this.get(name).getType().equals(RInteger.class)) return RIntegerVector.class;
//		if (this.get(name).getType().equals(RNumeric.class)) return RNumericVector.class;
//		if (this.get(name).getType().equals(RFactor.class)) return RFactorVector.class;
//		if (this.get(name).getType().equals(RLogical.class)) return RLogicalVector.class;
//		if (this.get(name).getType().equals(RDate.class)) return RDateVector.class;
//		throw new IncompatibleTypeException("Unsupported type in column: "+name);
	}
	
	private <X extends RPrimitive> void ensureColumnExists(String name, Class<X> type) {
		if (!this.containsKey(name)) {
			this.put(name, RVector.ofNA(type, nrow()));
		}
	}
	
	public synchronized void addRow(Map<String,Object> row) {
		row.forEach((k,v) -> {
			if(this.containsKey(k)) {
				try {
					this.get(k).add(RConverter.convertObjectToPrimitive(v));
				} catch (UnconvertableTypeException e) {
					throw new IncompatibleTypeException("Unsupported type in column: "+k,e);
				}
			} else {
				try {
					RPrimitive prim = RConverter.convertObjectToPrimitive(v);
					this.put(k, RVector.padded(prim, nrow()));
				} catch (UnconvertableTypeException e) {
					throw new IncompatibleTypeException("Unsupported type in column: "+k,e);
				} 
				
			}
		});
	}
	
	public synchronized void addRow(RNamedPrimitives row) {
		row.forEach((k,v) -> {
			if(this.containsKey(k)) {
				//This is updating map in situation where adding to an untyped NA vector declares its type.
				this.put(k, this.get(k).addUnsafe(v));
			} else {
				this.put(k, RVector.padded(v, nrow()));
			}
		});
	}
	
	public synchronized RDataframe withRow(Map<String,Object> row) {
		this.addRow(row);
		return this;
	}
	
	public synchronized RDataframe withRow(RNamedPrimitives row) {
		this.addRow(row);
		return this;
	}
	
	public RDataframeRow getRow(int i) {
		if (i<0) throw new BeforeFirstElementException();
		if (i>=this.nrow()) throw new AfterLastElementException(); 
		return new RDataframeRow(this, i);
	}
	
	public synchronized void addCol(String s, RVector<?> col) {
		if (this.ncol() > 0 && col.size() != this.nrow()) throw new IncompatibleTypeException("Array is the incorrect length for column:" + s);
		if (this.containsKey(s)) throw new IncompatibleTypeException("Column name '"+s+"' already exists in dataframe");
		this.put(s, col);
	}
	

	public synchronized <X extends RPrimitive> void addCol(String k, X v) {
		int rows=this.nrow();
		RVector<X> col;
		if (rows==0)
			col = RVector.singleton(v);
		else 
			col = RVector.rep(v, rows);
		this.addCol(k, col);
	}
	
	public RDataframe withCol(String s, RVector<?> col) {
		this.addCol(s, col);
		return this;
	}
	
	public <X extends RPrimitive> RDataframe withCol(String s, X col) {
		this.addCol(s, col);
		return this;
	}
	
	public RDataframe withColIfAbsent(String s, RVector<?> col) {
		if (!this.containsKey(s))
			this.addCol(s, col);
		return this;
	}
	
	public <X extends RPrimitive> RDataframe withColIfAbsent(String s, X col) {
		if (!this.containsKey(s))
			this.addCol(s, col);
		return this;
	}
	
	public <X extends RPrimitive> RDataframe mergeWithCol(String s, X col, BiFunction<X,X,X> mergeOperation) {
		if (!this.containsKey(s))
			this.addCol(s, col);
		else {
			mergeWithCol(s, RVector.rep(col, this.nrow()), mergeOperation);
		}
		return this;
	}
	
	public <X extends RPrimitive> RDataframe mergeWithCol(String s, RVector<X> col, BiFunction<X,X,X> mergeOperation) {
		if (!this.containsKey(s))
			this.addCol(s, col);
		else {
			if (!this.getVectorTypeOfColumn(s).equals(col.getClass())) {
				throw new IncompatibleTypeException(
					"Tried to merge column "+s+" with a column of "+col.getType().getSimpleName()+" but it is a "+this.getVectorTypeOfColumn(s).getSimpleName());
			} else if (col.size() != this.nrow()) {
				throw new IncompatibleTypeException(
						"Tried to merge column "+s+" with a length of "+col.size()+" but daatframe had length "+this.nrow());
			} else {
				RVector<?> source = this.get(s);
				for (int i=0;i<col.size();i++) {
					@SuppressWarnings("unchecked")
					X value = mergeOperation.apply((X) source.get(i), col.get(i));
					col.set(i, value);
				}
				this.replace(s, col);
			}
		}
		return this;
	}
	
	public RVector<?> getCol(String name) {
		return this.get(name);
	}
	
	public synchronized void bindRows(RDataframe rows) {
		rows.keySet().forEach(name -> this.ensureColumnExists(name,rows.getTypeOfColumn(name)));
		int appendNrow = rows.nrow();
		this.keySet().forEach(k -> {
			if (rows.containsKey(k)) {
				this.put(k, this.get(k).addAllUnsafe(rows.get(k)));
			} else {
				//Columns that are not in dataframe to be bound need to have NAs added
				this.get(k).fillNA(appendNrow);
			}
		});
	}
	
	public RDataframe withRows(RDataframe rows) {
		this.bindRows(rows);
		return this;
	}
	
	public RDataframe withCols(RDataframe cols) {
		this.bindCols(cols);
		return this;
	}
	
	public synchronized void bindCols(RDataframe cols) {
		if (this.nrow() != cols.nrow()) throw new IncompatibleTypeException("Dataframes are not the same length");
		cols.columnIterator().forEachRemaining(s->this.addCol(s.getKey(), s.getValue()));
	}
	
	
	
	public <X> RBoundDataframe<X> attach(Class<X> type) throws UnconvertableTypeException {
		return new RBoundDataframe<X>(type,this);
	}
	
	public <X> RBoundDataframe<X> attachPermissive(Class<X> type) throws UnconvertableTypeException {
		return new RBoundDataframe<X>(type,this,false);
	}
	
	/**
	 * @param type an interface definition with getter methods that specify the correct RPrimitive datatype of the each named column.
	 * @return a stream of the interface type
	 * @throws UnconvertableTypeException 
	 */
	public <T> Stream<T> stream(Class<T> type) throws UnconvertableTypeException {
		return attach(type).streamCoerce();
	}
	
	public Stream<LinkedHashMap<String,Object>> streamJava() {
		return IntStream.range(0, RDataframe.this.size()).mapToObj(row -> {
			LinkedHashMap<String,Object> tmp = new LinkedHashMap<>();
			RDataframe.this.keySet().forEach(k -> tmp.put(k, RConverter.unconvert(RDataframe.this.get(k).get(row))));
			return tmp;
		});
	}
	
	public Iterator<RNamed<RVector<?>>> columnIterator() {
		return new Iterator<RNamed<RVector<?>>>() {
			Iterator<Map.Entry<String, RVector<? extends RPrimitive>>> it = RDataframe.this.entrySet().iterator();
			@Override
			public boolean hasNext() {
				return it.hasNext();
			}

			@Override
			public RNamed<RVector<?>> next() {
				return RNamed.from(it.next());
			}
			
		};
		
	}
	
	public String[] rKeys() {
		return this.keySet().toArray(new String[] {});
	}
	
	public RVector<?> rColumn(String key) {
		return this.get(key);
	}
	
	public String rConversion() {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		PrintStream sb = new PrintStream(baos);
		sb.println("function(jObj) {");
		// create a function for translating each of the component columns
		List<String> keyList = new ArrayList<String>(this.keySet());
		for(int i = 0; i<this.ncol(); i++) {
			String k = keyList.get(i);
			String colFunction = Stream.of(this.getVectorTypeOfColumn(k).getAnnotation(RDataType.class).JavaToR()).collect(Collectors.joining("\n"));
			String fnName = "convert_"+i;
			sb.append(fnName+" = "+colFunction+"\n");
		}
		//get jobj references for each of the vector columns
		for(int i = 0; i<this.ncol(); i++) {
			String k = keyList.get(i);
			String returnSig = "L"+/*this.getTypeOfColumn(k)*/ RVector.class.getCanonicalName().replace(".", "/")+";";
			String actualSig = "L"+this.getVectorTypeOfColumn(k).getCanonicalName().replace(".", "/")+";";
			sb.println("tmp_"+i+" = rJava::.jcall(obj=jObj,returnSig='"+returnSig+"',method='rColumn','"+k+"')");
			//.jcast(obj, new.class = "java/lang/Object"
			sb.println("tmp_"+i+" = rJava::.jcast(tmp_"+i+",new.class='"+actualSig+"')");
		}
		//construct the tibble
		sb.println("\nreturn(tibble::tibble(");
		for(int i = 0; i<this.ncol(); i++) {
			String k = keyList.get(i);
			sb.println("`"+k+"` = convert_"+i+"(tmp_"+i+")"+(i==this.ncol()-1?"":","));
		}
		sb.println("))");
		sb.println("}");
		return baos.toString();
	}

	
	@Override
	public Iterator<RDataframeRow> iterator() {
		int nrow = nrow();
		return new Iterator<RDataframeRow>() {
			int i=0;

			@Override
			public boolean hasNext() {
				return i<nrow;
			}

			@Override
			public RDataframeRow next() {
				i = i+1;
				return getRow(i-1);
			}
			
		};
	}
	
	public String rCode() {
		return "tibble::tibble("+
				this.entrySet().stream()
				.map(kv -> kv.getKey()+"="+kv.getValue().rCode())
				.collect(Collectors.joining(", "))+")";
	}

	
	@Override
	public <X> X accept(RObjectVisitor<X> visitor) {
		X out = visitor.visit(this);
		this.columnIterator().forEachRemaining(c -> c.accept(visitor));
		return out;
	}
	
	public Set<RNamedPrimitives> distinct() {
		Set<RNamedPrimitives> tmp = new LinkedHashSet<>();
		for (RDataframeRow row: this) {
			tmp.add((RNamedPrimitives) row);
		}
		return tmp;
	}
	
	public Map<RNamedPrimitives,RDataframe> groupData() {
		Map<RNamedPrimitives,RDataframe> tmp = new HashMap<>();
		this.stream().forEach(r -> {
			RNamedPrimitives row = r.rowGroup();
			if (!tmp.containsKey(row)) tmp.put(row, new RDataframe());
			tmp.get(row).addRow(r);
		});
		return tmp;
	}

	public RDataframe select(String... columns) {
		LinkedHashSet<String> cols = new LinkedHashSet<String>(this.groups);
		cols.addAll(Arrays.asList(columns));
		String addingGrp = cols.stream().filter(c -> this.groups.contains(c)).collect(Collectors.joining(", ")); 
		if (!addingGrp.isEmpty()) log.debug("Adding grouping columns to select: "+addingGrp);
		RDataframe out = new RDataframe();
		for (String col: cols) {
			if (this.containsKey(col))
				out.addCol(col, this.get(col));
		}
		out.groupBy(this.getGroups());
		return out;
	}
	
	public RDataframe ungroup() {
		this.groups = new LinkedHashSet<>();
		return this;
	}
	
	public RDataframe drop(String... columns) {
		Set<String> cols = new LinkedHashSet<>(this.keySet());
		Stream.of(columns).forEach(cols::remove);
		return this.select(cols.toArray(new String[] {}));
	}
	
	public RDataframe filter(RNamedPrimitives match) {
		BitSet filter = (new BitSet(this.nrow()));
		filter.set(0, this.nrow(), true);
		match.forEach((k,v) -> { 
			BitSet and = this.get(k).matches(v);
			filter.and(and); 
		});
		final BitSet filter2 = filter;
		RDataframe out = new RDataframe();
		this.columnIterator().forEachRemaining(n -> {
			out.addCol(n.getKey(), n.getValue().subset(filter2));
		});
		out.groupBy(this.getGroups());
		return out;
	}
	
	/**
	 *  Filter a dataframe and return a new dataframe containing entrie that pass predicate
	 * @param name - the column to test
	 * @param type - the type of the column (for type hinting)
	 * @param predicate - a test of the individual contents of the column
	 * @return A new dataframe
	 */
	public <Y extends RPrimitive> RDataframe filter(String name, Class<Y> type, Predicate<Y> predicate) {
		return filter(RNamedPredicate.from(name, type, predicate));
	}
	
	/**
	 *  Filter a dataframe and return a new dataframe containing entrie that pass predicate
	 * @param name - the column to test
	 * @param predicate - a test of the individual contents of the column
	 * @return A new dataframe
	 */
	public <Y extends RPrimitive> RDataframe filter(String name, Predicate<Y> predicate) {
		return filter(RNamedPredicate.from(name, predicate));
	}
	
	/**
	 * Filter a dataframe and return a new dataframe
	 * @param tests A set of tests
	 * @return a new dataframe containing only items which pass all the filter test
	 */
	public RDataframe filter(RNamedPredicate<?>... tests) {
		
		// return everything if no conditions
		BitSet filter = (new BitSet(this.nrow()));
		filter.set(0, this.nrow(), true);
		
		for (RNamedPredicate<?> test: tests) {
			BitSet and = this.get(test.name()).matches(test.predicate());
			filter.and(and); 
		}
		final BitSet filter2 = filter;
		RDataframe out = new RDataframe();
		this.columnIterator().forEachRemaining(n -> {
			out.addCol(n.getKey(), n.getValue().subset(filter2));
		});
		out.groupBy(this.getGroups());
		return out;
	}
	
	public RDataframe groupModify(BiFunction<RDataframe, RNamedPrimitives, RDataframe> func) {
		RDataframe out = new RDataframe();
		Map<RNamedPrimitives, RDataframe> groupData = this.groupData();
		if (groupData.isEmpty()) {
			groupData = new HashMap<>();
			groupData.put(new RNamedPrimitives(),this);
		}
		
		groupData.entrySet().parallelStream().map(group -> {
			RDataframe subgroup = group.getValue().ungroup().drop(this.getGroups());
			RNamedPrimitives grouping = group.getKey();
			RDataframe subgroupOut = func.apply(subgroup, grouping);
			RDataframe groupOut = grouping.toDataframe(subgroupOut.nrow());
			subgroupOut.forEach((k,v) -> groupOut.addCol(k, v));
			return groupOut;
		}).forEach(out::bindRows);
		
		out.groupBy(this.getGroups());
		return out;
	}

	
	

	public <Y extends RVector<?>> Y pull(String col,Class<Y> vectorClass) {
		if (!this.containsKey(col)) throw new NameNotFoundException(col);
		return this.get(col).as(vectorClass);
	}
	
	public RVector<?> pull(String col) {
		if (!this.containsKey(col)) throw new NameNotFoundException(col);
		return this.get(col);
	}

	public RDataframe rename(String to, String from) {
		if (from.equals(to)) return this;
		RDataframe out = new RDataframe(this);
		out.addCol(to, out.get(from));
		out.remove(from);
		return out;
	}

	public RDataframe subset(int start, int end) {
		RDataframe out = new RDataframe();
		IntStream.range(start, end).forEach(i -> out.addRow(this.getRow(i)));
		return out;
	}
	
	public RDataframe count() {
		return this.groupModify((d,g) -> 
			RDataframe.create().withCol("n", 
					RVector.with(
							d.nrow()
						)
					)
		);
	}
	
	public String asCsv() {
		StringBuilder out = new StringBuilder();
		out.append(
				this.keySet().stream().map(s -> "\""+s+"\"").collect(Collectors.joining(","))+"\n"
				);
		this.stream().map(row -> row.asCsv()).forEach(out::append);
		return out.toString();
	}

	
	/**
	 * Returns the same data frame with the column "columnName" 
	 * @param columnName -  the name of the column to mutate
	 * @param inputType - the type of the original column
	 * @param mapping - the operation to apply to the column (must result in an RPrimitive of some type)
	 * @return the same dataframe with a changed column
	 */
	public <X extends RPrimitive,Y extends RPrimitive> RDataframe mutate(String columnName, Class<X> inputType, Function<X,Y> mapping) {
		return mutate(columnName,mapping);
	}
	
	
	/**
	 * Returns the same data frame with the column "columnName" 
	 * @param columnName -  the name of the column to mutate
	 * @param mapping - the operation to apply to the column (must result in an RPrimitive of some type)
	 * @return the same dataframe with a changed column
	 */
	@SuppressWarnings("unchecked")
	public <X extends RPrimitive,Y extends RPrimitive> RDataframe mutate(String columnName, Function<X,Y> mapping) {
		try {
			RVector<X> tmp = (RVector<X>) this.get(columnName);
			List<Y> tmp2 = tmp.stream().map(mapping).collect(Collectors.toList());
			RVector<Y> tmp3 = (RVector<Y>) RVector.empty(tmp2.get(0).getClass());
			tmp2.forEach(tmp3::add);
			this.replace(columnName, tmp3);
		} catch (ClassCastException e) {
			throw new IncompatibleTypeException("The column type is not compatible with the function class");
		}
		return this;
	}

	
}