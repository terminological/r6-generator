package uk.co.terminological.rjava.types;

import static org.junit.jupiter.api.Assertions.*;


// CS01
import uk.co.terminological.rjava.types.*;
import uk.co.terminological.rjava.utils.RFunctions;

import static uk.co.terminological.rjava.RConverter.*;
import static uk.co.terminological.rjava.Rule.MapRule.*;

import uk.co.terminological.rjava.IncompatibleTypeException;
import uk.co.terminological.rjava.NameNotFoundException;
import uk.co.terminological.rjava.RConverter;
import uk.co.terminological.rjava.RName;
// CE01
import uk.co.terminological.rjava.UnconvertableTypeException;
import uk.co.terminological.rjava.ZeroDimensionalArrayException;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.config.Configurator;
import org.apache.logging.log4j.core.config.DefaultConfiguration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


@SuppressWarnings("unused")
class TestDatatypes {

	@BeforeEach
	void setUp() throws Exception {
		Configurator.initialize(new DefaultConfiguration());
	    Configurator.setRootLevel(Level.DEBUG);
	}

	public static enum TestEnum {ONE,TWO,THREE}
	
	
	@Test void testDateBehaviour() {
//		DateTimeFormatter rparser = DateTimeFormatter.ofPattern("yyy-MM-dd G");
//		System.out.println(LocalDate.parse("234-01-01 BC",rparser));
//		
		System.out.println(new RDate("1066-01-01"));
		System.out.println(new RDate("234-01-01"));
//		DateTimeFormatter rparser = DateTimeFormatter.ofPattern("yyy-MM-dd G");
//		value = 
//		if (value.startsWith("-")) {
//			value = value.substring(1)+" BC";
//		} else {
//			value = value.substring(1)+" AD";
//		}
//		self = LocalDate.parse(value,rparser);
//	}
		
		System.out.println(new RDate("-234-01-01"));
		
	}
	
	@Test
	final void testNaBehaviour() {
		
		{
			RInteger i1 = RInteger.from(RInteger.NA_VALUE);
			RInteger i2 = new RInteger();
			RInteger i3 = RInteger.NA;
			assertTrue(i1.equals(i2));
			assertTrue(i1.equals(i3));
			assertTrue(!i1.equals(RPrimitive.of(123)));
		}
		
		{
			RLogical i1 = RLogical.NA;
			RLogical i2 = new RLogical();
			RLogical i3 = RLogical.from(RLogical.NA_VALUE);
			assertTrue(i1.equals(i2));
			assertTrue(i1.equals(i3));
			assertTrue(!i1.equals(RPrimitive.of(false)));
		}
		
		{
			RNumeric i1 = RNumeric.NA;
			RNumeric i2 = new RNumeric();
			RNumeric i3 = RNumeric.from(RNumeric.NA_VALUE);
			assertTrue(i1.equals(i2));
			assertTrue(i1.equals(i3));
			assertTrue(!i1.equals(RPrimitive.of(2.0)));
			RNumeric nan = RPrimitive.of(Double.NaN);
			assertTrue(i1.isNa());
			assertTrue(!i1.equals(nan));
			
		}
		
		{
			RDate i1 = RDate.NA;
			RDate i2 = new RDate();
			RDate i3 = RDate.from(RDate.NA_VALUE);
			assertTrue(i1.equals(i2));
			assertTrue(i1.equals(i3));
			assertTrue(!i1.equals(RPrimitive.of(LocalDate.now())));
		}
		
		
		
		{
			RFactor i1 = RFactor.NA;
			RFactor i2 = new RFactor();
			RFactor i3 = RFactor.from(null);
			assertTrue(i1.equals(i2));
			assertTrue(i1.equals(i3));
			assertTrue(!i1.equals(RFactor.from(TestEnum.THREE)));
		}
		
		{
			RCharacter i1 = RCharacter.NA;
			RCharacter i2 = new RCharacter();
			RCharacter i3 = RCharacter.from(null);
			assertTrue(i1.equals(i2));
			assertTrue(i1.equals(i3));
			assertTrue(!i1.equals(RCharacter.from("Something")));
		}
		
	}
	
