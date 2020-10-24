package ch.usi.hse.db.entities;

import java.util.Set;

import javax.persistence.Entity;

/**
 * User implementation for participants 
 * 
 * @author robert.jans@usi.ch
 *
 */
@Entity(name="participant")
public class Participant extends User {

	public Participant() {
		
		super();
	}
	
	public Participant(int id, String userName, String password, Set<Role> roles) {
		
		super(id, userName, password, roles);
	}
	
	public Participant(String userName, String password) {
		
		super(userName, password);
	}
}
