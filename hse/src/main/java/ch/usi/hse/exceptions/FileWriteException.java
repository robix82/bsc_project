package ch.usi.hse.exceptions;

/**
 * Exception thrown when a file write operation fails
 * 
 * @author robert.jans@usi.ch
 *
 */
public class FileWriteException extends Exception {

	/**
	 * default generatd
	 */
	private static final long serialVersionUID = 1L;

	public FileWriteException(String fileName) {
		
		super("Error writing file " + fileName);
	}
}
