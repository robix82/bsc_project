package ch.usi.hse.exceptions;

/**
 * Exception thrown when a file delete operation fails
 * 
 * @author robert.jans@usi.ch
 *
 */
public class FileDeleteException extends Exception {

	/**
	 * generated default
	 */
	private static final long serialVersionUID = 1L;

	public FileDeleteException(String fileName) {
		
		super("Error deleting file " + fileName);
	}
}
