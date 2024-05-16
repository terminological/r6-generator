package uk.co.terminological.rjava.plugin;
import static org.twdata.maven.mojoexecutor.MojoExecutor.artifactId;
import static org.twdata.maven.mojoexecutor.MojoExecutor.attribute;
import static org.twdata.maven.mojoexecutor.MojoExecutor.configuration;
import static org.twdata.maven.mojoexecutor.MojoExecutor.element;
import static org.twdata.maven.mojoexecutor.MojoExecutor.executeMojo;
import static org.twdata.maven.mojoexecutor.MojoExecutor.executionEnvironment;
import static org.twdata.maven.mojoexecutor.MojoExecutor.goal;
import static org.twdata.maven.mojoexecutor.MojoExecutor.groupId;
import static org.twdata.maven.mojoexecutor.MojoExecutor.name;
import static org.twdata.maven.mojoexecutor.MojoExecutor.plugin;
import static org.twdata.maven.mojoexecutor.MojoExecutor.version;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.ArtifactUtils;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.ResolutionScope;


/**
 * The root of the maven plugin. This is the entry point called by maven.
 */
@Mojo( name = "generate-r-library", defaultPhase = LifecyclePhase.INSTALL, requiresDependencyResolution = ResolutionScope.RUNTIME )
public class R6GeneratorPlugin extends PluginBase {

