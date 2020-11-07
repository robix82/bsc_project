package ch.usi.hse.db.entities;

import java.util.Objects;

import javax.persistence.*;

/**
 * Stores a user's role: one of "ADMIN", "EXPERIMENTER" or "PARTICCIPANT"
 * 
 * @author robert.jans@usi.ch
 *
 */
@Entity(name="role")
public class Role {

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="role_id")
	private Integer id;
	
	@Column(name="role")
	private String role;
	
	public Role() {
		  
		id = 0;
		role = "";
	}
	
	public Role(int id, String role) {
		
		this.id = id;
		this.role = role;
	}
	
	public Role(String role) {
		
		id = 0;
		this.role = role;
	}
	
	public int getId() {
		return id;
	}
	
	public String getRole() {
		return role;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	public void setRole(String role) {
		this.role = role;
	}
	
	@Override
	public boolean equals(Object o) {
		
		if (o == this) {
			return true;
		}
		
		if (! (o instanceof Role)) {
			return false;
		}
		
		Role r = (Role) o;
		
		return r.role.equals(role);
	}
	
	@Override
	public int hashCode() {
		
		return Objects.hash(role);
	}
}





