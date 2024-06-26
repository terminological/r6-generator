package uk.co.terminological.rjava;

import java.io.File;
import java.io.IOException;
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

/**
 * <p>LogController class.</p>
 *
 * @author vp22681
 * @version $Id: $Id
 */
public class RLogController {

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
				// .addAttribute("pattern", "%d %p %c [%t] %m%n"));
				.addAttribute("pattern", "%m%n"));
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
	 *
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
