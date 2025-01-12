package uk.co.terminological.rjava.plugin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.commons.text.WordUtils;

import com.thoughtworks.qdox.model.JavaClass;
import com.thoughtworks.qdox.model.JavaType;

public class RAnnotated {
	
	private RModel model;
	private Map<String,List<String>> annotations = new HashMap<>();
	protected JavaType type;
	protected String simpleName;
	
	public Map<String,List<String>> getAnnotations() {
		return annotations;
	}
	
	public RAnnotated(RModel model, Map<String,Object> annotation, JavaType type, String name) {
		this.model=model;
		this.type=type;
		this.simpleName= name;
		annotation.forEach((key,value) -> this.mergeAnnotations(key, value));
	}
	
	public RModel getModel() {return model;}
	
	
	public String getAnnotationValue(String s) {
		return getAnnotationList(s).stream().collect(Collectors.joining("\n"));
	}
	
	// Only string annotations are supported here.
	// In general an annotation is a list of strings.
	public List<String> getAnnotationList(String s) {
		return annotations.get(s) == null ? 
			Collections.emptyList() : 
				annotations.get(s).stream()
				.map(RAnnotated::stripQuotes)
				.map(s2 -> s2.trim())
				.filter(s2 -> !s2.isEmpty())
				.collect(Collectors.toList()) ;
	}
	
	public void mergeAnnotations(String key, Object value) {
		List<String> tmp;
		if (value instanceof Collection<?>) {
			tmp = ((Collection<?>) value).stream().map(o -> o.toString()).collect(Collectors.toList());
		} else if (value instanceof String[]) {
			tmp = Arrays.asList((String[]) value);
		} else {
			tmp = Collections.singletonList(value.toString());
		} 
		getAnnotations().merge(
			key, tmp, 
			(oldV,newV) -> {
				List<String> out = new ArrayList<>(oldV);
				out.addAll(newV);
				return out;
			}
		);
	}
	
	private Optional<JavaClass> asJavaClass() {
		if (type instanceof JavaClass) return Optional.of((JavaClass) type);
		return Optional.empty();
	}

	public boolean isA(Class<?> clazz) {
		Optional<JavaClass> tmp = this.getModel().javaClassFor(clazz);
		if (!tmp.isPresent()) return false;
		if (!asJavaClass().isPresent()) return false;
		return tmp.get().equals(asJavaClass().get());
	}

	protected static String getSnakeCase(String s) {
		Pattern p = Pattern.compile("([a-z0-9])([A-Z])");
		Matcher m =p.matcher(s);
		StringBuffer sb = new StringBuffer();
	    while(m.find()) {
	    	m.appendReplacement(sb, m.group(1)+"_"+m.group(2).toLowerCase());
	    }
	    m.appendTail(sb);
	    String out = sb.toString();
	    if (p.matcher(out).find()) out = getSnakeCase(out);
	    return out.replaceAll("([a-z])([0-9])", "$1_$2").toLowerCase();
	}
	
	
	public String getSnakeCaseName() {
		return getSnakeCase(this.simpleName);
	}
	
	public String getSimpleName() {
		return this.simpleName;
	}
	
	public String getCanonicalName() {
		return this.type.getCanonicalName();
	}
	
	//this is the JNI name without "L..;" wrapper
	public String getJNIName() {
		//return "L"+this.getCanonicalName().replace(".", "/")+";";
		return this.getCanonicalName().replace(".", "/");
	}

	private static String stripQuotes(String s) {
		return s.replace("\"", "");
	}
	
	public int hashCode() {return type.hashCode();}
	public boolean equals(Object other) {
		if (other == null) return false;
		if (!(other instanceof RAnnotated)) return false;
		return (((RAnnotated) other).type.equals(type));
	}
	
	public static String rdEscape(String s) {
		if(s == null) return null;
		return s.replaceAll("%","\\\\%").replaceAll("\\{","\\\\{").replaceAll("\\}","\\\\}");
	}
	
	public static String rdEscapeExample(String s) {
		if(s == null) return null;
		// Make sure this does not end up too long
		if (s.startsWith("#'")) {
			s = s.replaceAll("(.{50})(%>%|,|\\+)", "$1$2\n#'");
		} else if (s.startsWith("#")) {
			s = s.replaceAll("(.{50})(%>%|,|\\+)", "$1$2\n#");
		} else {
			s = s.replaceAll("(.{50})(%>%|,|\\+)", "$1$2\n");
		}
		return s.replaceAll("%","\\\\%");
	}
	
	public String indent(String s, int indent) {
		if(s == null) return null;
		String tabs = String.join("", Collections.nCopies(indent, "\t"));
		return tabs+s.trim().replaceAll("\\n", "\n"+tabs).trim();
	}
	
	public String doxygen(String s) { return doxygen(s,1); }
	public String doxygen(String s1, String s2) { return doxygen(s1,s2,1); }
	
	public String doxygen(String s, int indent) {
		if(s == null) return null;
		s = WordUtils.wrap(s, 70);
		String tabs = String.join("", Collections.nCopies(indent, "\t"));
		return tabs+"#' "+s.trim().replaceAll("\\n", "\n"+tabs+"#'   ").trim();
	}
	public String doxygen(String field, String s, int indent) {
		if(s == null) return null;
		s = WordUtils.wrap(s, 70);
		String tabs = String.join("", Collections.nCopies(indent, "\t"));
		return tabs+"#' @"+field+" "+s.trim().replaceAll("\\n", "\n"+tabs+"#'   ").trim();
	}
}