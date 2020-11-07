package ch.usi.hse.exceptions;

public class NoSuchExperimentException extends NoSuchEntityException {

	/**
	 * default generated
	 */
	private static final long serialVersionUID = 1L;

	public NoSuchExperimentException(int id) {
		
		super("No Experiment with id " + id  + " found");
	}
}
