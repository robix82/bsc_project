package ch.usi.hse.db.entities;

import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;


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
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "group_id")
	private TestGroup testGroup;
	
	
	public Participant() {
		
		super();
	}
	
	public Participant(int id, String userName, String password, Set<Role> roles, 
			           int experimentId, TestGroup testGroup) {
		
		super(id, userName, password, roles);
		
		this.experimentId = experimentId;
		this.testGroup = testGroup;
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
	
	public void setExperimentId(int experimentId) {
		this.experimentId = experimentId;
	}
	
	public TestGroup getTestGroup() {
		return testGroup;
	}

	public void setTestGroup(TestGroup testGroup) {
		this.testGroup = testGroup;
	}
}





