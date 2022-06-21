package uk.co.terminological.rjava.plugin;
import static org.twdata.maven.mojoexecutor.MojoExecutor.artifactId;
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
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
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
		getLog().debug("Wiping previous files.");
		// Find additional exports in non-generated R files 
		List<String> additionalExports = scanDirectoryForExports(rDir);

		//TODO: make this a mvn clean stage
		rmContents(docs);
		rmContents(manDir);
		rmJar(jarDir, this.mavenProject.getArtifactId());
		rmGenerated(workflows);
		rmGenerated(rDir);
		
    
		// STEP1: determine what format the package is being deployed in and build the shareable jar files
		if (!packageData.preCompileBinary()) {
			
			deleteJar(this.sourcesFile);
			getLog().info("Construct source jar");
		
			// If we are not using a pre-compiled binary then construct a jar of sources 
			// and move that into inst/java
//			executeMojo(
//					plugin(
//						groupId("org.apache.maven.plugins"),
//						artifactId("maven-source-plugin"),
//						version("2.2.1")),
//					goal("jar-no-fork"),
//					configuration(
//							element(name("includePom"),"true")
//					),
//					executionEnvironment(
//							mavenProject,
//							mavenSession,
//							pluginManager));
			
			executeMojo(
					plugin(
						groupId("org.apache.maven.plugins"),
						artifactId("maven-assembly-plugin"),
						version("3.2.0")),
					goal("single"),
					configuration(
							element(name("descriptorRefs"), 
								element(name("descriptorRef"),"src")
							),
							element(name("formats"),
								element(name("foramt"),"jar")
							)),
					executionEnvironment(
							mavenProject,
							mavenSession,
							pluginManager));
			
			
			getLog().info("Copying source files into inst/java.");
			moveJar(this.sourcesFile);
			
			
			
		} else {
			if (packageData.packageAllDependencies()) {
				// If we are using a pre-compiled binary then construct a fat jar of all dependencies 
				// and move that into inst/java
				deleteJar(this.jarFile);
				executeMojo(
						plugin(
							groupId("org.apache.maven.plugins"),
							artifactId("maven-assembly-plugin"),
							version("3.2.0")),
						goal("single"),
						configuration(
								element(name("descriptorRefs"), 
									element(name("descriptorRef"),"jar-with-dependencies")
								)),
						executionEnvironment(
								mavenProject,
								mavenSession,
								pluginManager));
				getLog().info("Copying fat jar into inst/java.");
				moveJar(this.jarFile);
			} else {
				// If we are using a pre-compiled binary then construct a regular jar (which is already done by this stage by maven) 
				// and move that into inst/java
				deleteJar(this.thinJarFile);
				getLog().info("Copying thin jar into inst/java.");
				moveJar(this.thinJarFile);
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
		if (packageData.useRoxygen2() && !packageData.getDebugMode()) {
			
			// must be an array to stop java tokenising it
			String rCMD[] = {"R","-e","devtools::document(pkg = '"+rProjectDir+"')"};
			getLog().info("Generating roxygen configuration.");
			getLog().debug(Arrays.stream(rCMD).collect(Collectors.joining(" ")));
			// Runtime run = Runtime.getRuntime();
			try {
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
					throw new MojoExecutionException("ROxygen did not complete normally. The package is probably in an inconsistent state.");
				} else {
					while ((line=buf.readLine())!=null) {
						getLog().info(line);
					}
				}
			} catch (IOException | InterruptedException e) {
				throw new MojoExecutionException("Failed to execute pkgdown", e);
			}
		}
		
		// STEP 3.5: Run a R CMD Check locally to make sure nothing is broken. 
		
		// STEP 4: (Optional) Run pkgdown in R to build site documentation.  
		if (packageData.usePkgdown() && !packageData.getDebugMode()) {
			
			// must be an array to stop java tokenising it
			String rCMD[] = {"R","-e","pkgdown::build_site(pkg = '"+rProjectDir+"')"};
			getLog().info("Generating pkgdown site - please be patient");
			getLog().debug(Arrays.stream(rCMD).collect(Collectors.joining(" ")));
			// Runtime run = Runtime.getRuntime();
			try {
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
					getLog().error("Pkgdown did not complete normally. Details in the log file.");
				} else {
					while ((line=buf.readLine())!=null) {
						getLog().info(line);
					}
				}
			} catch (IOException | InterruptedException e) {
				throw new MojoExecutionException("Failed to execute pkgdown", e);
			}
		}
		
		
		
	}
	
	
	private void deleteJar(String jarFile) {
		Path jarLoc = jarDir.resolve(jarFile);
		try {
			if (Files.exists(jarLoc)) Files.delete(jarLoc);
		} catch (IOException e) {
			getLog().warn("Couldn't delete the jar from: "+jarLoc);
		}
	}
	
	private void moveJar(String jarFile) throws MojoExecutionException {
		Path jarLoc = jarDir.resolve(jarFile);
		try {
			
			Files.createDirectories(rProjectDir);
			File targetDir = new File(mavenProject.getModel().getBuild().getDirectory());
			Files.copy(
					Paths.get(targetDir.getAbsolutePath(), jarFile), 
					jarLoc, StandardCopyOption.REPLACE_EXISTING);
			
		} catch (IOException e) {
			throw new MojoExecutionException("Couldn't move fat jar",e);
		}
	}
}