	@Test
	final void testIntegerVectors() {
		
		// CS02
		
		// Java boxed arrays can be directly converted to vector from RVector
		// Integer.MIN_VALUE inputs are converted to nulls silently
		Integer[] tmp = {1,3,5,7,null,Integer.MIN_VALUE, RInteger.NA_VALUE, 0};
		RIntegerVector col0 = RVector.with(tmp);
		RIntegerVector col1 = convert(tmp);
		
		assertTrue(col1.get(4).isNa());
		assertTrue(col1.get(5).isNa());
		assertTrue(col1.get(6).isNa());
		
		assertTrue(!col1.get(4).opt().isPresent());
		
		// Alternatively RConverter has a set of collectors for Streams of convertible types.
		RIntegerVector col2 = Stream.of(tmp).collect(integerCollector());
		assertTrue(col1.equals(col0));
		assertTrue(col1.equals(col2));
		
		// Conversion using a CollectionConverter to convert arbitrary collection types
		RIntegerVector col3 = using(integerCollector()).convert(tmp);
		RIntegerVector col4 = using(integerCollector()).convert(Arrays.asList(tmp));
		RIntegerVector col5 = using(integerCollector()).convert(Arrays.asList(tmp).iterator());
		RIntegerVector col6 = using(integerCollector()).convert(Stream.of(tmp));
		// These should all be the same		
		assertTrue(col1.equals(col3));
		assertTrue(col1.equals(col4));
		assertTrue(col1.equals(col5));
		assertTrue(col1.equals(col6));
				
		// But Converting collectors can also handle singleton conversions
		RIntegerVector i1 = using(integerCollector()).convert(tmp[0]);
		assertTrue(i1.get(0) instanceof RInteger);
		assertTrue(col1.get(0).equals(i1.get(0)));
						
		// Primitive arrays are slightly less flexible but RConverter can process them
		// nulls are not allowed but the "equivalent" to NA is Integer.MIN_VALUE = RInteger.NA_INT
		// If they are converted to a boxed form by an IntStream they can be collected as above
		int[] tmp2 = {1,3,5,7,RInteger.NA_VALUE, Integer.MIN_VALUE, RInteger.NA_VALUE, 0};
		RIntegerVector col7 = convert(tmp2);
		RIntegerVector col8 = IntStream.of(tmp2).boxed().collect(integerCollector());
		assertTrue(col1.equals(col7));
		assertTrue(col1.equals(col8));
		
		// CE02
	}

	@Test
	final void testOtherCollectors() {
		
		// CS03
		
		//Dates - difficulty is getting a stream of maybe null dates
		//Collect streams of other data types:
		RDateVector rdv = Stream
				.of("2020-03-01","2020-03-02","2020-03-03",null,"2020-03-05")
				.collect(dateFromStringCollector());
		RLogicalVector rlv1 = rdv.stream().map(d -> d.isNa()).collect(booleanCollector());
		//or use RConverter.convert on arrays
		boolean[] bools = {false,false,false,true,false};
		boolean[] bools2 = {false,false,false,true,true};
		assertTrue(rlv1.equals(convert(bools)));
		assertTrue(!rlv1.equals(convert(bools2)));
		
		// CE03
	}
	
	@Test
	final void testUnwrapRObject() {
		
		// CS04
		RDate date = RDate.from("2020-02-02");
		assertTrue(date.opt(LocalDate.class).isPresent());
		assertTrue(date.get().toString().equals("2020-02-02"));
		
		// RPrimitive.as(Class<?>) returns an optional. NA or incorrect types both come out as Optional.empty()
		RNumeric num = RNumeric.from(123.456);
		assertTrue(num.opt(BigDecimal.class).isPresent());
		assertTrue(num.opt(Float.class).isPresent());
		assertTrue(num.opt(Long.class).isPresent());
		
		// Will return an Optional.empty() as not a String
		assertTrue(!num.opt(String.class).isPresent());
		
		// RPrimitive.get() will return the main underlying implementation type (in this case a Double) or a null for a NA
		assertTrue(num.get().doubleValue() == 123.456);
		assertTrue(num.get(Float.class).equals(123.456F));
		
		// NA values are returned as Optional.empty() by RPrimitive.opt()
		assertTrue(!RInteger.NA.opt().isPresent());
		assertTrue(RInteger.from(234).opt().isPresent());
		
		// CE04
		
	}
	
	@Test
	final void testDataframe() {
		//Use a stream + dataframe collector to generate data frame:
		
		Arrays.asList("Hello","World","Stream","Support","in","Java")
		.stream()
		.collect(dataframeCollector(
			mapping("original", s-> s),
			mapping("lowercase", s-> s.toLowerCase()),
			mapping("uppercase", s-> s.toUpperCase()),
			mapping("subst", s-> s.substring(0,Math.min(3,s.length()))),
			mapping("length", s-> s.length())
		));
	}
	
	static RDataframe getDiamonds() throws IOException {
		InputStream is = TestDatatypes.class.getResourceAsStream("/diamonds.ser");
		if(is==null) throw new IOException("Could not locate /diamonds.ser");
		return RObject.readRDS(RDataframe.class, is);
	}
	
	
	static interface Diamonds {
		@RName("carat") public RNumeric getCarats();
		@RName("cut") public RFactor getCuts();
		public RInteger price(); //doesn't have to be named if 
		public default void print() {
			System.out.println("price: "+this.price()+"; carats: "+this.getCarats() + "; cut: "+this.getCuts());
		}
	}
	
