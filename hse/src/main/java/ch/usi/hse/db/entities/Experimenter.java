package ch.usi.hse.db.entities;

import java.util.Set;

import javax.persistence.Entity;

/**
 * User implementation for experimenters
 * 
 * @author robert.jans@usi.ch
 *
 */
@Entity(name="experimenter")
public class Experimenter extends User {

	public Experimenter() {
		
		super();
	}
	
	public Experimenter(int id, String userName, String password, Set<Role> roles) {
		
		super(id, userName, password, roles);
	}
	
	public Experimenter(String userName, String password) {
		
		super(userName, password);
	}
}
