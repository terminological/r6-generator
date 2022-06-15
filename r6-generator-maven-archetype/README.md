# r6-generator-maven-archetype

Archetype to setup r6-generator maven projects for Java library to R project integration.

The archetype needs the following information:

* githubOrganisation
* githubRepository
* package - the java package, optional, defaults to io.github.<githubOrganisation>
* rPackageName - optional, defaults to same as githubRepository
* versionId - optional, defaults to main-SNAPSHOT
* rPackageVersion - optional, defaults to 0.0.0.9000
* rPackageLicense - optional, defaults to MIT
* maintainerName - optional, defaults to an example name
* maintainerSurname - ditto
* maintainerEmail - ditto
* maintainerOrganisation - optional, defaults to same as githubOrganisation.

Usage:

```BASH
cd ~/Git
mvn archetype:generate \
  -DarchetypeGroupId=io.github.terminological \
  -DarchetypeArtifactId=r6-generator-maven-archetype \
  -DarchetypeVersion=0.3.0-SNAPSHOT \
  -DgithubOrganisation=exampleOrganisation \
  -DgithubRepository=examplePackage
```

With the organisation and repository I suggest not using names with hyphens for the repository as they can't be used as R package names.
The use of camel-case is probably also not ideal.

In general the rest of the default values can be accepted.

The newly created Java project will be in the examplePackage subdirectory. There is a README.md file there which details the next steps.

To generate the R package from the Java library it needs to be installed using maven. The java component of the library is in the `src`
subdirectory.


```BASH
cd ~/Git/examplePackage/src
mvn install
```

The resulting generated R-package can be pushed to github where it should trigger appropriate workflows if the organisation and repository names given above match.

```BASH
cd ~/Git/examplePackage
git init -b main
git add . 
git commit -m "initial commit"
gh repo create exampleOrganisation/examplePackage --source=. --public
git push

```

Once basic setup complete more background on developing your package is available here:

https://github.com/terminological/r6-generator-docs
