package ch.usi.hse.exceptions;


public class ExperimentExistsException extends EntityExistsException {

	/**
	 * default generated
	 */
	private static final long serialVersionUID = 1L;

	public ExperimentExistsException(int id) {
		
		super("An experiment with id " + id + " already exists");
	}
	
	public ExperimentExistsException(String title) {
		
		super("An experiment with title " + title + " already exists");
	}
}
