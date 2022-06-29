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

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.ResolutionScope;


/**
 * The root of the maven plugin. This is the entry point called by maven.
 */
@Mojo( name = "flatten-pom", defaultPhase = LifecyclePhase.PROCESS_RESOURCES, requiresDependencyResolution = ResolutionScope.RUNTIME )
public class R6FlattenerPlugin extends PluginBase {

	public void execute() throws MojoExecutionException {
		
		getLog().info("Executing R6 generator");
		
		if (!packageData.preCompileBinary()) {
			
			executeMojo(
					plugin(
						groupId("org.codehaus.mojo"),
						artifactId("flatten-maven-plugin"),
						version("1.2.7")),
					goal("flatten"),
					configuration(
							element(name("flattenMode"),"ossrh")
					),
					executionEnvironment(
							mavenProject,
							mavenSession,
							pluginManager));
		
		} else {
			if (packageData.packageAllDependencies()) {
		
				// We can flatten out all the dependencies of the pom.xml file as they will be included
				// in the jar
				// We will flatten out the pom.xml to pull in all parent code:
				executeMojo(
						plugin(
							groupId("org.codehaus.mojo"),
							artifactId("flatten-maven-plugin"),
							version("1.2.7")),
						goal("flatten"),
						configuration(
								element(name("flattenMode"),"fatjar")
						),
						executionEnvironment(
								mavenProject,
								mavenSession,
								pluginManager));
				
			} else {
				
				// If we are using a pre-compiled binary then construct a regular jar with) 
				// flatten out parent pom:
				executeMojo(
						plugin(
							groupId("org.codehaus.mojo"),
							artifactId("flatten-maven-plugin"),
							version("1.2.7")),
						goal("flatten"),
						configuration(
								element(name("flattenMode"),"ossrh")
						),
						executionEnvironment(
								mavenProject,
								mavenSession,
								pluginManager));
			}
		}
	}
}
