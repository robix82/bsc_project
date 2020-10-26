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
 * db entity representing test groups
 * 
 * @author robert.jans@usi.ch
 *
 */
@Entity(name="group")
public class TestGroup {

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="group_id")
	private Integer id;
	
	@Column(name="name")
	private String name;
	
	@OneToMany(mappedBy="group_id", fetch=FetchType.LAZY, cascade=CascadeType.ALL)
	private Set<Participant> participants;
	
	public TestGroup() {
		
		id = 0;
		participants = new HashSet<>();
	}
	
	public TestGroup(int id, String name, Set<Participant> participants) {
		
		this.id = id;
		this.name = name;
		this.participants = participants;
	}
	
	public TestGroup(String name, Set<Participant> participants) {
		
		id = 0;
		this.name = name;
		this.participants = participants;
	}
	
	public TestGroup(String name) {
		
		id = 0;
		this.name = name;
		this.participants = new HashSet<>();
	}
	
	public int getId() {
		return id;
	}
	
	public String getName() {
		return name;
	}
	
	public Set<Participant> getParticipants() {
		return participants;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public void setParticipants(Set<Participant> participants) {
		this.participants = participants;
	}
	
	public void addParticipant(Participant p) {
		
		participants.add(p);
	}
	
	public void removeParticipant(Participant p) {
		
		participants.remove(p);
	}
	
	public void clearParticipants() {
		
		participants.clear();
	}
	
	@Override
	public boolean equals(Object o) {
		
		if (o == this) {
			return true;
		}
		
		if (! (o instanceof TestGroup)) {
			return false;
		}
		
		TestGroup g = (TestGroup) o;
		
		return g.id.equals(id);
	}
	
	@Override
	public int hashCode() {
		
		return Objects.hash(id);
	}
}












