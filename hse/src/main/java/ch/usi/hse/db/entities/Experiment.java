package ch.usi.hse.db.entities;

import java.time.Duration;
import java.time.LocalDateTime;
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
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import com.fasterxml.jackson.annotation.JsonIgnore;

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
	
	@OneToMany(mappedBy="experiment", fetch=FetchType.EAGER, orphanRemoval=true, cascade=CascadeType.ALL)
	private Set<UsageEvent> usageEvents;
	
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name="experimenter_id")
	@JsonIgnore
	private Experimenter experimenter;
	
	@Column(name="_experimenter_id")
	private int experimenterId;
	
	@Column(name="experimenter_name")
	private String experimenterName;
	
	@Column(name="status") 
	private Status status;
	 
	@Column(name="date_creaed")
	private LocalDateTime dateCreated; 
	
	@Column(name="date_conducted")
	private LocalDateTime dateConducted; 
	
	@Column(name="t_start")
	private LocalDateTime startTime;
	
	@Column(name="t_end")
	private LocalDateTime endTime;
	
	@Column(name="duration")
	private Duration duration;
	 
	public Experiment() {
		
		id = 0; 
		testGroups = new HashSet<>();
		status = Status.NOT_READY;
		startTime = LocalDateTime.of(2000, 1, 1, 0, 0);
		endTime = LocalDateTime.of(2000, 1, 1, 0, 0);
		duration = Duration.ofMillis(0);
	} 
	
	public Experiment(int id, String title, Set<TestGroup> testGroups) {
		
		this.id = id;
		this.title = title;
		setTestGroups(testGroups);
		status = Status.NOT_READY;
		startTime = LocalDateTime.of(2000, 1, 1, 0, 0);
		endTime = LocalDateTime.of(2000, 1, 1, 0, 0);
		duration = Duration.ofMillis(0);
	}
	
	public Experiment(String title) {
		
		id = 0;
		testGroups = new HashSet<>();
		this.title = title;
		status = Status.NOT_READY;
		startTime = LocalDateTime.of(2000, 1, 1, 0, 0);
		endTime = LocalDateTime.of(2000, 1, 1, 0, 0);
		duration = Duration.ofMillis(0);
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
	
	public Set<UsageEvent> getUsageEvents() {
		return usageEvents;
	}
	
	public Experimenter getExperimenter() {
		return experimenter;
	}
	
	public int getExperimenterId() {
		return experimenterId;
	}
	
	public String getExperimenterName() {
		return experimenterName;
	}
	
	public Status getStatus() {
		return status;
	}
	
	public LocalDateTime getDateCreated() {
		return dateCreated;
	}
	
	public LocalDateTime getDateConducted() {
		return dateConducted;
	}
	
	public LocalDateTime getStartTime() {
		return startTime;
	}
	
	public LocalDateTime getEndTime() {
		return endTime;
	}
	
	public Duration getDuration() {
		return duration;
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
	
	public void setUsageEvents(Set<UsageEvent> usageEvents) {
		
		if (usageEvents != null) {
			
			for (UsageEvent e : usageEvents) {
				
				e.setExperiment(this);
			}
		}
		
		this.usageEvents = usageEvents;
	}
	
	public void setExperimenter(Experimenter experimenter) {
		
		this.experimenter = experimenter;
		
		
		if (experimenter != null) {
			
			experimenterId = experimenter.getId();
			experimenterName = experimenter.getUserName();
		}
		else {
			
			experimenterId = 0;
			experimenterName = null;
		}
	}
	
	public void setExperimenterId(int experimenterId) {
		this.experimenterId = experimenterId;
	}
	
	public void setExperimenterName(String experimenterName) {
		this.experimenterName = experimenterName;
	}
	
	public void setStatus(Status status) {
		this.status = status;
	}
	
	public void setDateCreated(LocalDateTime dateCreated) {
		this.dateCreated = dateCreated;
	}
	
	public void setDateConducted(LocalDateTime dateConducted) {
		this.dateConducted = dateConducted;
	}
	
	public void setStartTime(LocalDateTime startTime) {
		this.startTime = startTime;
	}
	
	public void setEndTime(LocalDateTime endTime) {
		
		this.endTime = endTime;
		
		if (startTime != null) {
			
			duration = Duration.between(startTime, endTime);
		}
	}
	
	public void setDuration(Duration duration) {
		this.duration = duration;
	}
	
	public void addTestGroup(TestGroup group) {
		
		group.setExperiment(this);
		testGroups.add(group);
	}
	
	public void removeTestGroup(TestGroup group) {
		
		testGroups.remove(group);
	}

	public void clearTestGroups() {
		
		testGroups.clear();
	}
	
	public void addUsageEvent(UsageEvent usageEvent) {
		
		usageEvent.setExperiment(this);
		usageEvents.add(usageEvent);
	}
	
	public void removeUsageEvent(UsageEvent usageEvent) {
		usageEvents.remove(usageEvent);
	}
	
	public void clearUsageEvents() {
		
		usageEvents.clear();
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








