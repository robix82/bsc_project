package ch.usi.hse.db.entities;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

/**
 * db entity representing experiments
 * 
 * @author robert.jans@usi.ch
 *
 */
@Entity(name="experiment")
public class Experiment {

	public static enum Status {
		
		NOT_READY,
		READY,
		RUNNING,
		COMPLETE
	} 
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="experiment_id")
	private Integer id;
	
	@Column(name="title")
	private String title;
	
	@OneToMany(mappedBy="experiment", fetch=FetchType.EAGER, orphanRemoval=true, cascade=CascadeType.ALL)
	private Set<TestGroup> testGroups;
	
	@Column(name="status")
	private Status status;
	 
	public Experiment() {
		
		id = 0; 
		testGroups = new HashSet<>();
		status = Status.NOT_READY;
	} 
	
	public Experiment(int id, String title, Set<TestGroup> testGroups) {
		
		this.id = id;
		this.title = title;
		setTestGroups(testGroups);
		status = Status.NOT_READY;
	}
	
	public Experiment(String title) {
		
		id = 0;
		testGroups = new HashSet<>();
		this.title = title;
		status = Status.NOT_READY;
	}
	
	public int getId() {
		return id;
	}
	
	public String getTitle() {
		return title;
	}
	
	public Set<TestGroup> getTestGroups() {
		return testGroups;
	}
	
	public Status getStatus() {
		return status;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	public void setTitle(String title) {
		this.title = title;
	}
	
	public void setTestGroups(Set<TestGroup> testGroups) {
		
		if (testGroups != null) {
			
			for (TestGroup g : testGroups) {
				
				g.setExperiment(this);
			}
		}
		
		this.testGroups = testGroups;
	}
	
	public void setStatus(Status status) {
		this.status = status;
	}
	
	public void addTestGroup(TestGroup group) {
		
		group.setExperiment(this);
		testGroups.add(group);
	}
	
	public void removeTestGroup(TestGroup group) {
		
		testGroups.remove(group);
	}
	
	public void removeTestGroup(String groupName) {
		
		TestGroup toRemove = null;
		
		for (TestGroup g : testGroups) {
			if (g.getName().equals(groupName)) {
				
				toRemove = g;
				break;
			}
		}
		
		if (toRemove != null) {
			testGroups.remove(toRemove);
		}
	}
	
	public void clearTestGroups() {
		
		testGroups.clear();
	}
	
	@Override
	public boolean equals(Object o) {
		
		if (o == this) {
			return true;
		}
		
		if (! (o instanceof Experiment)) {
			return false;
		}
		
		Experiment e = (Experiment) o;
		
		return e.id.equals(id);
	}
	
	@Override
	public int hashCode() {
		
		return Objects.hash(id);
	}
}








