package ch.usi.hse.exceptions;

public class DocCollectionExistsException extends EntityExistsException {

	/**
	 * default generated
	 */
	private static final long serialVersionUID = 1L;

	public DocCollectionExistsException(int id) {
		
		super("A DocCollection with id " + id + " already exists");
	}
	
	public DocCollectionExistsException(String name) {
		
		super("A DocCollection with name " + name + " already exists");
	}
}
