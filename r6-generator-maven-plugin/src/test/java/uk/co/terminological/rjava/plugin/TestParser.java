package uk.co.terminological.rjava.plugin;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.text.StringEscapeUtils;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;


public class TestParser {

//	@BeforeAll
//	public final void setUp() throws Exception {
//		
//	}

	@Test
	public final void test() {
		System.out.println(Object[].class.getCanonicalName());
		System.out.println(Object[].class.getComponentType().getCanonicalName());
		ArrayList<String> test = new ArrayList<String>();
		System.out.println(test.getClass().getCanonicalName());
		System.out.println(test.getClass().getTypeParameters()[0].getGenericDeclaration());
	}
	
	@Test
	public final void testRegex() {
		System.out.print(
				StringEscapeUtils.unescapeJava("'hello world'").replaceAll("^\\s*\"|\"\\s*$", "")
				);
	}
	

	@Test
	public final void testSnakeCase() {
		System.out.println(RAnnotated.getSnakeCase("getCamelCaseABCdef"));
		System.out.println(RAnnotated.getSnakeCase("GetCamelCaseABCdef"));
		System.out.println(RAnnotated.getSnakeCase("GetCamel123CaseABCdef"));
		
		System.out.println(RAnnotated.rdEscape("hello % this {needs escaping}"));
	}
	
//	@Test
//	public final void testPaths() {
//		Path path1 = Paths.get("/home/terminological/tmp");
//		Path path2 = Paths.get("/home/terminological/tmp");
//		System.out.println(path1.relativize(path2).toString());
//		System.out.println(path1.relativize(path2).toString().equals(""));
//	}
}
