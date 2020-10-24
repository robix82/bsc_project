package ch.usi.hse.db.entities;

import java.util.Set;

import javax.persistence.Entity;

/**
 * User implementation for administrators 
 * 
 * @author robert.jans@usi.ch
 *
 */
@Entity(name="administrator")
public class Administrator extends User {

	public Administrator() {
		
		super();
	}
	
	public Administrator(int id, String userName, String password, Set<Role> roles) {
		
		super(id, userName, password, roles);
	}
	
	public Administrator(String userName, String password) {
		
		super(userName, password);
	}
}
