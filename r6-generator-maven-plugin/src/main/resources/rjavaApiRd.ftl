% Generated by r6-generator-maven-plugin: do not edit by hand

\name{JavaApi}

\alias{JavaApi}
<#-- \alias{\S4method{get}{JavaApi}} -->

\title{${model.getConfig().getTitle()}}

\section{Usage}{
	\if{html}{\out{<div class="r">}}
	\preformatted{
 J = ${model.getConfig().getPackageName()}::JavaApi$get(logLevel)
 	}
  \if{html}{\out{</div>}}
}

\arguments{
	\if{html}{\out{<div class="arguments">}}
	\item{logLevel}{optional - the slf4j log level as a string - one of OFF (most specific, no logging), 
	FATAL (most specific, little data), ERROR, WARN, INFO, DEBUG, 
	TRACE (least specific, a lot of data), ALL (least specific, all data)}
	\if{html}{\out{</div>}}
}

\description{
${model.getConfig().getDescription()}

Version: ${model.getConfig().getVersion()}

Generated: ${model.getConfig().getDate()}
}

\author{\email{${model.getConfig().getMaintainerEmail()}}}

\keyword{java api}

\section{Static methods and constructors}{
	\itemize{
		\item \code{JavaApi$get()}
<#list model.getClassTypes() as class>
		\item \href{#method-${class.getSimpleName()}-new}{\code{J$${class.getSimpleName()}$new(${class.getConstructor().getParameterCsv()})}}
	<#list class.getStaticMethods() as method>
		\item \href{#method-${class.getSimpleName()}-${method.getName()}}{\code{J$${class.getSimpleName()}$${method.getName()}(${method.getParameterCsv()})}}
	</#list>
</#list>
	}
	

<#list model.getClassTypes() as class>
<#list class.getConstructorsAndStaticMethods() as method>
	\if{html}{\out{<hr>}}
	\if{html}{\out{<a id="method-${class.getSimpleName()}-${method.getName()}"></a>}}
	
	\subsection{Method \code{${class.getSimpleName()}$${method.getName()}()}}{
		${method.getDescription()}
	
		\subsection{Usage}{
			\if{html}{\out{<div class="r">}}
			\preformatted{
J = ${model.getConfig().getPackageName()}::JavaApi$get()
J$${class.getSimpleName()}$${method.getName()}(${method.getParameterCsv()})
<#if !method.isConstructor()>
# this method is also exposed as a package function:
${model.getConfig().getPackageName()}::${method.getSnakeCaseName()}(${method.getParameterCsv()})
</#if>
	  		}
			\if{html}{\out{</div>}}
		}
	
		\subsection{Arguments}{
			\if{html}{\out{<div class="arguments">}}
			\itemize{
	<#if !method.getParameterNames()?has_content>
				\item{none}
	<#else>
		<#list method.getParameterNames() as paramName>
				\item{${method.getParameterDescription(paramName)}}{ - (java expects a ${method.getParameterType(paramName).getSimpleName()})}
		</#list>
	</#if>
			}
			\if{html}{\out{</div>}}
		}

		\subsection{Returns}{
	<#if method.isFactory()>
			R6 ${method.getReturnType().getSimpleName()} object: ${method.getAnnotationValue("return")!}
	<#else>
			${method.getReturnType().getSimpleName()}: ${method.getAnnotationValue("return")!}
	</#if>
		}
	}

</#list>
</#list>
}