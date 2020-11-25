package ch.usi.hse.db.entities;

import java.time.LocalDateTime;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.fasterxml.jackson.annotation.JsonIgnore;



@Entity(name="usage_event")
@Inheritance(strategy=InheritanceType.JOINED)
public class UsageEvent {

	public static enum Type {
		
		SESSION,
		QUERY,
		DOC_CLICK
	}
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="event_id")
	protected Integer id;
	
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name="experiment_id")
	@JsonIgnore
	protected Experiment experiment;
	
	@Column(name="timestamp")
	protected LocalDateTime timestamp;
	
	@Column(name="user_id")
	protected int userId;
	
	@Column(name="group_id")
	protected int groupId;
	
	@Column(name="groupName")
	protected String groupName;
	
	@Column(name="event_type")
	protected Type eventType;
	
	public UsageEvent() {
		
		id = 0;
		timestamp = LocalDateTime.now();
	}
	
	public UsageEvent(Type eventType, Participant participant) {
		
		id = 0;
		timestamp = LocalDateTime.now();
		this.eventType = eventType;
		this.userId = participant.getId();
		
		TestGroup group = participant.getTestGroup();
		
		if (group != null) {
			
			groupId = group.getId();
			groupName = group.getName();
		}
	}
	
	public int getId() {
		return id;
	}
	
	public void setId(int id) {
		
		this.id = id;
	}
	
	public Experiment getExperiment() {
		return experiment;
	}
	
	public void setExperiment(Experiment experiment) {
		this.experiment = experiment;
	}
	
	public LocalDateTime getTimestamp() {
		return timestamp;
	}
	
	public void setTimestamp(LocalDateTime timestamp) {
		this.timestamp = timestamp;
	}
	
	public int getUserId() {
		return userId;
	}
	
	public void setUserId(int userId) {
		this.userId = userId;
	}
	
	public int getGroupId() {
		return groupId;
	}
	
	public void ssetGroupId(int groupId) {
		this.groupId = groupId;
	}
	
	public String getGroupName() {
		return groupName;
	}
	
	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}
	
	public Type getEventType() {
		return eventType;
	}
	
	public void setEventType(Type eventType) {
		this.eventType = eventType;
	}
	
	@Override
	public boolean equals(Object o) {
		
		if (o == this) {
			return true;
		}
		
		if (! (o instanceof UsageEvent)) {
			return false;
		}
		
		UsageEvent e = (UsageEvent) o;
		
		return e.id.equals(id);
	}
	
	@Override
	public int hashCode() {
		
		return Objects.hash(id);
	}
}





















