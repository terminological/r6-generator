package $package;

import java.io.IOException;
import java.io.InputStream;

import uk.co.terminological.rjava.types.RObject;

/** 
 * Get previously serialised data for testing
 */
public class TestData {

	/**
	 * Gets a previously serialised resource. This is intended to be used from Java for testing so not
	 * exposed in the R api.
	 * 
	 *  @param resourceName - the name of the resource previously serialised
	 */
	public static <X extends RObject> X getResouce(Class<X> binding, String resourceName) {
		try {
			InputStream is = TestData.class.getResourceAsStream("/"+resourceName+".ser");
			if(is==null) throw new IOException("Could not locate "+resourceName);
			return RObject.readRDS(binding, is);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
}
