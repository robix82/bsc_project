package ch.usi.hse.db.entities;

import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.fasterxml.jackson.annotation.JsonIgnore;


/**
 * User implementation for participants 
 * 
 * @author robert.jans@usi.ch
 *
 */
@Entity(name="participant")
public class Participant extends User {

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "group_id")
	@JsonIgnore
	private TestGroup testGroup;
	 
	@Column(name="experiment_id")
	private int experimentId;
	
	@Column(name="test_group_id")
	private int testGroupId;
	
	@Column(name="group_name")
	private String testGroupName;
	
	@Column(name="experimentTitle")
	private String experimentTitle;
	
	public Participant() {
		
		super();
	}
	
	public Participant(int id, String userName, String password, 
					   Set<Role> roles, TestGroup testGroup) {
		
		super(id, userName, password, roles);
		
		setTestGroup(testGroup);
	}
	
	public Participant(String userName, String password) {
		
		super(userName, password);
	} 
	
	public TestGroup getTestGroup() {
		return testGroup;
	}

	public void setTestGroup(TestGroup testGroup) {
		
		this.testGroup = testGroup;
		
		if (testGroup != null) {
			
			testGroupId = testGroup.getId();
			testGroupName = testGroup.getName();
			
			Experiment experiment = testGroup.getExperiment();
			
			if (experiment != null) {
				
				experimentId = experiment.getId();
				experimentTitle = experiment.getTitle();
	 		}
		}
	}
	
	public int getExperimentId() {
		return experimentId;
	}
	
	public String getExperimentTitle() {
		return experimentTitle;
	}
	
	public int getTestGroupId() { 
		return testGroupId;
	}
	
	public String getTestGroupName() {
		return testGroupName;
	}
	
	public void setExperimentId(int experimentId) {
		this.experimentId = experimentId;
	}
	
	public void setExperimentTitle(String experimentTitle) {
		this.experimentTitle = experimentTitle;
	}
	
	public void setTestGroupId(int testGroupId) {
		this.testGroupId = testGroupId;
	}
	
	public void setTestGroupName(String testGroupName) {
		this.testGroupName = testGroupName;
	}
}
















