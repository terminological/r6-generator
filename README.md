# r6-generator

[![DOI](https://zenodo.org/badge/503675575.svg)](https://zenodo.org/badge/latestdoi/503675575)

This is the main development repository for the r6-generator. 

It is a multi-module Maven project containing a code generation framework allowing Java libraries to be used in R.

Beyond this README, the best source of documentation is in the example [documenation project](https://github.com/terminological/r6-generator-docs).

## Rationale

R can use `rJava` or `jsr223` to communicate with Java. R also has a class system called `R6`.

If you want to use a Java library with native `rJava` or `jsr223` in R there is potentially a lot of glue code and R library specific packaging configuration required.

However if you don't mind writing an R-centric API in Java you can generate all of this glue code using a few Java annotations and the normal javadoc comments. This 
project provides an annotation processor, and code generator that writes that glue code and creates a fairly transparent connection between Java code and R code with a minimum of hard work. This is supported by a robust R package infrastructure which makes R package creation much simpler. 

The focus of this is streamlining the creation and maintenance of R libraries by Java developers, rather than allowing access to arbitrary Java code from R. The ultimate aim of this plugin to allow Java developers to provide a simple API for a Java library, package it using Java's standard dependency management system Maven, push it to github, invoke continuous integration frameworks using Github workflows, and for the Java code to become seamlessly available as an R package, with a minimal amount of fuss. We try to generate robust R packages directly from Java code which are ready for CRAN submission.

The project has 3 components:

* `r6-generator-runtime`: a pure Java library which manages data manipulation and marshalling to and from R. This supports the main R datatypes and provides Java versions of these, plus converter methods to ease Java development.
* `r6-generator-maven-plugin`: a Maven plugin which uses native the R-centric Java API to generates R and `rJava` code that handles the mechanics of installing Java libraries into R, calling Java code from R, and marshalling data across the R-Java interface ni a lossless manner. 
* `r6-generator-maven-archetype`: a Maven archetype which creates a basic framework with recommended configuration for projects using the Maven plugin.  

## r6-generator projects in the wild

r6-generator is in use in a number of in-development R-packages.

* [html2pdfr](https://github.com/terminological/html2pdfr) - generates pdfs and images from html files using the OpenHtml2PDF java library.
* [roogledocs](https://github.com/terminological/roogledocs) - programatic access to google docs using the Java google docs API client.
* [jepidemic](https://github.com/terminological/jepidemic) - tools to calculate epidemic growth rates and reproduction numbers.

Please let me know if you are using the project and happy for me to add it to this list.

## r6-generator-runtime

The runtime dependency for r6-generator projects contains Java annotations, R like data-structures, and the Java utilities for mapping to and from R-like data structures to native Java

These necessary annotations and data structures are required for the Java developer who is trying to create an R library using the `r6-generator-maven-plugin`, and is a required dependency of such a project. 

The main imports required in r6-generator projects are: 

```Java
import uk.co.terminological.rjava.RClass;
import uk.co.terminological.rjava.RMethod;
import uk.co.terminological.rjava.types.*;
import static uk.co.terminological.rjava.RConverter.*;
```

The annotations allow you to specify an R-centric API for using your Java code like so:

```Java
package com.example.hello;

import uk.co.terminological.rjava.RClass;
import uk.co.terminological.rjava.RMethod;

/**
 * A test of the r6 templating
 * 
 * this is a details comment 
 * @author joe tester joe.tester@example.com ORCIDID
 * 
 */
@RClass
public class HelloWorld {

	/**
	 * Description of a hello world function
	 * @return this Java method returns a String
	 */
	@RMethod(examples = {
		"An example",
		"Spans many lines"
	})
	public static String greet() {
		return "Hello R world. Love from Java."
	}
}
```

Key points:

* You can annotate multiple classes with `@RClass`.
* Only public methods annotated with `@RMethod` will feature in the R library.
* you cannot overload methods or constructors. Only one method with a given name is supported, and only one annotated constructor.
* methods can return anything from `uk.co.terminological.rjava.types.*`, basic Java primitives or Java Strings or any class that is part of this API and annotated with `@RClass`. 
* static and non static Java methods are supported.

### Datatype conversion

The runtime component of this project contains tools to enable bidirectional lossless transfer of R data structures into Java (`uk.co.terminological.rjava.RConverter`), 
in a idiom that is relatively comfortable to Java developers, without having to manage the edge cases posed by R's interchangeable use of single values, vectors, and complexities 
around `NA` values and different flavours of null. This is a big topic and further documented in the [documenation project](https://github.com/terminological/r6-generator-docs).

## r6-generator-maven-plugin

This is the main Maven plugin. It contains a code generator which writes glue code and R package configuration files to allow correctly annotated Java class to be used within R as an set of R6 classes.

Using the example class above we need to provide R package metadata and code generator configuration to generate an R package to support this Java API. This is done through the Maven build configuration `pom.xml`:   

The required `r6-generator-runtime` dependency provides datatypes, annotations, and support for surfacing Java logging on the R console.

```XML
...
	<properties>
		...
		<r6.version>0.5.0</r6.version>
	</properties>
...
	<dependencies>
		<dependency>
			<groupId>io.github.terminological</groupId>
			<artifactId>r6-generator-runtime</artifactId>
			<version>${r6.version}</version>
		</dependency>
...
	</dependencies>
...
```
The stable versions of the Maven plugin are available on Maven Central. If we are using the development version `main-SNAPSHOT` we will need to enable the snapshot repositories: 

```XML

...
	<!-- Resolve runtime library -->
	<repositories>
		<repository>
		    <id>Sonatype OSSRH</id>
		    <url>https://s01.oss.sonatype.org/content/repositories/snapshots/</url>
		</repository>
	</repositories>
...
	<!-- Resolve Maven plugin -->
	<pluginRepositories>
		<pluginRepository>
		    <id>Sonatype OSSRH</id>
		    <url>https://s01.oss.sonatype.org/content/repositories/snapshots/</url>
		</pluginRepository>
	</pluginRepositories>
...
```

Maven plugin example configuration - this particular example generates the R package into the directory above `pom.xml` in the source tree. Other options are available and discussed in the [documenation project](https://github.com/terminological/r6-generator-docs).

```XML
...
	<build>
		<plugins>
			...
			<plugin>
				<groupId>io.github.terminological</groupId>
				<artifactId>r6-generator-maven-plugin</artifactId>
				<version>${r6.version}</version>
				<configuration>
					<outputDirectory>${project.basedir}/..</outputDirectory>
					<packageData>
						<title>A test library</title>
						<version>0.1.0.9000</version>
						<packageName>myRpackage</packageName>
						<license>MIT</license>
						<description>A long description of the package which does not start with "This package".</description>
						<maintainerName>test forename</maintainerName>
						<maintainerFamilyName>optional surname</maintainerFamilyName>
						<maintainerEmail>test@example.com</maintainerEmail>
					</packageData>
				</configuration>
				<executions>
					<execution>
						<id>clean-r-library</id>
						<goals>
							<goal>clean-r-library</goal>
						</goals>
					</execution>
					<execution>
						<id>flatten-pom</id>
						<goals>
							<goal>flatten-pom</goal>
						</goals>
					</execution>
					<execution>
						<id>generate-r-library</id>
						<goals>
							<goal>generate-r-library</goal>
						</goals>
					</execution>
				</executions>
			</plugin>
		</plugins>
	</build>
...
```

And with this `pom.xml` in place, a call to `mvn install` will create your R package by adding files to the specified output directory.

### run it from R:

Once the R package is built it can be committed to github (where it will run a github actions workflow to test it can be installed), and where it can 
be picked up and installed by others. Alternatively it can be used locally on your development machine.

```R

# if you are using locally:
devtools::load_all("~/Git/your-project-id")
# OR if you pushed the project to github
# devtools::install_github("your-github-name/your-project-id")

# a basic smoke test

# the JavaApi class is the entry point for R to your Java code.
J = myRpackage::JavaApi$get()

# all the API classes and static methods are attached to the J Java api object
J$HelloWorld$greet()

# everything is automatically documented using the javadoc comments,
?myRpackage::HelloWorld
```

## r6-generator-maven-archetype

This step-by-step is easier to follow with a example starting point. The `r6-generator-maven-archetype` helps set-up a default r6-generator project in the recommended configuration.

The archetype needs the following information, most of which can be entered interactively:

Necessary:

* githubOrganisation - (or your github user name)
* githubRepository - the name you are going to use for the github repo for this project.

Optional:

* package - the Java package, optional, defaults to io.github.${githubOrganisation}
* rPackageName - optional, defaults to ${githubRepository}
* versionId - optional, defaults to main-SNAPSHOT
* rPackageVersion - optional, defaults to 0.0.0.9000
* rPackageLicense - optional, defaults to MIT
* maintainerName - optional, defaults to an example name
* maintainerSurname - ditto
* maintainerEmail - ditto
* maintainerOrganisation - optional, defaults to same as githubOrganisation.
* libraryStyle - optional, one of fat-jar, thin-jar, compile-source (defaults to fat-jar)

Usage:

```BASH
cd ~/Git
mvn archetype:generate \
  -DarchetypeGroupId=io.github.terminological \
  -DarchetypeArtifactId=r6-generator-maven-archetype \
  -DarchetypeVersion=0.5.0 \
  -DgithubOrganisation=exampleOrganisation \
  -DgithubRepository=examplePackage
```

With the organisation and repository I suggest not using names with hyphens for the repository as they cannot be used as R package names.
The use of camel-case is probably also not ideal.

In general the rest of the default values can be accepted.

The newly created Java project will be in the `examplePackage` subdirectory. There is a `README.md` file there which details the next steps.

To generate the R-package from the Java library it needs to be installed using maven. The Java component of the library is in the `java`
subdirectory.


```BASH
cd ~/Git/examplePackage/java
mvn install
```

The resulting generated R-package can be pushed to github where it should trigger appropriate workflows if the organisation and repository names given above match.

```BASH
cd ~/Git/examplePackage
git init
git checkout -b main
git add . 
git commit -m "initial commit"
gh repo create exampleOrganisation/examplePackage --source=. --public
git push --set-upstream origin main

```

Once basic setup complete more background on developing your package is available in the [documentation project](https://github.com/terminological/r6-generator-docs).

## Citing r6-generator

If you are building a R-package using r6-generator, your package will automatically cite r6-generator. This behaviour can be modified by editing `inst/CITATION`.

Here is the reference it uses:

```R
bibentry(bibtype = "Manual",
		title = "R6 generator maven plugin",
		author = person(given="Rob", family="Challen",role="aut",email="rc538@exeter.ac.uk",comment = structure("0000-0002-5504-7768", .Names = "ORCID")),
		note = "Maven plugin artifact: io.github.terminological:r6-generator-maven-plugin:0.5.0",
		year = 2022,
		url = "https://github.com/terminological/r6-generator",
		doi = "10.5281/zenodo.6644970"
	)
```

And the same in markup:

```
Challen R (2022). _R6 generator maven plugin_. doi: 10.5281/zenodo.6644970 (URL: https://doi.org/10.5281/zenodo.6644970), Maven plugin artifact: io.github.terminological:r6-generator-maven-plugin:0.5.0, <URL:
https://github.com/terminological/r6-generator>.
```
