package uk.co.terminological.rjava.types;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

import uk.co.terminological.rjava.RDataType;
import uk.co.terminological.rjava.utils.RObjectVisitor;

/**
 * A java representation of the R base Date class (will also be used for POSIXt classes)
 *
 * @author terminological
 * @version $Id: $Id
 */
@RDataType(
		JavaToR = {
				"function(jObj) {"
				+"	fs::path(rJava::.jcall(jObj,returnSig='Ljava/lang/String;',method='rPrimitive'))"
				+"}",
		},
		RtoJava = {
				"function(rObj) {",
				"	if (is.na(rObj)) return(rJava::.jnew('~RFILE~'))",
				"	if (length(rObj) > 1) stop('input too long')",
				"	if (!is.character(rObj)) stop('input must be a character representing a file path')",
				"	tmp = fs::path_abs(fs::path_expand(rObj),getwd())",
				"	return(rJava::.jnew('~RFILE~',tmp))",
				"}"
		}
		//JNIType = "D"
	)
public class RFile implements RPrimitive, JNIPrimitive  {

	private static final long serialVersionUID = RObject.datatypeVersion;
	
	static final String NA_VALUE = null;
	/** Constant <code>NA</code> */
	public static final RFile NA = new RFile(NA_VALUE);
	
	Path self;
		
	/**
	 * <p>get.</p>
	 *
	 * @return a {@link java.time.LocalDate} object
	 */
	@SuppressWarnings("unchecked")
	public Path get() {
		return self;
	}
	
	/**
	 * <p>Constructor for RFile.</p>
	 *
	 * @param value a {@link java.lang.String} object
	 */
	public RFile(String value) {
		if (value == null) self=null;
		else {
			self = Paths.get(value).toAbsolutePath();
		}
	}
	
	public FileOutputStream getForWriting() throws IOException {
		Files.createDirectories(self.getParent());
		return new FileOutputStream(self.toFile());
	}
	
	/**
	 * <p>Constructor for RFile.</p>
	 */
	public RFile() {
		self = null;
	}
	
	/**
	 * <p>Constructor for RFile.</p>
	 *
	 * @param boxed a {@link java.io.File} object
	 */
	public RFile(File boxed) {
		self = Paths.get(boxed.getAbsolutePath());
	}
	
	/**
	 * <p>Constructor for RFile.</p>
	 *
	 * @param boxed a {@link java.nio.file.Path} object
	 */
	public RFile(Path boxed) {
		self = boxed;
	}
	
	/**
	 * <p>from.</p>
	 *
	 * @param p a {@link java.lang.String} object
	 * @return a {@link uk.co.terminological.rjava.types.RFile} object
	 */
	public static RFile from(Path p) {
		return new RFile(p);
	}

	/** {@inheritDoc} */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((self == null) ? 0 : self.hashCode());
		return result;
	}

	/** {@inheritDoc} */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		RFile other = (RFile) obj;
		if (self == null) {
			if (other.self != null)
				return false;
		} else if (!self.equals(other.self))
			return false;
		return true;
	}
	
	/**
	 * <p>rPrimitive.</p>
	 *
	 * @return a {@link java.lang.String} object
	 */
	public String rPrimitive() {return self == null ? "NA" : self.toAbsolutePath().toString();} 
	
	/** {@inheritDoc} */
	@SuppressWarnings("unchecked")
	@Override
	public <X> Optional<X> opt(Class<X> type) {
		if (type.isInstance(this)) return Optional.ofNullable((X) this);
		if (type.isInstance(self)) return Optional.ofNullable((X) self);
		return Optional.empty();
	}
	
	/** {@inheritDoc} */
	@SuppressWarnings("unchecked")
	@Override
	public <X> X get(Class<X> type) throws ClassCastException {
		if (type.isInstance(this)) return (X) this;
		if (type.isInstance(self)) return (X) self;
		throw new ClassCastException("Can't convert to a "+type.getCanonicalName());
	}
	
	/**
	 * <p>toString.</p>
	 *
	 * @return a {@link java.lang.String} object
	 */
	public String toString() {return self==null ? "NA" : self.toAbsolutePath().toString();}
	
	/** {@inheritDoc} */
	@Override
	public String rCode() {
		return this.isNa() ? "NA": "fs::path('"+self+"')";
	}
	
	/** {@inheritDoc} */
	@Override
	public <X> X accept(RObjectVisitor<X> visitor) {return visitor.visit(this);}
	
	/**
	 * <p>isNa.</p>
	 *
	 * @return a boolean
	 */
	public boolean isNa() {return self == null;}
	
	/**
	 * <p>asCsv.</p>
	 *
	 * @return a {@link java.lang.String} object
	 */
	public String asCsv() {
		return toString();
	}
	
	/** {@inheritDoc} */
	@SuppressWarnings("unchecked")
	@Override
	public Optional<Path> opt() {return opt(Path.class);}
	
}
