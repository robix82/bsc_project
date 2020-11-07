package ch.usi.hse.exceptions;

/**
 * Exception thrown on attempts to create a User 
 * with an already existing user name or id
 * 
 * @author robert.jans@usi.ch
 *
 */
public class UserExistsException extends EntityExistsException {

	// default generated
	private static final long serialVersionUID = 1L;

	public UserExistsException(int id) {
		
		super("A User with id " + id + " already exists");
	}
	
	public UserExistsException(String userName) {
		
		super("A User named " + userName + " already exists");
	}
}
