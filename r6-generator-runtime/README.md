# r6-generator-runtime

Runtime maven dependency for annotation processor and java utilities for mapping to and from R supported data structures

This package contains necessary annotations and data structures that are required for the Java developer who is trying to create an R library using the r6-generator-maven-plugin, and is a required dependency of such a project. 

The main part of the project are the annotations and the data structures

```Java
import uk.co.terminological.rjava.RClass;
import uk.co.terminological.rjava.RMethod;
import uk.co.terminological.rjava.types.*;
```

These allow you to specify an API for using your java code in R like so:

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
	 * @return this java method returns a String
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

For a minimal example and the main maven plugin code see: 
https://github.com/terminological/r6-generator

For a more complete working example and further documentation see: 
https://github.com/terminological/r6-generator-docs

## Datatype conversion

The runtime component of this project also contains tools to enable bidirectional lossless transfer of R data structures into Java, in a idiom that is relatively comfortable to Java developers, 
without having to manage the edge cases posed by R's interchangeable use of vectors and NA values. This is further documented in the docs project:

https://github.com/terminological/r6-generator-docs
