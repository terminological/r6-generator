# ${rPackageName}

[![R-CMD-check](https://github.com/${githubOrganisation}/${githubRepository}/workflows/R-CMD-check/badge.svg)](https://github.com/${githubOrganisation}/${githubRepository}/actions)

## Development notes

To generate the R code from the Java classes in this example library run: 

```BASH
cd ~/Git/${githubRepository}/src
mvn install
```

The newly generated R project can be pushed to github where it should trigger appropriate workflows if the organisation and repository names given above match.

```BASH
cd ~/Git/${githubRepository}
git init -b main
git add . 
git commit -m "initial commit"
gh repo create ${githubOrganisation}/${githubRepository} --source=. --public
git push

```

Once the basic structure is setup more background on developing useful functionality in your package is available here:

https://github.com/terminological/r6-generator-docs

After this is done this development section does not need to be in your README.md for users, and can be taken out. 

## Usage instructions

This template readme should be updated to include documentation for the end users of your R-package with the aims and objectives, installation and usage guide for your users

`${rPackageName}` is based on a java library and must have a working version of `Java` and `rJava` installed prior to installation. The following commands can ensure that your `rJava` installation is working.


```R
install.packages("rJava")
rJava::.jinit()
rJava::J("java.lang.System")$getProperty("java.version")
```

To install `${rPackageName}` in R:

```R
library(devtools)
# you can install from the local copy:
load_all("~/Git/${githubRepository}")

# or from your github repository:
# install_github("${githubOrganisation}/${githubRepository}", build_opts = c("--no-multiarch"))
# N.B. the no-multiarch option is required for windows users.
```

A basic usage scenario:


```R
# a basic smoke test
J = ${rPackageName}::JavaApi$get()

# exploring the API using autocomplete in RStudio
# is a matter of typing J$<ctrl-space> 

tmp = J$BasicExample$new()
tmp$doHelloWorld()

# generated documentation will be available

?${rPackageName}-package
?${rPackageName}::BasicExample

```

The following links will take you to the R library documentation (e.g. pkgdown site) once this has been generated from the Java code, and assuming Github pages has been enabled for the repository.  

see the [full docs](https://${githubOrganisation}.github.io/${rPackageName})

see the [${rPackageName} home page](https://${githubOrganisation}.github.io/${rPackageName}/docs/)


