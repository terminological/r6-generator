# Generated by roxygen2: do not edit by hand
# But actually generated by r6-generator-maven-plugin
export(JavaApi)
export("%>%")
<#list model.getExports() as export>
export(${export})
</#list>
<#list model.getClassTypes() as class>
	<#list class.getStaticMethods() as method>
export(${method.getSnakeCaseName()})
	</#list>
</#list>

<#-- imports in description but not in namespace. There should be no reason to import these into the namespace. 
<#list model.getImports() as import>
import(${import})
</#list>
-->
importFrom(magrittr,"%>%")
import(rJava)
