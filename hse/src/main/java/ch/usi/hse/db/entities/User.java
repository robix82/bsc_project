package ch.usi.hse.db.entities;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import javax.persistence.*;


/**
 * Base class representing users for security/authentication
 * Planned subclasses: Administrator, Experimenter, Participant
 * 
 * @author robert.jans@usi.ch
 *
 */
@Entity(name="user")
@Inheritance(strategy=InheritanceType.JOINED)
public abstract class User {

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="user_id")
	protected Integer id;
	
	@Column(name="user_name", unique=true)
	protected String userName;
	
	@Column(name="password")
	protected String password;
	
	@Column(name="active")
	protected boolean active;

	@ManyToMany(cascade=CascadeType.MERGE)
	@JoinTable(name="user_role", joinColumns=@JoinColumn(name="user_id"), 
			   inverseJoinColumns=@JoinColumn(name="role_id"))
	protected Set<Role> roles;
	
	protected User() {
		 
		id = 0;
		userName = "";
		password = "";
		active = true;
		roles = new HashSet<>();
	}
	
	protected User(int id, String userName, String password, Set<Role> roles) {
		
		this.id = id;
		this.userName = userName;
		this.password = password;
		active = true; 
		this.roles = roles;
	}
	
	public User(String userName, String password) {
		
		id = 0;
		this.userName = userName;
		this.password = password;
		active = true;
		roles = new HashSet<>();
	}

	public int getId() {
		return id;
	}
	
	public String getUserName() {
		return userName;
	}
	
	public String getPassword() {
		return password;
	}
	
	public boolean getActive() {
		return active;
	}
	
	public Set<Role> getRoles() {
		return roles;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	public void setUserName(String userName) {
		this.userName = userName;
	}
	
	public void setPassword(String password) {
		this.password = password;
	}
	
	public void setActive(boolean active) {
		this.active = active;
	}
	
	public void setRoles(Set<Role> roles) {
		this.roles = roles;
	}
	
	public void addRole(Role role) {
		roles.add(role);
	}
	
	public void removeRole(Role role) {
		roles.remove(role);
	}
	
	@Override
	public boolean equals(Object o) {
		
		if (o == this) {
			return true;
		}
		
		if (! (o instanceof User)) {
			return false;
		}
		
		User u = (User) o;
		
		return u.userName.equals(userName);
	}
	
	@Override
	public int hashCode() {
		
		return Objects.hash(userName);
	}
}









