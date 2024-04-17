package uk.co.terminological.rjava.plugin;

import java.util.ArrayList;
import org.apache.commons.text.StringEscapeUtils;
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
	
	@Test
	public final void testLongString() {
		String s = "## this is a long %>% string more than 100 characters %>% breaking just before here is ok as %>% will be about the right length + maybe here also will trigger it";
		if (s.startsWith("#'")) {
			s = s.replaceAll("(.{50})(%>%|,|\\+)", "$1$2\n#'");
		} else if (s.startsWith("#")) {
			s = s.replaceAll("(.{50})(%>%|,|\\+)", "$1$2\n#");
		} else {
			s = s.replaceAll("(.{50})(%>%|,|\\+)", "$1$2\n");
		}
		System.out.print(s);
		
	}
}
