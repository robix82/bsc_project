package ch.usi.hse.exceptions;

public class NoSuchTestGroupException extends NoSuchEntityException {

	/**
	 * default generated
	 */
	private static final long serialVersionUID = 1L;

	public NoSuchTestGroupException(int id) {
		
		super("No TestGroup with id " + id + " found");
	}
}
