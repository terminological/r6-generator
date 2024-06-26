package uk.co.terminological.rjava.types;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InvalidClassException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import uk.co.terminological.rjava.utils.RObjectVisitor;

/**
 * <p>RObject interface.</p>
 *
 * @author vp22681
 * @version $Id: $Id
 */
public interface RObject extends Serializable {

	/** Constant <code>datatypeVersion=1L</code> */
	public static final long datatypeVersion = 1L;
	
	/**
	 * Derives the R code representation of this object. This is used for some objects
	 * as a wire serialisation ({@link uk.co.terminological.rjava.types.RList} and {@link uk.co.terminological.rjava.types.RNamedList}) to copy them accross to R.
	 * Other data types tend to use the raw primitives to copy.
	 *
	 * @return a string that can be parsed as R code.
	 */
	String rCode();
		
	
	/**
	 * <p>accept.</p>
	 *
	 * @param visitor a {@link uk.co.terminological.rjava.utils.RObjectVisitor} object
	 * @param <X> a X class
	 * @return a X object
	 */
	public <X> X accept(RObjectVisitor<X> visitor); 
	
	/**
	 * <p>writeRDS.</p>
	 *
	 * @param os a {@link java.io.FileOutputStream} object
	 * @throws java.io.IOException if any.
	 */
	public default void writeRDS(FileOutputStream os) throws IOException {
		ObjectOutputStream oos = new ObjectOutputStream(os);
		oos.writeObject(this);
		oos.flush();
		oos.close();
		os.close();
	}
	
	/**
	 * <p>readRDS.</p>
	 *
	 * @param clazz a {@link java.lang.Class} object
	 * @param is a {@link java.io.InputStream} object
	 * @param <X> a X class
	 * @return a X object
	 * @throws java.io.IOException if any.
	 */
	public static <X extends RObject> X readRDS(Class<X> clazz, InputStream is) throws IOException {
		ObjectInputStream ois = new ObjectInputStream(is);
		try {
			@SuppressWarnings("unchecked")
			X out = (X) ois.readObject();
			return out;
		} catch (ClassNotFoundException | ClassCastException e) {
			throw new IOException("Could not read class: "+clazz.getCanonicalName(),e);
		} catch (InvalidClassException e) {
			throw new IOException("An incompatible serialisation format is being used: "+clazz.getCanonicalName(),e);
		}
	}
		
}
