package uk.co.terminological.rjava.plugin;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.logging.Log;

import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapperBuilder;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateExceptionHandler;

public class RModelWriter {

	private Configuration cfg;
	private RModel model;
	private File target;
	private String jarFileName;
	private String rToPomPath;
	private Log log;

	public RModelWriter(RModel model, File target, String jarFileName, String rToPomPath, Log log) {
		this.model = model;
		this.target = target;
		this.jarFileName = jarFileName;
		this.rToPomPath = rToPomPath;
		this.log=log;
	}

	public void write() throws MojoExecutionException {

		if (target == null) throw new MojoExecutionException("No target directory has been set");

		// Freemarker stuff
		cfg = new Configuration(Configuration.VERSION_2_3_31);
		cfg.setObjectWrapper(new DefaultObjectWrapperBuilder(Configuration.VERSION_2_3_31).build());
		cfg.clearTemplateCache();
		cfg.setDefaultEncoding("UTF-8");
		cfg.setTemplateExceptionHandler(TemplateExceptionHandler.DEBUG_HANDLER);
		cfg.setClassForTemplateLoading(RModelWriter.class,"/");

		// TODO:: these file locations are already defnied by the plugin and should be reused
		// rather than recalculated. We woudl have to figure out how the plugin passes them though
		File rDir = new File(target,"R");
		rDir.mkdirs();
		
		File instDir = new File(target,"inst");
		instDir.mkdirs();
		
		File manDir = new File(target,"man");
		manDir.mkdirs();
		
		File testDir = new File(target,"tests/testthat");
		testDir.mkdirs();
		
		File workflowDir = new File(target,".github/workflows");
		workflowDir.mkdirs();
		
		Map<String,Object> typeRoot = new HashMap<>();
		typeRoot.put("model", model);
		typeRoot.put("jarFileName", jarFileName);
		typeRoot.put("rToPomPath", rToPomPath);
	
		
		doGenerate(new File(target,"DESCRIPTION"),getTemplate("rjavaDescription.ftl"),typeRoot);
		doGenerateSafe(new File(target,"NAMESPACE"),getTemplate("rjavaNamespace.ftl"),typeRoot);
		doGenerateSafe(new File(instDir,"CITATION"),getTemplate("rjavaCitation.ftl"),typeRoot);
		doGenerate(new File(testDir.getParentFile(),"testthat.R"),getTemplate("rjavaTestRunner.ftl"),typeRoot);
		if (model.getConfig().needsLicense()) doGenerate(new File(target,"LICENSE"),getTemplate("rjavaLicense.ftl"),typeRoot);
		doGenerate(new File(target,"LICENSE.md"),getTemplate("rjavaLicenseMd.ftl"),typeRoot);
		doGenerate(new File(manDir,"JavaApi.Rd"),getTemplate("rjavaApiRd.ftl"),typeRoot);
		doGenerate(new File(manDir,model.getConfig().getPackageName()+"-package.Rd"),getTemplate("rjavaPackageRd.ftl"),typeRoot);
		doGenerate(new File(rDir,"JavaApi.R"),getTemplate("rjavaApiR.ftl"),typeRoot);
		doGenerate(new File(rDir,"StaticApi.R"),getTemplate("rjavaStaticApiR.ftl"),typeRoot);
		doGenerate(new File(rDir,"zzz.R"),getTemplate("rjavaZzz.ftl"),typeRoot);
		doGenerateSafe(new File(target,"cran-comments.md"),getTemplate("rjavaCranComments.ftl"),typeRoot);
		doGenerateSafe(new File(target,".Rbuildignore"),getTemplate("rjavaRbuildignore.ftl"),typeRoot);
		doGenerateSafe(new File(workflowDir,"R-CMD-check.yaml"),getTemplate("rjavaGithubWorkflow.ftl"),typeRoot);
		
		for (RClass type: model.getClassTypes()) {
			
			typeRoot.put("class", type);
			
			doGenerate(new File(manDir,type.getSimpleName()+".Rd"),getTemplate("rjavaRd.ftl"),typeRoot);
			doGenerate(new File(rDir,type.getSimpleName()+".R"),getTemplate("rjavaClassR.ftl"),typeRoot);
			doGenerateSafe(new File(testDir,"test-"+type.getSimpleName()+".R"),getTemplate("rjavaTests.ftl"),typeRoot);
			
			for (RMethod method: type.getStaticMethods()) {
				typeRoot.put("method", method);
				doGenerate(new File(manDir,method.getSnakeCaseName()+".Rd"),getTemplate("rjavaStaticRd.ftl"),typeRoot);
			}
			
		}
		
		// supporting R files
		try {
			Stream.of("aa_maven.R","aa_rfuture.R").forEach(x -> {
				try {
					Files.copy(
							RModelWriter.class.getResourceAsStream("/"+x), 
							Paths.get(new File(rDir,x).getPath()),
							StandardCopyOption.REPLACE_EXISTING
					);
				} catch (IOException e) {
					throw new RuntimeException("Could not copy file: "+x,e);
				}
			});
		} catch (RuntimeException e) {
			throw new MojoExecutionException(e.getMessage(),e.getCause());
		}
		
		// documentation
		try {
			Stream.of("RFuture.Rd","pipe.Rd").forEach(x -> {
				try {
					Files.copy(
							RModelWriter.class.getResourceAsStream("/"+x), 
							Paths.get(new File(manDir,x).getPath()),
							StandardCopyOption.REPLACE_EXISTING
					);
				} catch (IOException e) {
					throw new RuntimeException("Could not copy file: "+x,e);
				}
			});
		} catch (RuntimeException e) {
			throw new MojoExecutionException(e.getMessage(),e.getCause());
		}
		
	}

	private Template getTemplate(String name) throws MojoExecutionException {
		Template tmp;
		try {
			tmp = cfg.getTemplate(name);
		} catch (IOException e) {
			throw new MojoExecutionException("Couldn't load template "+name,e);
		}
		log.debug("Using freemarker template: "+name);
		return tmp;
	}

	
	
	private void doGenerateSafe(File file, Template tmp, Map<String,Object> root) throws MojoExecutionException {
		if (file.exists()) {
			if (PluginBase.isGenerated(file)) {
				doGenerate(file,tmp,root);
			} else {
				log.info("Skipping file as not generated by plugin: "+file.getAbsolutePath());
				// leave file as is
				return;
			}
		} else {
			doGenerate(file,tmp,root);
		}
	}
	
	// Utility to handle the freemarker generation mechanics.
	private void doGenerate(File file, Template tmp, Map<String,Object> root) throws MojoExecutionException {

		try {

			Writer out;

			out = new PrintWriter(new FileOutputStream(file));
			log.info("Writing file: "+file.getAbsolutePath());
			tmp.process(root, out);
			out.close();

		} catch (IOException e) {
			e.printStackTrace();
			throw new MojoExecutionException("Couldn't write source file", e);
			// this should not happen. 

		} catch (TemplateException e) {
			
			throw new MojoExecutionException("Error in freemarker template: "+tmp.getName(),e);
		}
	}

}
