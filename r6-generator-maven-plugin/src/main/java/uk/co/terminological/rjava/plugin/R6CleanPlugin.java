package uk.co.terminological.rjava.plugin;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.ResolutionScope;


/**
 * The root of the maven plugin. This is the entry point called by maven.
 */
@Mojo( name = "clean-r-library", defaultPhase = LifecyclePhase.CLEAN, requiresDependencyResolution = ResolutionScope.RUNTIME )
public class R6CleanPlugin extends PluginBase {

	public void execute() throws MojoExecutionException {
		
		setupPaths();
		getLog().debug("Wiping previous R6 files.");
		
		rmContents(docs);
		rmContents(manDir);
		rmJar(jarDir, this.mavenProject.getArtifactId());
		rmGenerated(workflows);
		rmGenerated(rDir);
		rmGenerated(testDir);
		
		deleteRPackageJar(this.sourcesFile);
		deleteRPackageJar(this.jarFile);
		deleteRPackageJar(this.thinJarFile);
		
		
	}
	
	

}
