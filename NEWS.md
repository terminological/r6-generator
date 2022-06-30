## Versions

### 0.3.0 (not supported)

* This version was the first release to Maven central and had security vulnerabilities - It should not be used.
* supports fat-jar distributions of all bundled dependencies
* Description, namesspace, R6 class files and Rd files generated
* Doxygen2 and Pkgdown support
* Git workflows
* Support for basic R datatypes, and date, data-frames, numeric arrays, named lists,  
* Annotation driven code generation - including default R values
* Maven central deployment

### 0.3.1

* Protoype support for compilation from source at first load of the R-package in R to mitigate size issues with fat-jars with additional `compile-java-goal` configuration.
* Fixed security vulnerabilities
* Improved (i.e. working) code generation
* Maven archetype

### 0.4.0 (non functional)

* Changes to allow Maven execution at first package load allowing both dependency management and compilation. 
* Removal of the `compile-java-library` goal which is now automatically performed if needed when a `r6-generator` package is loaded. 
* Experiemental support for fat-jar, thin-jar and sources only Java library distributions through Maven configuration to support minimising package size for CRAN submission.
* Erroneous dependency to ossrh-deploy-config-master-SNAPSHOT requires OSSRH snaphot repository information be retained in project POMs  

### 0.4.1

* Flattened pom.xml for r6-generator modules allows fixes dependency on snapshot repositories declaration. Pom flattening also included in
projects created by archetype.
* Java log and console messages surfaced as R messages.
* Package startup is logged via java, rather than through R, respecting log level settings.
* Version numbering validity checks and repair, enables the reuse of maven artifact version as R package version, e.g. <version>${version}</version>. main-SNAPSHOT will become 0.0.0.9999 R package version.
* Fixes for windows users with thin-jar and source distributions caused by environment differences and mvn wrapper issues
* Github workflow and cran-comments changes to use specific versions of OS, R and Java.  Support for windows runners in github workflows to mitigate bug in R for windows that Rcmd.exe fails to respect .Rbuildignore directive to ignore `src` directory leading to a uniformative error about DLLs.
* fixed windows dataframe conversion bug due to platform encoding
* Improved archetype README template, moved default directory for java library to `java` due to windows R CMD check issue: 

### 0.5.0

* Title to sentence case to make CRAN happy.
* Breaking API changes refactoring runtime file to subpackage organisation for future proofing. 
    - MapRule and StreamRule are now static members of Rule;  
    - RNamedPredicates are now RFilter and in functional packages, 
    - RFunctions, RObjectVisitor, RStringr now in utils package. 
    - Java8Streams renamed to RStreams for consistency and moved to utils package.
* Non breaking changes to generated R-api: code generation of static Java methods are now also exposed as more regular R functions as well as R6 ones. changes in staticRd, & api templates. 
* Non breaking changes to Maven plugin API
    - Pom flattening built into the main plugin as a new goal bound to Process-resources phase
    - Clean phase as a new goal
* Maven archetype enhancements - generated demo project now includes support for importing data, functioning R tests, and a demo get started vignette.
     - configure maven archetype for 3 main output types (fat-jar, thin-jar, compile-source) 
 
### main-SNAPHOT
 
* TODO: we probably ought to check for static method name collisions between @RClass though.
* TODO: other maven plugin inputs need validity checking?
* TODO: prevent caching issue on failure to generate or load library, leading to unexpected success unsing old library version. 
* TODO: better approach to automation of whole library testing. Including an option to fail package generation when R tests fail or vignettes cannot be built, and a test package as part of the r6-generator project, with test that run during a integration test phase.
* TODO: configure maven archetype for 4 layouts (R first, java first, siblings, identity).

### Future work

* Split maven / java code functions within R to a seperate CRAN based native R library.
* Support for promises / asynchronous Java calls.
* Archetype documentation and class fixes
* Impact of Java 17: JEP 412 foriegn function and memory API.
* Impact of Java 17: JEP 338 vector API
* Additional data-type support as required
* All other ideas welcome.