package ch.usi.hse.exceptions;

public class TestGroupExistsException extends EntityExistsException {

	/**
	 * default generated
	 */
	private static final long serialVersionUID = 1L;

	public TestGroupExistsException(int id) {
		
		super("A TestGroup with id " + id + " already exists"); 
	}
	
	public TestGroupExistsException(String name) {
		
		super("A TestGroup with name " + name + " already exists");
	}
}
