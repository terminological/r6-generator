package uk.co.terminological.rjava;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.appender.ConsoleAppender;
import org.apache.logging.log4j.core.config.Configurator;
import org.apache.logging.log4j.core.config.builder.api.AppenderComponentBuilder;
import org.apache.logging.log4j.core.config.builder.api.ConfigurationBuilder;
import org.apache.logging.log4j.core.config.builder.api.ConfigurationBuilderFactory;
import org.apache.logging.log4j.core.config.builder.api.RootLoggerComponentBuilder;
import org.apache.logging.log4j.core.config.builder.impl.BuiltConfiguration;
//import java.io.PrintStream;
//import org.rosuda.JRI.RConsoleOutputStream;
//import org.rosuda.JRI.Rengine;

public class LogController {

	/**
	 * Configure log dynamically. This can be called from R to change the log level
	 *
	 * @param logLevel the log level one of ALL, DEBUG, ERROR, FATAL, INFO, OFF, TRACE, WARN
	 */
	public static void changeLogLevel(String logLevel) {
		Configurator
			.setAllLevels(LogManager.getRootLogger().getName(), 
				Level.toLevel(logLevel, Level.INFO));
	}
	
	/**
	 * Dynamically reconfigure java logging using a log4j properties file.
	 *
	 * @param filename the filename of the properties file.
	 */
	public static void reconfigureLog(String filename) {
		LoggerContext context = (org.apache.logging.log4j.core.LoggerContext) LogManager.getContext(false);
		File file = new File(filename);
		// this will force a reconfiguration
		context.setConfigLocation(file.toURI());
	}
	
	static ByteArrayOutputStream baos = new ByteArrayOutputStream();
	
	// This is needed as rJava does not interact properly with the console.
	// If we want console output then this is 
	// So instead we redirect System.out to a static byte array and 
	// print the contents after every method call. This is bad
	// for all sorts of reasons. Multiple concurrent use is one, which will scatter the 
	// output all over the place. Multiple installations of different java based libraries will probably mess it all up
	// as well. If this is not needed it can be removed and should fallback gracefully :-) or crash.
	
	// we also want this to be a per thread console: 
	// https://stackoverflow.com/questions/10015182/in-a-multithreaded-java-program-does-each-thread-have-its-own-copy-of-system-ou
	// TODO: the whole lot needs refactoring to another project.
	public static void setupRConsole() {
		baos = new ByteArrayOutputStream();
		PrintStream ps = new PrintStream(baos);
		System.setOut(ps);
		// TODO: consider JRI support for proper console callbacks:
		// https://stackoverflow.com/questions/54319034/display-java-console-output-in-r-using-rjava
		// JRI library files are here: system.file("jri", package="rJava")
		// Rengine r = new Rengine();
		// RConsoleOutputStream rs = new RConsoleOutputStream(r, 0);
		// System.setOut(new PrintStream(rs));
	}
	
	/**
	 * Gets the system messages from Java and presents them to R.
	 *
	 * @return the system messages
	 */
	public static String getSystemMessages() {
		String out = baos.toString();
		baos.reset();
		return out;
	}
		
	/**
	 * Configure log dynamically. This can be called from R to change the log level
	 *
	 * @param logLevel the log level one of ALL, DEBUG, ERROR, FATAL, INFO, OFF, TRACE, WARN
	 */
	public static void configureLog(String logLevel) {

		
		ConfigurationBuilder<BuiltConfiguration> builder = 
				ConfigurationBuilderFactory.newConfigurationBuilder();
		Level lev = Level.toLevel(logLevel, Level.INFO);

		builder.setStatusLevel(lev);
		// naming the logger configuration
		builder.setConfigurationName("DefaultLogger");

		// create a console appender
		AppenderComponentBuilder appenderBuilder = builder
			.newAppender("Console", "CONSOLE")
		    .addAttribute("target", ConsoleAppender.Target.SYSTEM_OUT);
		// add a layout like pattern, json etc
		appenderBuilder
			.add(builder.newLayout("PatternLayout")
					.addAttribute("pattern", "%d %p %c [%t] %m%n"));
		RootLoggerComponentBuilder rootLogger = 
				builder.newRootLogger(lev);
		rootLogger.add(builder.newAppenderRef("Console"));

		builder.add(appenderBuilder);
		builder.add(rootLogger);
		Configurator.initialize(builder.build());
		Configurator.setAllLevels(LogManager.getRootLogger().getName(),lev);
	}
	
	/**
	 * Handles files, jar entries, and deployed jar entries in a zip file (EAR).
	 * @return The date if it can be determined, or null if not.
	 */
	public static String getClassBuildTime() {
	    LocalDateTime d = null;
	    Class<?> currentClass = new Object() {}.getClass().getEnclosingClass();
	    URL resource = currentClass.getResource(currentClass.getSimpleName() + ".class");
	    if (resource != null) {
	        if (resource.getProtocol().equals("file")) {
	            try {
	                d = Instant.ofEpochMilli(new File(resource.toURI()).lastModified())
	                		.atZone(ZoneId.systemDefault()).toLocalDateTime();
	            } catch (URISyntaxException ignored) { }
	        } else if (resource.getProtocol().equals("jar")) {
	            String path = resource.getPath();
	            d = Instant.ofEpochMilli(
	            		new File(path.substring(5, path.indexOf("!"))).lastModified()
	            	).atZone(ZoneId.systemDefault()).toLocalDateTime();
	        } else if (resource.getProtocol().equals("zip")) {
	            String path = resource.getPath();
	            File jarFileOnDisk = new File(path.substring(0, path.indexOf("!")));
	            //long jfodLastModifiedLong = jarFileOnDisk.lastModified ();
	            //Date jfodLasModifiedDate = new Date(jfodLastModifiedLong);
	            try(JarFile jf = new JarFile (jarFileOnDisk)) {
	                ZipEntry ze = jf.getEntry (path.substring(path.indexOf("!") + 2));//Skip the ! and the /
	                long zeTimeLong = ze.getTime ();
	                d = Instant.ofEpochMilli(
		            		zeTimeLong
	    	            	).atZone(ZoneId.systemDefault()).toLocalDateTime();
	            } catch (IOException|RuntimeException ignored) { }
	        }
	    }
	    return d == null ? "unknown" : d.format(DateTimeFormatter.ISO_DATE_TIME);
	}
}
