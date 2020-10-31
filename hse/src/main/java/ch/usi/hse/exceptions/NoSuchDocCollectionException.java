package ch.usi.hse.exceptions;

public class NoSuchDocCollectionException extends Exception {

	/**
	 * default generated
	 */
	private static final long serialVersionUID = 1L;

	public NoSuchDocCollectionException(int id) {
		
		super("No DocCollection with id " + id + " found");
	}
}
