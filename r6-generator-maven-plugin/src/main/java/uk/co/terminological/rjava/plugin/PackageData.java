package uk.co.terminological.rjava.plugin;

import java.io.File;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.commons.text.WordUtils;
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
	
	@Parameter(required=true, defaultValue="${package.version}")
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
	private Boolean useShadePlugin;
	
	@Parameter
	private Boolean usePkgdown;
	
	@Parameter
	private Boolean useRoxygen2;
	
	@Parameter
	private Boolean useCmdCheck;
	
	@Parameter
	private Boolean installLocal;
	
	@Parameter
	private Boolean useJavadoc;
	
	@Parameter
	private String doi;
	
	@Parameter
	private String githubOrganisation;
	
	@Parameter
	private String githubRepository;
	
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
	
	public String getGithubOrganisation() {
		return githubOrganisation;
	}
	
	public String getGithubRepository() {
		return githubRepository;
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
	 * build machine must have R and pkgdown installed
	 * @return pkgDown flag
	 */
	public boolean useCmdCheck() {
		return useCmdCheck != null && useCmdCheck.booleanValue(); 
	}
	
	/** {@code <useRoxygen2>true</useRoxygen2>}
	 * 
	 * defaults to false, as requires build machine must have R, roxygen and 
	 * devtools installed
	 * @return roxygen2 flag
	 */
	public boolean useRoxygen2() {
		return useRoxygen2 != null && useRoxygen2.booleanValue(); 
	}
	
	/** {@code <installLocal>true</installLocal>}
	 * 
	 * defaults to false, as requires build machine must have R and 
	 * devtools installed
	 * @return install local flag
	 */
	public boolean installLocal() {
		return installLocal != null && installLocal.booleanValue(); 
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
	
	/** {@code <useShadePlugin>true</useShadePlugin>}
	 * 
	 * defaults to false. only relevant if packageAllDependencies is true (the default). if useShadePlugin is true then a shaded jar will be precompiled 
	 * and included in the R package making the package large but completely self contained. This may be needed if the package depends on libraries
	 * that are not available to download from a public maven repository. 
	 * 
	 *   
	 * @return package shaded flag
	 */
	public boolean useShadePlugin() {
		return useShadePlugin != null && useShadePlugin.booleanValue(); 
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

	public String getLicenseType() {
		return license;
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
		return WordUtils.capitalizeFully(title);
	}

	public String getVersion() throws MojoExecutionException {
		Version tmp = new Version(version);
		return tmp.rVersion();
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

	public List<String> getUrls() {
		List<String> out = new ArrayList<>();
		if (url != null) out.add(url);
		if (this.getGithubOrganisation() != null && this.getGithubRepository() != null) {
			if (url == null) {
				out.add(
					"https://"+this.getGithubOrganisation()+".github.io/"+this.getGithubRepository()+"/index.html"
				);
			}
			out.add(
				"https://github.com/"+this.getGithubOrganisation()+"/"+this.getGithubRepository()
				);
		}
		return out;
	}
	
	public void setUrl(String url) {
		this.url = url;
	}
	
}
