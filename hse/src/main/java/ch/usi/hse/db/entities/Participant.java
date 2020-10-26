package ch.usi.hse.db.entities;

import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;

/**
 * User implementation for participants 
 * 
 * @author robert.jans@usi.ch
 *
 */
@Entity(name="participant")
public class Participant extends User {

	@Column(name="experiment_id")
	private int experimentId;
	
	@Column(name="group_id")
	private int groupId;
	
	@Column(name="group_name")
	private String groupName;
	
	public Participant() {
		
		super();
	}
	
	public Participant(int id, String userName, String password, Set<Role> roles, 
			           int experimentId, int groupId, String groupName) {
		
		super(id, userName, password, roles);
		
		this.experimentId = experimentId;
		this.groupId = groupId;
		this.groupName = groupName;
	}
	
	public Participant(int id, String userName, String password, Set<Role> roles) {

		super(id, userName, password, roles);
	}
	
	public Participant(String userName, String password) {
		
		super(userName, password);
	}
	
	public int getExperimentId() {
		return experimentId;
	}
	 
	public int getGroupId() {
		return groupId;
	}
	
	public String getGroupName() {
		return groupName;
	}
	
	public void setExperimentId(int experimentId) {
		this.experimentId = experimentId;
	}
	
	public void setGroupId(int groupId) {
		this.groupId = groupId;
	}
	
	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}
}