	public void execute() throws MojoExecutionException {
		
		getLog().info("Executing R6 generator");
		setupPaths();
//		getLog().debug("Wiping previous files.");
		// Find additional exports in non-generated R files 
		List<String> additionalExports = scanDirectoryForExports(rDir);


		
    
		// STEP1: determine what format the package is being deployed in and build the shareable jar files
		if (!packageData.preCompileBinary()) {
			
			deleteRPackageJar(this.sourcesFile);
			getLog().info("Construct source jar");
			
			// If we are not using a pre-compiled binary then construct a jar of sources 
			// and move that into inst/java (format: jar means it is a jar file but
			// descriptorRefs: src means it does not have compiled content.
			// we use the assembly plugin because it preserves maven directory structure. 			
			executeMojo(
					plugin(
						groupId("org.apache.maven.plugins"),
						artifactId("maven-assembly-plugin"),
						version("3.6.0")),
					goal("single"),
					configuration(
							element(name("descriptorRefs"), 
								element(name("descriptorRef"),"src")
							),
							element(name("formats"),
								element(name("format"),"jar")
							)),
					executionEnvironment(
							mavenProject,
							mavenSession,
							pluginManager));
			
			
			getLog().info("Copying source files into inst/java.");
			copyCompliedJarToRPackage(this.sourcesFile);
			
			
			
		} else {
			if (packageData.packageAllDependencies()) {
				
				if (packageData.useShadePlugin()) {
					

					// If we are using a pre-compiled binary then construct a fat jar of all dependencies
					// and move that into inst/java
					deleteRPackageJar(this.shadedFile);
					// We can flatten out all the dependencies of the pom.xml file as they will be included
					// in the jar
					// then assemble the fat jar
					executeMojo(
							plugin(
								groupId("org.apache.maven.plugins"),
								artifactId("maven-shade-plugin"),
								version("3.5.2")),
							goal("shade"),
							configuration(
									element(name("outputDirectory"),"${project.build.outputDirectory}"),
									element(name("outputFile"),"target/${project.artifactId}-${project.version}-shaded.jar"),
									element(name("minimizeJar"),"true"),
									element(name("filters"),
											element(name("filter"),
													element(name("artifact"),"io.github.terminological:r6-generator-runtime"),
													element(name("includes"),element(name("include"),"**"))
											),
											element(name("filter"),
													element(name("artifact"),"org.slf4j:slf4j-api"),
													element(name("includes"),element(name("include"),"**"))
											),
											element(name("filter"),
													element(name("artifact"),"org.apache.logging.log4j:log4j-api"),
													element(name("includes"),element(name("include"),"**"))
											),
											element(name("filter"),
													element(name("artifact"),"org.apache.logging.log4j:log4j-core"),
													element(name("includes"),element(name("include"),"**"))
											)
									),
									element(name("transformers"),
											element(name("transformer"), attribute("implementation","org.apache.maven.plugins.shade.resource.ApacheLicenseResourceTransformer")),
											element(name("transformer"), attribute("implementation","org.apache.maven.plugins.shade.resource.ApacheNoticeResourceTransformer")),
											element(name("transformer"), attribute("implementation","org.apache.maven.plugins.shade.resource.ManifestResourceTransformer"),
												element(name("manifestEntries"), 
														element(name("Multi-Release"),"true")
												)
											)
									)
//									element(name("relocations"), 
//										element(name("relocation"),
//											element(name("pattern"), ""),
//											element(name("shadedPattern"), "shaded/$"),
//											element(name("rawString"), "true")
//									))
							),
							executionEnvironment(
									mavenProject,
									mavenSession,
									pluginManager));
					getLog().info("Copying shaded jar into inst/java.");
					copyCompliedJarToRPackage(this.shadedFile);
					
				} else {
					
					// If we are using a pre-compiled binary then construct a fat jar of all dependencies
					// and move that into inst/java
					deleteRPackageJar(this.jarFile);
					// We can flatten out all the dependencies of the pom.xml file as they will be included
					// in the jar
					// then assemble the fat jar
					executeMojo(
							plugin(
								groupId("org.apache.maven.plugins"),
								artifactId("maven-assembly-plugin"),
								version("3.6.0")),
							goal("single"),
							configuration(
									element(name("descriptorRefs"), 
										element(name("descriptorRef"),"jar-with-dependencies")
									),
									// https://stackoverflow.com/questions/53049346/is-log4j2-compatible-with-java-11
									element(name("archive"), 
										element(name("manifestEntries"), 
												element(name("Multi-Release"),"true")
										)
									)),
							executionEnvironment(
									mavenProject,
									mavenSession,
									pluginManager));
					getLog().info("Copying fat jar into inst/java.");
					copyCompliedJarToRPackage(this.jarFile);
				}
			} else {
				// If we are using a pre-compiled binary then construct a regular jar (which is already done by this stage by maven) 
				// and move that into inst/java
				deleteRPackageJar(this.thinJarFile);
				// assemble normal jar - this has already been done by this stage
				getLog().info("Copying thin jar into inst/java.");
				copyCompliedJarToRPackage(this.thinJarFile);
			}
		}
		
		// Generate java docs (for pkgdown site / github pages)
		if (packageData.useJavadoc() && !packageData.getDebugMode()) {
			getLog().info("Generating javadocs");
			executeMojo(
					plugin(
						groupId("org.apache.maven.plugins"),
						artifactId("maven-javadoc-plugin"),
						version("3.2.0")),
					goal("javadoc"),
					configuration(
							element(name("reportOutputDirectory"),docs.toString()),
							element(name("destDir"),"javadoc"),
							element(name("javadocExecutable"),packageData.getJavadocExecutable()),
							element(name("additionalOptions"),
									element(name("additionalOption"),"-header '<script type=\"text/javascript\" src=\"http://cdn.mathjax.org/mathjax/latest/MathJax.js?config=TeX-AMS-MML_HTMLorMML\"></script>'"),
									element(name("additionalOption"),"--allow-script-in-comments")),
							element(name("failOnError"),"false")
					),
					executionEnvironment(
							mavenProject,
							mavenSession,
							pluginManager));
		}
		
		// STEP 2: build the model of the code we are going to use to build the API and generate the R package files & documentation
		getLog().debug("Scanning source code.");
		Optional<RModel> model = QDoxParser.scanModel(mavenProject.getCompileSourceRoots(), packageData, getLog());
		if (model.isPresent()) {
				
			RModel m = model.get();
			
			String key = ArtifactUtils.versionlessKey("io.github.terminological","r6-generator-maven-plugin");
			Artifact pluginVersion = (Artifact) mavenProject.getPluginArtifactMap().get(key);
			m.setPluginMetadata(pluginVersion);
			m.setMavenMetadata(mavenProject);
			m.setRelativePath(mavenProject,rProjectDir);
					
			//TODO: tidy up the location of files.
			//write the code to the desired location.
			getLog().debug("Writing R6 library code.");
			RModelWriter writer = new RModelWriter(
					m.withAdditionalExports(additionalExports), 
					rProjectDir.toFile(),
					jarFile,
					rToPomPath,
					getLog()
					);
			writer.write();
		}
		
		// STEP 3: (Optional) Regenerate documentation using ROxygen2
		if (packageData.useRoxygen2()) {
			
			// must be an array to stop java tokenising it
			String rCMD[] = {"R","-e","devtools::document(pkg = '"+rProjectDir+"')"};
			getLog().info("Generating roxygen configuration.");
			getLog().debug(Arrays.stream(rCMD).collect(Collectors.joining(" ")));
			// Runtime run = Runtime.getRuntime();
			try {
				executeRcommand(rCMD, "ROxygen did not complete normally. The package may be in an inconsistent state.");
			} catch (IOException | InterruptedException e) {
				throw new MojoExecutionException("Failed to execute devtools::document", e);
			}
		}
		
		// STEP 3.5: Run a R CMD Check locally to make sure nothing is broken. 
		if (packageData.useCmdCheck()) {
			
			// must be an array to stop java tokenising it
			String rCMD[] = {"R","-e","tmp = devtools::check(pkg = '"+rProjectDir+"', args = c('--no-manual', '--no-multiarch')); if(length(tmp$errors) + length(tmp$warnings) > 0) stop('R CMD Check created errors or warnings')"};
			getLog().info("Running R CMD Check.");
			getLog().debug(Arrays.stream(rCMD).collect(Collectors.joining(" ")));
			// Runtime run = Runtime.getRuntime();
			try {
				executeRcommand(rCMD, "R CMD Check did not complete normally.");
			} catch (IOException | InterruptedException e) {
				throw new MojoExecutionException("Failed to execute R CMD Check", e);
			}
		}
		
		
		
		if (packageData.installLocal()) {
			
			String rCMD[] = {"R","-e","pak::local_install(root = '"+rProjectDir+"',upgrade=FALSE, ask=FALSE)"};
			getLog().info("Installing R package");
			getLog().debug(Arrays.stream(rCMD).collect(Collectors.joining(" ")));
			// Runtime run = Runtime.getRuntime();
			try {
				executeRcommand(rCMD, "R Package could not be installed");
			} catch (IOException | InterruptedException e) {
				throw new MojoExecutionException("Failed to execute pak::local_install", e);
			}
			
		}
		
		// STEP 4: (Optional) Run pkgdown in R to build site documentation.  
		if (packageData.usePkgdown()) {
			
			// must be an array to stop java tokenising it
			String rCMD[] = {"R","-e","pkgdown::build_site(pkg = '"+rProjectDir+"')"};
			getLog().info("Generating pkgdown site - please be patient");
			getLog().debug(Arrays.stream(rCMD).collect(Collectors.joining(" ")));
			// Runtime run = Runtime.getRuntime();
			try {
				executeRcommand(rCMD, "Pkgdown did not complete normally.");
			} catch (IOException | InterruptedException e) {
				throw new MojoExecutionException("Failed to execute pkgdown::build_site", e);
			}
		}
		
	}
	
	void executeRcommand(String rCMD[], String error) throws IOException, InterruptedException, MojoExecutionException {
		ProcessBuilder processBuilder = new ProcessBuilder(rCMD);
		processBuilder.redirectErrorStream(true);
		Process pr = processBuilder.start();
		int res = pr.waitFor();
		BufferedReader buf = new BufferedReader(new InputStreamReader(pr.getInputStream()));
		String line = "";
		if(res != 0) {
			while ((line=buf.readLine())!=null) {
				getLog().error(line);
			}
			throw new MojoExecutionException(error);
		} else {
			while ((line=buf.readLine())!=null) {
				getLog().info(line);
			}
		}
	}
	

}
