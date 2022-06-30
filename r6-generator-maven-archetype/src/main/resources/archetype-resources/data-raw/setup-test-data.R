library(${rPackageName})

# Serialise the ggplot diamonds dataframe into the java/src/test/resources directory
serialise_dataframe(ggplot2::diamonds, "diamonds")