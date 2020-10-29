package ch.usi.hse.exceptions;

/**
 * Exception thrown when a file read operation fails
 * 
 * @author robert.jans@usi.ch
 *
 */
public class FileReadException extends Exception {

	/**
	 * default generated
	 */
	private static final long serialVersionUID = 1L;

	public FileReadException(String fName) {
		
		super("Error reading file " + fName);
	}
}
