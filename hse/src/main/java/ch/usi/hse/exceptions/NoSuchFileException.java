package ch.usi.hse.exceptions;

/**
 * Exception thrown on requests to read a non-existing file
 * 
 * @author robert.jans@usi.ch
 *
 */
public class NoSuchFileException extends Exception {

	/**
	 * default generated
	 */
	private static final long serialVersionUID = 1L;

	public NoSuchFileException(String fileName) {
		
		super("File " + fileName + " not found");
	}
}
