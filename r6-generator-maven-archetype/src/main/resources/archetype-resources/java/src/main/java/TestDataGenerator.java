package $package;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.co.terminological.rjava.RClass;
import uk.co.terminological.rjava.RMethod;
import uk.co.terminological.rjava.RDefault;
import uk.co.terminological.rjava.types.*;

/** 
 * Serialise data for testing
 * 
 * Temporarily useful class which can help generate testing data from R that can be used natively in Java
 * for testing purposes. Once test data is set up this class can be deleted.
 * @author terminological
 *
 */
@RClass
public class TestDataGenerator {

	static Logger log = LoggerFactory.getLogger(TestDataGenerator.class);
	
	/**
	 * Write a dataframe to disk as a java object
	 * 
	 * @param dataframe - the dataframe to write
	 * @param resourceName - the internal name of the resource for Java testing
	 * @param filename - a filename (maybe generated)
	 * @return nothing
	 */
	@RMethod
	public static void serialiseDataframe(
			RDataframe dataframe, 
			@RDefault(rCode="'test_dataframe'") RCharacter resourceName,
			@RDefault(rCode="here::here(paste0('java/src/test/resources/',resourceName,'.ser'))") String filename
	) throws IOException {
		FileOutputStream fos = new FileOutputStream(filename);
		dataframe.writeRDS(fos);
		log.info("dataframe written to: "+filename);
	}
	
	/**
	 * Write a list to disk as a java object
	 * 
	 * @param list - the list to write
	 * @param resourceName - the internal name of the resource for Java testing
	 * @param filename - a filename (maybe generated)
	 * @return nothing
	 */
	@RMethod
	public static void serialiseList(
			RList list, 
			@RDefault(rCode="'test_list'") RCharacter resourceName,
			@RDefault(rCode="here::here(paste0('java/src/test/resources/',resourceName,'.ser'))") String filename
	) throws IOException {
		FileOutputStream fos = new FileOutputStream(filename);
		list.writeRDS(fos);
		log.info("list written to: "+filename);
	}
	
	/**
	 * Write a named to disk as a java object
	 * 
	 * @param namedList - the list to write
	 * @param resourceName - the internal name of the resource for Java testing
	 * @param filename - a filename (maybe generated)
	 * @return nothing
	 */
	@RMethod
	public static void serialiseNamedList(
			RNamedList namedList, 
			@RDefault(rCode="'test_namedlist'") RCharacter resourceName,
			@RDefault(rCode="here::here(paste0('java/src/test/resources/',resourceName,'.ser'))") String filename
	) throws IOException {
		FileOutputStream fos = new FileOutputStream(filename);
		namedList.writeRDS(fos);
		log.info("named list written to: "+filename);
	}
	
}
