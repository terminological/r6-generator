% Generated by r6-generator-maven-plugin: do not edit by hand

\name{${class.getSimpleName()}}

\alias{${class.getSimpleName()}}
<#-- \alias{\S4method{new}{${class.getSimpleName()}}} -->

\title{${class.getTitle()}}

<#-- 
\usage{
<#assign method=class.getConstructor()>
\S4method{new}{${class.getSimpleName()}}(${method.getParameterCsv()})
}
 -->
<#assign method=class.getConstructor()>
<#if method.getParameterNames()?has_content>
\arguments{
	\if{html}{\out{<div class="arguments">}}
	<#list method.getParameterNames() as paramName>
	\item{${paramName}}{${method.getParameterDescription(paramName)} - (java expects a ${method.getParameterType(paramName).getSimpleName()})}
	</#list>
	\if{html}{\out{</div>}}
}
</#if>


\description{
${class.getDescription()!}

Version: ${model.getConfig().getVersion()}

Generated: ${model.getConfig().getDate()}
}

\details{
	${class.getDetails()!}
}

\examples{\dontrun{
J = ${model.getConfig().getPackageName()}::JavaApi$get();
<#assign method=class.getConstructor()>
instance = J$${class.getSimpleName()}$${method.getName()}(${method.getParameterCsv()})

<#list class.getMethods() as method>
## -----------------------------------
## Method `${class.getSimpleName()}$${method.getName()}`
## -----------------------------------
	<#list method.getAnnotationList("examples") as example>
${example}
	</#list>

</#list>
}}

\keyword{java api}

\section{Methods}{
	\subsection{Constructors}{
		\itemize{
<#assign method=class.getConstructor()>
			\item \href{#method-${method.getName()}}{\code{J$${class.getSimpleName()}$${method.getName()}(${method.getParameterCsv()})}}
		}
	}
	\subsection{Static methods}{
		\itemize{
<#if !class.getStaticMethods()?has_content>
			\item{none}
<#else>
	<#list class.getStaticMethods() as method>
			\item \href{#method-${method.getName()}}{\code{J$${class.getSimpleName()}$${method.getName()}(${method.getParameterCsv()})}}
	</#list>
</#if>
		}
	}
	\subsection{Instance methods}{
		\itemize{
<#if !class.getInstanceMethods()?has_content>
			\item{none}
<#else>
	<#list class.getInstanceMethods() as method>
			\item \href{#method-${method.getName()}}{\code{instance$${method.getName()}(${method.getParameterCsv()})}}
	</#list>
</#if>
			\item \code{instance$clone()}
			\item \code{instance$print()}
		}
	}

<#list class.getConstructorAndMethods() as method>
	\if{html}{\out{<hr>}}
	\if{html}{\out{<a id="method-${method.getName()}"></a>}}
	
	\subsection{Method \code{${method.getName()}()}}{
		${method.getDescription()}
	

		\subsection{Usage}{
			\if{html}{\out{<div class="r">}}
			\preformatted{
	<#if method.isStatic()>
J = ${model.getConfig().getPackageName()}::JavaApi$get()
J$${class.getSimpleName()}$${method.getName()}(${method.getParameterCsv()})
	<#else>
J = ${model.getConfig().getPackageName()}::JavaApi$get()
instance = J$${class.getSimpleName()}$new(...);
instance$${method.getName()}(${method.getParameterCsv()})
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

		\subsection{Examples}{
			\if{html}{\out{<div class="r example copy">}}
			\preformatted{
	<#if !method.getAnnotationList("examples")?has_content>
not available
	<#else>
		<#list method.getAnnotationList("examples") as example>
${example}
		</#list>
	</#if>
			}
			\if{html}{\out{</div>}}
		}
	}
</#list>
}