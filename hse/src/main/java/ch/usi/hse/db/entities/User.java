package ch.usi.hse.db.entities;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Inheritance(strategy=InheritanceType.JOINED)
public abstract class User {

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="user_id")
	protected Integer id;
	
	@Column(name="user_name")
	protected String userName;
	
	@Column(name="password")
	protected String password;
	
	@JsonIgnore
	@ManyToMany(cascade=CascadeType.MERGE)
	@JoinTable(name="user_role", joinColumns=@JoinColumn(name="user_id"), 
			   inverseJoinColumns=@JoinColumn(name="role_id"))
	protected Set<Role> roles;
	
	protected User() {
		
		id = 0;
		userName = "";
		password = "";
		roles = new HashSet<>();
	}
	
	protected User(int id, String userName, String password, Set<Role> roles) {
		
		this.id = id;
		this.userName = userName;
		this.password = password;
		this.roles = roles;
	}
	
	public User(String userName, String password, Set<Role> roles) {
		
		this.userName = userName;
		this.password = password;
		this.roles = roles;
	}
}









