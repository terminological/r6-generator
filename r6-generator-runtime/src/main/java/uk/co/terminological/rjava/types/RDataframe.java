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
import uk.co.terminological.rjava.UnconvertableTypeException;
import uk.co.terminological.rjava.functionals.RFilter;
import uk.co.terminological.rjava.utils.RObjectVisitor;


/**
 * A java equivalent of the R Dataframe organised in column format. This method has various accessors for iterating
 * over or streaming the contents
 *
 * @author terminological
 * @version $Id: $Id
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
	
	/**
	 * <p>Constructor for RDataframe.</p>
	 */
	public RDataframe() {
		super();
	}

	/**
	 * <p>Constructor for RDataframe.</p>
	 *
	 * @param dataframe a {@link uk.co.terminological.rjava.types.RDataframe} object
	 */
	public RDataframe(RDataframe dataframe) {
		super(dataframe);
	}

	/**
	 * <p>create.</p>
	 *
	 * @return a {@link uk.co.terminological.rjava.types.RDataframe} object
	 */
	public static RDataframe create() {
		return new RDataframe();
	}
	
	/**
	 * <p>Getter for the field <code>groups</code>.</p>
	 *
	 * @return an array of {@link java.lang.String} objects
	 */
	public String[] getGroups() {return groups.toArray(new String[] {});}
	/**
	 * <p>groupSet.</p>
	 *
	 * @return a {@link java.util.Set} object
	 */
	public Set<String> groupSet() {return groups;}
	
	/**
	 * <p>groupBy.</p>
	 *
	 * @param groups a {@link java.lang.String} object
	 * @return a {@link uk.co.terminological.rjava.types.RDataframe} object
	 */
	public RDataframe groupBy(String... groups) {
		this.groups = new LinkedHashSet<String>();
		return groupByAdditional(groups);
	}
	
	/**
	 * <p>groupByAdditional.</p>
	 *
	 * @param groups a {@link java.lang.String} object
	 * @return a {@link uk.co.terminological.rjava.types.RDataframe} object
	 */
	public RDataframe groupByAdditional(String... groups) {
		if (groups==null) return this;
		Arrays.asList(groups).stream().filter(s -> s!=null & this.containsKey(s)).forEach(this.groups::add);
		return this;
	}

	/**
	 * <p>toString.</p>
	 *
	 * @return a {@link java.lang.String} object
	 */
	public synchronized String toString() {
		return "groups: "+this.groups+"\n"+
			this.entrySet().stream()
			.map(
				kv -> kv.getKey() +": "+ kv.getValue().toString()
			).collect(Collectors.joining("\n"));
	}

	/**
	 * <p>nrow.</p>
	 *
	 * @return a int
	 */
	public int nrow() {
		if (this.size()==0) return 0;
		String key = this.keySet().iterator().next();
		return this.get(key).size();
	}
	
	/**
	 * <p>ncol.</p>
	 *
	 * @return a int
	 */
	public synchronized int ncol() {
		return this.size();
	}
	
	/**
	 * <p>getTypeOfColumn.</p>
	 *
	 * @param name a {@link java.lang.String} object
	 * @return a {@link java.lang.Class} object
	 */
	public synchronized Class<? extends RPrimitive> getTypeOfColumn(String name) {
		return this.get(name).getType();
	}
	
	/**
	 * <p>getVectorTypeOfColumn.</p>
	 *
	 * @param name a {@link java.lang.String} object
	 * @return a {@link java.lang.Class} object
	 */
	@SuppressWarnings("unchecked")
	public synchronized Class<? extends RVector<?>> getVectorTypeOfColumn(String name) { //throws IncompatibleTypeException {
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
	
	/**
	 * <p>addRow.</p>
	 *
	 * @param row a {@link java.util.Map} object
	 */
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
	
	/**
	 * <p>addRow.</p>
	 *
	 * @param row a {@link uk.co.terminological.rjava.types.RNamedPrimitives} object
	 */
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
	
	/**
	 * <p>withRow.</p>
	 *
	 * @param row a {@link java.util.Map} object
	 * @return a {@link uk.co.terminological.rjava.types.RDataframe} object
	 */
	public synchronized RDataframe withRow(Map<String,Object> row) {
		this.addRow(row);
		return this;
	}
	
	/**
	 * <p>withRow.</p>
	 *
	 * @param row a {@link uk.co.terminological.rjava.types.RNamedPrimitives} object
	 * @return a {@link uk.co.terminological.rjava.types.RDataframe} object
	 */
	public synchronized RDataframe withRow(RNamedPrimitives row) {
		this.addRow(row);
		return this;
	}
	
	/**
	 * <p>getRow.</p>
	 *
	 * @param i a int
	 * @return a {@link uk.co.terminological.rjava.types.RDataframeRow} object
	 */
	public synchronized RDataframeRow getRow(int i) {
		if (i<0) throw new BeforeFirstElementException();
		if (i>=this.nrow()) throw new AfterLastElementException(); 
		return new RDataframeRow(this, i);
	}
	
	/**
	 * <p>addCol.</p>
	 *
	 * @param s a {@link java.lang.String} object
	 * @param col a {@link uk.co.terminological.rjava.types.RVector} object
	 */
	public synchronized void addCol(String s, RVector<?> col) {
		if (this.ncol() > 0 && col.size() != this.nrow()) throw new IncompatibleTypeException("Array is the incorrect length for column:" + s);
		if (this.containsKey(s)) throw new IncompatibleTypeException("Column name '"+s+"' already exists in dataframe");
		this.put(s, col);
	}
	

	/**
	 * <p>addCol.</p>
	 *
	 * @param k a {@link java.lang.String} object
	 * @param v a X object
	 * @param <X> a X class
	 */
	public synchronized <X extends RPrimitive> void addCol(String k, X v) {
		int rows=this.nrow();
		RVector<X> col;
		if (rows==0)
			col = RVector.singleton(v);
		else 
			col = RVector.rep(v, rows);
		this.addCol(k, col);
	}
	
	/**
	 * <p>withCol.</p>
	 *
	 * @param s a {@link java.lang.String} object
	 * @param col a {@link uk.co.terminological.rjava.types.RVector} object
	 * @return a {@link uk.co.terminological.rjava.types.RDataframe} object
	 */
	public RDataframe withCol(String s, RVector<?> col) {
		this.addCol(s, col);
		return this;
	}
	
	/**
	 * <p>withCol.</p>
	 *
	 * @param s a {@link java.lang.String} object
	 * @param col a X object
	 * @param <X> a X class
	 * @return a {@link uk.co.terminological.rjava.types.RDataframe} object
	 */
	public <X extends RPrimitive> RDataframe withCol(String s, X col) {
		this.addCol(s, col);
		return this;
	}
	
	/**
	 * <p>withColIfAbsent.</p>
	 *
	 * @param s a {@link java.lang.String} object
	 * @param col a {@link uk.co.terminological.rjava.types.RVector} object
	 * @return a {@link uk.co.terminological.rjava.types.RDataframe} object
	 */
	public synchronized RDataframe withColIfAbsent(String s, RVector<?> col) {
		if (!this.containsKey(s))
			this.addCol(s, col);
		return this;
	}
	
	/**
	 * <p>withColIfAbsent.</p>
	 *
	 * @param s a {@link java.lang.String} object
	 * @param col a X object
	 * @param <X> a X class
	 * @return a {@link uk.co.terminological.rjava.types.RDataframe} object
	 */
	public synchronized <X extends RPrimitive> RDataframe withColIfAbsent(String s, X col) {
		if (!this.containsKey(s))
			this.addCol(s, col);
		return this;
	}
	
	/**
	 * <p>mergeWithCol.</p>
	 *
	 * @param s a {@link java.lang.String} object
	 * @param col a X object
	 * @param mergeOperation a {@link java.util.function.BiFunction} object
	 * @param <X> a X class
	 * @return a {@link uk.co.terminological.rjava.types.RDataframe} object
	 */
	public synchronized <X extends RPrimitive> RDataframe mergeWithCol(String s, X col, BiFunction<X,X,X> mergeOperation) {
		if (!this.containsKey(s))
			this.addCol(s, col);
		else {
			mergeWithCol(s, RVector.rep(col, this.nrow()), mergeOperation);
		}
		return this;
	}
	
	/**
	 * <p>mergeWithCol.</p>
	 *
	 * @param s a {@link java.lang.String} object
	 * @param col a {@link uk.co.terminological.rjava.types.RVector} object
	 * @param mergeOperation a {@link java.util.function.BiFunction} object
	 * @param <X> a X class
	 * @return a {@link uk.co.terminological.rjava.types.RDataframe} object
	 */
	public synchronized <X extends RPrimitive> RDataframe mergeWithCol(String s, RVector<X> col, BiFunction<X,X,X> mergeOperation) {
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
	
	/**
	 * <p>getCol.</p>
	 *
	 * @param name a {@link java.lang.String} object
	 * @return a {@link uk.co.terminological.rjava.types.RVector} object
	 */
	public synchronized RVector<?> getCol(String name) {
		return this.get(name);
	}
	
	/**
	 * <p>bindRows.</p>
	 *
	 * @param rows a {@link uk.co.terminological.rjava.types.RDataframe} object
	 */
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
	
	/**
	 * <p>withRows.</p>
	 *
	 * @param rows a {@link uk.co.terminological.rjava.types.RDataframe} object
	 * @return a {@link uk.co.terminological.rjava.types.RDataframe} object
	 */
	public RDataframe withRows(RDataframe rows) {
		this.bindRows(rows);
		return this;
	}
	
	/**
	 * <p>withCols.</p>
	 *
	 * @param cols a {@link uk.co.terminological.rjava.types.RDataframe} object
	 * @return a {@link uk.co.terminological.rjava.types.RDataframe} object
	 */
	public RDataframe withCols(RDataframe cols) {
		this.bindCols(cols);
		return this;
	}
	
	/**
	 * <p>bindCols.</p>
	 *
	 * @param cols a {@link uk.co.terminological.rjava.types.RDataframe} object
	 */
	public synchronized void bindCols(RDataframe cols) {
		if (this.nrow() != cols.nrow()) throw new IncompatibleTypeException("Dataframes are not the same length");
		cols.columnIterator().forEachRemaining(s->this.addCol(s.getKey(), s.getValue()));
	}
	
	
	
	/**
	 * <p>attach.</p>
	 *
	 * @param type a {@link java.lang.Class} object
	 * @param <X> a X class
	 * @return a {@link uk.co.terminological.rjava.types.RBoundDataframe} object
	 * @throws uk.co.terminological.rjava.UnconvertableTypeException if any.
	 */
	public <X> RBoundDataframe<X> attach(Class<X> type) throws UnconvertableTypeException {
		return new RBoundDataframe<X>(type,this);
	}
	
	/**
	 * <p>attachPermissive.</p>
	 *
	 * @param type a {@link java.lang.Class} object
	 * @param <X> a X class
	 * @return a {@link uk.co.terminological.rjava.types.RBoundDataframe} object
	 * @throws uk.co.terminological.rjava.UnconvertableTypeException if any.
	 */
	public <X> RBoundDataframe<X> attachPermissive(Class<X> type) throws UnconvertableTypeException {
		return new RBoundDataframe<X>(type,this,false);
	}
	
	/**
	 * <p>stream.</p>
	 *
	 * @param type an interface definition with getter methods that specify the correct RPrimitive datatype of the each named column.
	 * @param <T> - any output type defined as a java interface
	 * @return a stream of the interface type T
	 * @throws uk.co.terminological.rjava.UnconvertableTypeException if the dataframe row cannot be coerced to type T
	 */
	public <T> Stream<T> stream(Class<T> type) throws UnconvertableTypeException {
		return attach(type).streamCoerce();
	}
	
	/**
	 * <p>streamJava.</p>
	 *
	 * @return a {@link java.util.stream.Stream} object
	 */
	public synchronized Stream<LinkedHashMap<String,Object>> streamJava() {
		return IntStream.range(0, RDataframe.this.size()).mapToObj(row -> {
			LinkedHashMap<String,Object> tmp = new LinkedHashMap<>();
			RDataframe.this.keySet().forEach(k -> tmp.put(k, RConverter.unconvert(RDataframe.this.get(k).get(row))));
			return tmp;
		});
	}
	
	/**
	 * <p>columnIterator.</p>
	 *
	 * @return a {@link java.util.Iterator} object
	 */
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
	
	/**
	 * <p>rKeys.</p>
	 *
	 * @return an array of {@link java.lang.String} objects
	 */
	public synchronized String[] rKeys() {
		return this.keySet().toArray(new String[] {});
	}
	
	/**
	 * <p>rColumn.</p>
	 *
	 * @param key a {@link java.lang.String} object
	 * @return a {@link uk.co.terminological.rjava.types.RVector} object
	 */
	public synchronized RVector<?> rColumn(String key) {
		return this.get(key);
	}
	
	/**
	 * <p>rConversion.</p>
	 *
	 * @return a {@link java.lang.String} object
	 */
	public synchronized String rConversion() {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		PrintStream sb = new PrintStream(baos) {
			public void println(String s) {
				this.print(s+"\n");
			}
		};
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

	
	/** {@inheritDoc} */
	@Override
	public synchronized Iterator<RDataframeRow> iterator() {
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
	
	/**
	 * <p>rCode.</p>
	 *
	 * @return a {@link java.lang.String} object
	 */
	public synchronized String rCode() {
		return "tibble::tibble("+
				this.entrySet().stream()
				.map(kv -> kv.getKey()+"="+kv.getValue().rCode())
				.collect(Collectors.joining(", "))+")";
	}

	
	/** {@inheritDoc} */
	@Override
	public <X> X accept(RObjectVisitor<X> visitor) {
		X out = visitor.visit(this);
		this.columnIterator().forEachRemaining(c -> c.accept(visitor));
		return out;
	}
	
	/**
	 * <p>distinct.</p>
	 *
	 * @return a {@link java.util.Set} object
	 */
	public synchronized Set<RNamedPrimitives> distinct() {
		Set<RNamedPrimitives> tmp = new LinkedHashSet<>();
		for (RDataframeRow row: this) {
			tmp.add((RNamedPrimitives) row);
		}
		return tmp;
	}
	
	/**
	 * <p>groupData.</p>
	 *
	 * @return a {@link java.util.Map} object
	 */
	public Map<RNamedPrimitives,RDataframe> groupData() {
		Map<RNamedPrimitives,RDataframe> tmp = new HashMap<>();
		this.stream().forEach(r -> {
			RNamedPrimitives row = r.rowGroup();
			if (!tmp.containsKey(row)) tmp.put(row, new RDataframe());
			tmp.get(row).addRow(r);
		});
		return tmp;
	}

	/**
	 * <p>select.</p>
	 *
	 * @param columns a {@link java.lang.String} object
	 * @return a {@link uk.co.terminological.rjava.types.RDataframe} object
	 */
	public synchronized RDataframe select(String... columns) {
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
	
	/**
	 * <p>ungroup.</p>
	 *
	 * @return a {@link uk.co.terminological.rjava.types.RDataframe} object
	 */
	public RDataframe ungroup() {
		this.groups = new LinkedHashSet<>();
		return this;
	}
	
	/**
	 * <p>drop.</p>
	 *
	 * @param columns a {@link java.lang.String} object
	 * @return a {@link uk.co.terminological.rjava.types.RDataframe} object
	 */
	public synchronized RDataframe drop(String... columns) {
		Set<String> cols = new LinkedHashSet<>(this.keySet());
		Stream.of(columns).forEach(cols::remove);
		return this.select(cols.toArray(new String[] {}));
	}
	
	/**
	 * <p>filter.</p>
	 *
	 * @param match a {@link uk.co.terminological.rjava.types.RNamedPrimitives} object
	 * @return a {@link uk.co.terminological.rjava.types.RDataframe} object
	 */
	public synchronized RDataframe filter(RNamedPrimitives match) {
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
	 *
	 * @param name - the column to test
	 * @param type - the type of the column (for type hinting)
	 * @param <Y> - any R primitive type
	 * @param predicate - a test of the individual contents of the column
	 * @return A new dataframe
	 */
	public synchronized <Y extends RPrimitive> RDataframe filter(String name, Class<Y> type, Predicate<Y> predicate) {
		return filter(RFilter.from(name, type, predicate));
	}
	
	/**
	 *  Filter a dataframe and return a new dataframe containing entries that pass predicate
	 *
	 * @param name - the column to test
	 * @param predicate - a test of the individual contents of the column
	 * @param <Y> - any R primitive type
	 * @return A new dataframe
	 */
	public <Y extends RPrimitive> RDataframe filter(String name, Predicate<Y> predicate) {
		return filter(RFilter.from(name, predicate));
	}
	
	/**
	 * Filter a dataframe and return a new dataframe
	 *
	 * @param tests A set of tests
	 * @return a new dataframe containing only items which pass all the filter test
	 */
	public synchronized RDataframe filter(RFilter<?>... tests) {
		
		// return everything if no conditions
		BitSet filter = (new BitSet(this.nrow()));
		filter.set(0, this.nrow(), true);
		
		for (RFilter<?> test: tests) {
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
	
	/**
	 * <p>groupModify.</p>
	 *
	 * @param func a {@link java.util.function.BiFunction} object
	 * @return a {@link uk.co.terminological.rjava.types.RDataframe} object
	 */
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

	
	

	/**
	 * <p>pull.</p>
	 *
	 * @param col a {@link java.lang.String} object
	 * @param vectorClass a {@link java.lang.Class} object
	 * @param <Y> a Y class
	 * @return a Y object
	 */
	public  synchronized <Y extends RVector<?>> Y pull(String col,Class<Y> vectorClass) {
		if (!this.containsKey(col)) throw new NameNotFoundException(col);
		return this.get(col).as(vectorClass);
	}
	
	/**
	 * <p>pull.</p>
	 *
	 * @param col a {@link java.lang.String} object
	 * @return a {@link uk.co.terminological.rjava.types.RVector} object
	 */
	public synchronized RVector<?> pull(String col) {
		if (!this.containsKey(col)) throw new NameNotFoundException(col);
		return this.get(col);
	}

	/**
	 * <p>rename.</p>
	 *
	 * @param to a {@link java.lang.String} object
	 * @param from a {@link java.lang.String} object
	 * @return a {@link uk.co.terminological.rjava.types.RDataframe} object
	 */
	public RDataframe rename(String to, String from) {
		if (from.equals(to)) return this;
		RDataframe out = new RDataframe(this);
		out.addCol(to, out.get(from));
		out.remove(from);
		return out;
	}

	/**
	 * <p>subset.</p>
	 *
	 * @param start a int
	 * @param end a int
	 * @return a {@link uk.co.terminological.rjava.types.RDataframe} object
	 */
	public RDataframe subset(int start, int end) {
		RDataframe out = new RDataframe();
		IntStream.range(start, end).forEach(i -> out.addRow(this.getRow(i)));
		return out;
	}
	
	/**
	 * <p>count.</p>
	 *
	 * @return a {@link uk.co.terminological.rjava.types.RDataframe} object
	 */
	public RDataframe count() {
		return this.groupModify((d,g) -> 
			RDataframe.create().withCol("n", 
					RVector.with(
							d.nrow()
						)
					)
		);
	}
	
	/**
	 * <p>asCsv.</p>
	 *
	 * @return a {@link java.lang.String} object
	 */
	public synchronized String asCsv() {
		StringBuilder out = new StringBuilder();
		out.append(
				this.keySet().stream().map(s -> "\""+s+"\"").collect(Collectors.joining(","))+"\n"
				);
		this.stream().map(row -> row.asCsv()).forEach(out::append);
		return out.toString();
	}

	
	/**
	 * Returns the same data frame with the column "columnName"
	 *
	 * @param columnName -  the name of the column to mutate
	 * @param inputType - the type of the original column
	 * @param <X> - the R primitive type of the column before the mapping
	 * @param <Y> - any R primitive type of the column after the mapping
	 * @param mapping - the operation to apply to the column (must result in an RPrimitive of some type)
	 * @return the same dataframe with a changed column
	 */
	public synchronized <X extends RPrimitive,Y extends RPrimitive> RDataframe mutate(String columnName, Class<X> inputType, Function<X,Y> mapping) {
		return mutate(columnName,mapping);
	}
	
	
	/**
	 * Returns the same data frame with the column "columnName"
	 *
	 * @param columnName -  the name of the column to mutate
	 * @param mapping - the operation to apply to the column (must result in an RPrimitive of some type)
	 * @param <X> - the R primitive type of the column before the mapping
	 * @param <Y> - any R primitive type of the column after the mapping
	 * @return the same dataframe with a changed column
	 */
	@SuppressWarnings("unchecked")
	public synchronized <X extends RPrimitive,Y extends RPrimitive> RDataframe mutate(String columnName, Function<X,Y> mapping) {
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
