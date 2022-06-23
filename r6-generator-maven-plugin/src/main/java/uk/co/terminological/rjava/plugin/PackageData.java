package uk.co.terminological.rjava.plugin;

import java.io.File;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.maven.MavenExecutionException;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Parameter;



/** Holds all the options for the maven plugin
 * @author terminological
 *
 */
public class PackageData {

	@Parameter
	private File[] inputFiles;
	
	@Parameter(required=true)
	private String title;
	
	@Parameter(required=true)
	private String version;

	@Parameter(required=true)
	private String packageName;
	
	@Parameter(required=true)
	private String maintainerName;
	
	@Parameter(defaultValue="",required=true)
	private String maintainerFamilyName;
	
	@Parameter(defaultValue="Unknown",required=true)
	private String maintainerOrganisation;
	
	@Parameter(required=true)
	private String maintainerEmail;
	
	@Parameter()
	private String maintainerORCID;
	
	@Parameter(required=true)
	private String description;
	
	@Parameter(required=true)
	private String license;
	
	@Parameter(required=false)
	private String[] rjavaOpts;
	
	@Parameter(property="defaultLogLevel",defaultValue="INFO",required=true)
	private String defaultLogLevel;
	
	@Parameter
	private Boolean debug;
	
	@Parameter
	private Boolean preCompileBinary;
	
	@Parameter
	private Boolean packageAllDependencies;
	
	@Parameter
	private Boolean usePkgdown;
	
	@Parameter
	private Boolean useRoxygen2;
	
	@Parameter
	private Boolean useJavadoc;

	@Parameter
	private String doi;

	@Parameter
	private String url;
	
	@Parameter(defaultValue="${java.home}/bin/javadoc")
	private String javadocExecutable;
	
	public String getMaintainerName() {
		return maintainerName;
	}

	public String getMaintainerFamilyName() {
		return maintainerFamilyName;
	}

	public String getMaintainerOrganisation() {
		return maintainerOrganisation;
	}
	
	public String getMaintainerEmail() {
		return maintainerEmail;
	}
	
	public String getMaintainerORCID() {
		return maintainerORCID;
	}

	/** {@code <debugMode>true</debugMode>}
	 * @return the debug mode
	 */
	public boolean getDebugMode() {
		return debug != null && debug.booleanValue(); 
	}
	
	/** {@code <defaultLogLevel>WARN</defaultLogLevel>}
	 * @return the default log level
	 */
	public String getDefaultLogLevel() {
		return defaultLogLevel == null ? "INFO" : defaultLogLevel;
	};
	
	/** {@code <usePkgdown>true</usePkgdown>}
	 * 
	 * build machine must have R and pkgdown installed
	 * @return pkgDown flag
	 */
	public boolean usePkgdown() {
		return usePkgdown != null && usePkgdown.booleanValue(); 
	}
	
	/** {@code <usePkgdown>true</usePkgdown>}
	 * 
	 * build machine must have R and devtools installed
	 * @return roxygen2 flag
	 */
	public boolean useRoxygen2() {
		return useRoxygen2 != null && useRoxygen2.booleanValue(); 
	}
	
	/** {@code <preCompileBinary>true</preCompileBinary>}
	 * 
	 * defaults to true. if false then no precompiled files are included and the package will have to be compiled from java source on first load 
	 * @return precompile flag
	 */
	public boolean preCompileBinary() {
		return preCompileBinary == null || preCompileBinary.booleanValue(); 
	}
	
	/** {@code <packageAllDependencies>true</packageAllDependencies>}
	 * 
	 * defaults to false. only relevant if preCompileBinary is true (the default) if packageAllDependencies is true then a fat jar will be precompiled 
	 * and included in the R package making the package large but completely self contained. This may be needed if the package depends on libraries
	 * that are not available to download from a public maven repository. 
	 * 
	 * If false then only this maven artifact is precompiled making the bundled java library code comparatively small. All other java dependencies declared in the maven file
	 * are fetched on first use of this library.
	 *   
	 * @return package fat jar flag
	 */
	public boolean packageAllDependencies() {
		return packageAllDependencies != null && packageAllDependencies.booleanValue(); 
	}
	
	/** {@code <useJavadoc>true</useJavadoc>}
	 * 
	 * build machine must have R and pkgdown installed
	 * @return javadoc flag
	 */
	public boolean useJavadoc() {
		return useJavadoc != null && useJavadoc.booleanValue(); 
	}
	
	public String getJavadocExecutable() {
		return this.javadocExecutable;
	}
	
	public boolean needsLicense() {
		return Arrays.asList(
				"MIT","BSD_2_clause","BSD_3_clause"
		).contains(license);
	}
	
	public String getYear() {
		return Integer.toString(LocalDate.now().getYear());
	}
	
	public String getDate() {
		return LocalDateTime.now().toString();
	}
	
	public boolean hasRJavaOpts() {
		return !getRJavaOpts().isEmpty();
	}
	
	/** For example {@code <rJavaOpts><rJavaOpt>-Xmx2048M</rJavaOpt></rJavaOpts>}
	* @return list of java options
	*/
	public List<String> getRJavaOpts() {
		if (this.rjavaOpts == null) return Collections.emptyList();
		return Arrays.asList(this.rjavaOpts);
	}

	public void setMaintainerName(String maintainerName) {
		this.maintainerName = maintainerName;
	}

	public void setMaintainerFamilyName(String maintainerFamilyName) {
		this.maintainerFamilyName = maintainerFamilyName;
	}

	public void setMaintainerEmail(String maintainerEmail) {
		this.maintainerEmail = maintainerEmail;
	}

	public String getDescription() {
		return description.replaceAll("\\s+", " ");
	}

	public String getLicense() {
		return license + (needsLicense() ? " + file LICENSE" : "");
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setLicense(String license) {
		this.license = license;
	}
	
	public File[] getInputFiles() {
		return inputFiles;
	}

	public void setInputFiles(File[] inputFiles) {
		this.inputFiles = inputFiles;
	}
	
	public String getPackageName() {
		return packageName;
	}

	public String getTitle() {
		return title;
	}

	public String getVersion() throws MojoExecutionException {
		if (version.equals("main-SNAPSHOT")) return "0.0.0.9999";
		version = version.replace("-SNAPSHOT", ".9999");
		if (!version.matches("[0-9\\.\\-]+")) throw new MojoExecutionException("Version must be only numerics in form 0.2.0.9000");
		return version;
	}

	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public void setDefaultLogLevel(String defaultLogLevel) {
		this.defaultLogLevel = defaultLogLevel;
	}
	
	public String getDoi() {
		return doi;
	}

	public void setDoi(String doi) {
		this.doi = doi;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}
	
}
