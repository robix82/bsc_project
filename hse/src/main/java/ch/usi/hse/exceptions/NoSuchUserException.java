package ch.usi.hse.exceptions;

/**
 * Exception thrown on attempts to retrieve a User
 * by a non existing id id user name
 * 
 * @author robert.jans@usi.ch
 *
 */
public class NoSuchUserException extends NoSuchEntityException {
	
	// default generated
	private static final long serialVersionUID = 1L;

	public NoSuchUserException(int id) {
		
		super("No User with id " + id + " found");
	}
	
	public NoSuchUserException(String userName) {
		
		super("No User named " + userName + " found");
	}
	
	public NoSuchUserException(String role, int id) {
		
		super("No " + role + " with id " + id + " found");
	}
	
	public NoSuchUserException(String role, String userName) {
		
		super("No " + role + " named " + userName + " found");
	}
}








