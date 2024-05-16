package uk.co.terminological.rjava.plugin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.StringEscapeUtils;

public class RMethod extends RAnnotated {
			
	private String methodName;
	private LinkedHashMap<String,RType> parameters = new LinkedHashMap<>();
	private LinkedHashMap<String,String> defaults = new LinkedHashMap<>();
	private RType returnType;
	private RClass definingClass;
	private boolean isStatic;
	private boolean isConstructor;
	private boolean async;
	private boolean future;
	private String description;
	
	public RMethod(RModel model, RClass definingClass, Map<String,Object> annotations, String mname, String description, boolean isStatic, boolean isConstructor, boolean async, boolean future) {
		super(model, annotations, null, mname);
		this.definingClass = definingClass;
		this.methodName = mname;
		this.description = description;
		this.isStatic = isStatic;
		this.isConstructor = isConstructor;
		this.async = async;
		this.future = future;
	}
	
	public void setReturnType(RType returnType) {this.returnType = returnType;}
	public void addParameter(String name, RType parameterType, String defaultExpr) {
		parameters.put(name, parameterType);
		defaults.put(name, defaultExpr);
	}
	
	public String getSnakeCaseName() {
		if (this.getModel().detectMethodCollision(this.simpleName)) {
			// TODO: have switched order to function.class name. Needs testing and vignetted updated.
			// return getSnakeCase(this.definingClass.simpleName)+"_"+getSnakeCase(this.simpleName);
			return getSnakeCase(this.simpleName)+"."+getSnakeCase(this.definingClass.simpleName);
		} else {
			return getSnakeCase(this.simpleName);
		}
	}
	
	public RType getReturnType() {
		return returnType;
	}
	public String getName() {
		return methodName;
	}
	public List<String> getParameterNames() {
		return new ArrayList<String>(parameters.keySet());
	}
	public List<RType> getParameterTypes() {
		return new ArrayList<RType>(parameters.values());
	}
	
	public boolean isFactory() {
		return this.getModel().getClassTypes().stream()
				.anyMatch(s2 -> s2.getCanonicalName().equals(this.getReturnType().getCanonicalName()));
	}
	public String getDescription() {
		return description == null || description.isEmpty() ? "no title" : description.replaceAll("<br/?>|<BR/?>", "\n");
	}
	public String getTitle() {
		return getDescription().split("\n")[0];
	}
	public String getNonTitleDescription() {
		String s = Arrays.asList(getDescription().split("\n")).stream().skip(1).collect(Collectors.joining("\n")).trim();
		if (s == null| s.isEmpty()) return "no description";
		return s;
	}
	public String getParameterDescription(String paramName) {
		if (this.getAnnotations().get("param") == null) return paramName;
		String tmp = this.getAnnotations().get("param").stream()
			.filter(val -> val.trim().startsWith(paramName))
			.collect(Collectors.joining());
		String defaultValue = defaults.get(paramName);
		if (defaultValue != null) {
			defaultValue = defaultValue.replaceAll("^\\s*\"|\"\\s*$", "").replaceAll("\\\\", "\\\\\\\\");
			if (defaultValue.length() > 40) defaultValue = defaultValue.substring(0,37)+"...";
			defaultValue = " - (defaulting to `"+defaultValue+"`)"; 
			return tmp.isEmpty() ? paramName+defaultValue : tmp+defaultValue;
		} else {
			return tmp.isEmpty() ? paramName : tmp;
		}
	}
	public RType getParameterType(String paramName) {
		return parameters.get(paramName);
	}
	public boolean isStatic() {
		return isStatic;
	}
	public boolean isAsync() {
		return async;
	}
	public boolean isFuture() {
		return future;
	}
	
	public boolean hasExamples() {
		return (
			!this.getAnnotationList("examples").isEmpty() ||
			hasTests()
		);
	}
	public boolean hasTests() {
		return !this.getAnnotationList("tests").isEmpty();
	}
	public List<String> getExamples() {
		if (this.getAnnotationList("examples").isEmpty()) {
			List<String> out = new ArrayList<String>();
			out.add("library(testthat)");
			out.addAll(getTests());
			return out;
		}
		return this.getAnnotationList("examples");
	}
	public List<String> getTests() {
		return this.getAnnotationList("tests");
	}
	
	public boolean isConstructor() {
		return isConstructor;
	}
	
	public String getParameterCsv() {
		return getParameterCsv(", ");
	}
	public String getParameterCsv(String sep) {
		return getParameterNames().stream().collect(Collectors.joining(sep));
	}
	public String getPrefixedParameterCsv(String pre) {
		if (this.parameters.size() == 0) return "";
		return ", "+getParameterNames().stream().map(s->pre+s).collect(Collectors.joining(", "));
	}
	public String getPrefixedParameterCsv(String pre, int tabs) {
		if (this.parameters.size() == 0) return "";
		String sep=",\n"+(StringUtils.repeat("\t", tabs));
		return sep+getParameterNames().stream().map(s->pre+s).collect(Collectors.joining(sep));
	}
	public String getFunctionParameterCsv() {
		return getFunctionParameterCsv(", ");
	}
	public String getFunctionParameterCsv(String sep) {
		return defaults.entrySet().stream()
				.map(kv -> {
					String tmp = kv.getKey();
					if (kv.getValue() != null) {
						tmp = tmp + "="+
								StringEscapeUtils.unescapeJava(kv.getValue()).replaceAll("^\\s*\"|\"\\s*$", "");
					}
					return tmp;		
				})
				.collect(Collectors.joining(sep));
	}
	
}