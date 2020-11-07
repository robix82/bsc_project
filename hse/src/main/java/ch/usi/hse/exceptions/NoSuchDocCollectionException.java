package ch.usi.hse.exceptions;

public class NoSuchDocCollectionException extends NoSuchEntityException {

	/**
	 * default generated
	 */
	private static final long serialVersionUID = 1L;

	public NoSuchDocCollectionException(int id) {
		
		super("No DocCollection with id " + id + " found");
	}
}
