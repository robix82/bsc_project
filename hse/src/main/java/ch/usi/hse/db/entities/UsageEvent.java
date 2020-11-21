package ch.usi.hse.db.entities;

import java.time.LocalDateTime;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.fasterxml.jackson.annotation.JsonIgnore;



@Entity(name="usage_event")
public class UsageEvent {

	public static enum Type {
		
		LOGIN,
		LOGOUT,
		QUERY,
		DOC_CLICK
	}
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="event_id")
	private Integer id;
	
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name="experiment_id")
	@JsonIgnore
	private Experiment experiment;
	
	@Column(name="timestamp")
	private LocalDateTime timestamp;
	
	@Column(name="user_id")
	private int userId;
	
	@Column(name="group_id")
	private int groupId;
	
	@Column(name="groupName")
	private String groupName;
	
	@Column(name="event_type")
	private Type eventType;
	
	@Column(name="content")
	private String content;
	
	public UsageEvent() {
		
		id = 0;
		timestamp = LocalDateTime.now();
	}
	
	public UsageEvent(Type eventType, Participant participant, String content) {
		
		id = 0;
		timestamp = LocalDateTime.now();
		this.eventType = eventType;
		this.userId = participant.getId();
		this.content = content;
		
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
	
	public LocalDateTime getTimeStamp() {
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
	
	public String getContent() {
		return content;
	}
	
	public void setContent(String content) {
		this.content = content;
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





