	@Test
	final void testDataframeCoercion() throws IOException, UnconvertableTypeException {
		RDataframe dia = getDiamonds();
		//Test object binding and default interface methods:
		dia.stream(Diamonds.class).limit(10).forEach(Diamonds::print);
		
		System.out.println(""+dia.pull("price",RIntegerVector.class).get().collect(Collectors.averagingDouble(x -> (double) x)));
		
		dia.attach(Diamonds.class).getCoercedRow(100).getCuts().get();
		dia.attach(Diamonds.class).getRow(100).lag().coerce().getCuts().get();
	}
	
	@Test void testNumericArray() throws ZeroDimensionalArrayException {
		RNumericArray tmp = new RNumericArray(testNumeric(), new int[] {3,5});
		assertEquals(3, tmp.get(1).dimensions[0]);
		RNumericArray tmp2 = new RNumericArray(testNumeric(), new int[] {5,3});
		assertEquals(5, tmp2.get(1).getVector().size());
	}
	
	static RNumericVector testNumeric() {
		return RVector.with(0.1,0.2,0.3,0.4,0.5,1.1,1.2,1.3,1.4,1.5);
	}
	
	static RDataframe testData() {
		return RDataframe.create()
			.withCol("group", RVector.with("One","One","One","One","One","Two","Two","Two","Two","Two"))
			.withCol("value", testNumeric());
	}
	
	@Test void testPull() {
		assertTrue(testData().pull("value", RNumericVector.class).equals(testNumeric()));
		assertThrows(NameNotFoundException.class, () -> testData().pull("unknown", RNumericVector.class));
		assertThrows(NameNotFoundException.class, () -> testData().pull("unknown", RIntegerVector.class));
		assertThrows(IncompatibleTypeException.class, () -> testData().pull("value", RIntegerVector.class));
	}
	
	@Test void testRFunctions() {
		assertTrue(!RFunctions.any(RFunctions::isNa, testNumeric()));
		assertTrue(RFunctions.all(RFunctions::isFinite, testNumeric()));
		
	}
	
	@Test void testSelectAndDrop() {
		assertTrue(testData().select("value").equals(RDataframe.create().withCol("value", testNumeric())));
		assertTrue(testData().select("unknown").equals(RDataframe.create()));
		assertTrue(testData().select().equals(RDataframe.create()));
		assertTrue(testData().select("group","value").equals(testData()));
		assertTrue(testData().drop().equals(testData()));
		assertTrue(testData().drop("unknown").equals(testData()));
		assertTrue(testData().drop("group","value").equals(RDataframe.create()));
		assertTrue(testData().select("value").equals(testData().drop("group")));
		
		assertTrue(testData().groupBy("group").select("value").equals(testData()));
		assertTrue(testData().groupBy("group").drop("group").equals(testData()));
		
	}
	
	@Test void testRename() {
		assertTrue(testData().rename("value2", "value").pull("value2").equals(testData().pull("value")));
	}
	
	@Test
	final void testDataframeGroupBy() throws IOException, UnconvertableTypeException {
		
		RDataframe test = testData();
		RDataframe test2 = test.groupBy("unknown","group");
		System.out.println(Arrays.toString(test2.getGroups()));
		assertTrue(test2.getGroups()[0] == "group");
		//assertTrue(test.getGroups() == new String[] {"group"});
		
		RDataframe testSummary = test2.groupModify((d,g) -> {
			Double mean = d.pull("value",RNumericVector.class).get().collect(Collectors.averagingDouble(x -> x));
			return RDataframe.create().withCol("mean value",RConverter.convert(mean)); 
		});
		
		System.out.print(testSummary);
			
	}
	
	@Test
	final void testDiamondsGroupBy() throws IOException, UnconvertableTypeException {
		RDataframe dia = getDiamonds();
		RDataframe diaSummary = dia.groupBy("cut","color","carat").groupModify((d,g) -> {
			Double mean = d.pull("price",RIntegerVector.class).get().collect(Collectors.averagingDouble(x -> (double) x));
			return RDataframe.create().withCol("mean price",RConverter.convert(mean)); 
		});
		System.out.println(diaSummary.nrow());
		System.out.print(dia.count().filter("n",RInteger.class, n -> n.get() > 1));		
	}
	
	public static void main(String[] args) throws IOException, UnconvertableTypeException {
		{
		System.out.print("Press a key");
		int i= -1;
		while( i == -1)
			i = System.in.read();
		}
		RDataframe dia = getDiamonds();
		long time = System.currentTimeMillis();
		for (int j=0; j<1000; j++) {
			
			RDataframe diaSummary = dia.groupBy("cut","color","carat").groupModify((d,g) -> {
				Double mean = d.pull("price",RIntegerVector.class).get().collect(Collectors.averagingDouble(x -> (double) x));
				return RDataframe.create().withCol("mean price",RConverter.convert(mean)); 
			});
			System.out.println(diaSummary.nrow());
			//System.out.println(diaSummary);
			
		}
		System.out.println("Took for 1000 (ms): "+(System.currentTimeMillis()-time));
		{
		System.out.print("Press a key");
		int i= -1;
		while( i == -1)
			i = System.in.read();
		}
	}
	
}
