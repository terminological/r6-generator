package $package;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.co.terminological.rjava.RClass;
import uk.co.terminological.rjava.RConverter;
import uk.co.terminological.rjava.RMethod;
import uk.co.terminological.rjava.RDefault;
import uk.co.terminological.rjava.types.RCharacter;

/**
 * This class is a very basic example of the features of the rJava maven plugin. <br>
 * The class is annotated with an @RClass to identify it as part of the R API. <br>
 */
@RClass(exampleSetup = {
	"J = JavaApi$get()",
	"ex = J$MoreComplexExample$new('Hello world')"
}, testSetup = {
	"J = JavaApi$get()",
	"ex = J$MoreComplexExample$new('test message')"
})
public class MoreComplexExample {

	String message;
	static Logger log = LoggerFactory.getLogger(BasicExample.class);
	
	/**
	 * A maximum of one constructor of any signature can be used. <br>
	 * 
	 * If different constructors are needed then they may be used but not 
	 * included in the R Api (i.e. not annotated with @RMethod.) <br>
	 * 
	 * Static factory methods can be used instead.
	 * @param message - a hello world message
	 */
	@RMethod(examples = {
		"J = JavaApi$get()",
		"ex = J$MoreComplexExample$new('Hello from Java constructor!')"
	})
	public MoreComplexExample(@RDefault(rCode="'default message'") String message) {
		log.info("A minimal example with: "+message);
		this.message = message;
	}
	
	/**
	 * Description of a getMessage function
	 * @return this java method returns the message that the object was created with 
	 */
	@RMethod(examples = {
		"ex$getMessage()"
	}, tests = {
		"testthat::expect_equal(ex$getMessage(), 'test message')"
	})
	public RCharacter getMessage() {
		return RConverter.convert(message);
	}
	
	/**
	 * A greet function
	 * 
	 * responds to someone
	 * 
	 * @param name - the name of the person to greet
	 * @return a greeting 
	 */
	@RMethod(examples = {
		"salutation('friend!')"
	}, tests = {
		"expect_equal(salutation('friend!'),'Hello, friend!')"
	})
	public static RCharacter salutation(@RDefault(rCode="'Anonymous'")String name) {
		return RConverter.convert("Hello, "+name);
	}
	
}
