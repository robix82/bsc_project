package ch.usi.hse.exceptions;

import ch.usi.hse.db.entities.Experiment.Status;

public class ExperimentStatusException extends Exception {

	/**
	 * default generated
	 */
	private static final long serialVersionUID = 1L;

	public ExperimentStatusException(Status expected, Status actual) {
		
		super("expecting " + expected + " but is " + actual);
	}
}
