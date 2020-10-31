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
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import com.fasterxml.jackson.annotation.JsonIgnore;


/**
 * db entity representing test groups
 * 
 * @author robert.jans@usi.ch
 *
 */
@Entity(name="test_group")
public class TestGroup {

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	@Column(name="group_id")
	private Integer id;
	
	@Column(name="name")
	private String name;
	
	@OneToMany(mappedBy="testGroup", fetch=FetchType.EAGER, orphanRemoval=true, 
			cascade=CascadeType.ALL)
	private Set<Participant> participants;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="exp_id")
	@JsonIgnore
	private Experiment experiment;
	
	@ManyToMany
	@JoinTable(name="test_group_doc_collection", joinColumns=@JoinColumn(name="test_group_id"), 
	   		   inverseJoinColumns=@JoinColumn(name="doc_collection_id"))
	public Set<DocCollection> docCollections;
	
	@Column(name="experiment_id")
	private int experimentId;
	
	@Column(name="experiment_title")
	private String experimentTitle;
	
	public TestGroup() {
		
		id = 0;
		participants = new HashSet<>();
		docCollections = new HashSet<>();
	}
	
	public TestGroup(int id, String name, Set<Participant> participants, Experiment experiment) {
		
		this.id = id; 
		this.name = name; 

		setParticipants(participants);
		setExperiment(experiment);
		docCollections = new HashSet<>();
	}
	
	public TestGroup(String name) {
		
		id = 0;
		this.name = name;
		this.participants = new HashSet<>();
		docCollections = new HashSet<>();
	}
	
	public int getId() {
		return id;
	}
	
	public String getName() {
		return name;
	}
	
	public Experiment getExperiment() {
		return experiment;
	}
	
	public Set<Participant> getParticipants() {
		return participants;
	}
	
	public int getExperimentId() {
		return experimentId;
	}
	
	public String getExperimentTitle() {
		return experimentTitle;
	}
	
	public Set<DocCollection> getDocCollections() {
		return docCollections;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public void setExperiment(Experiment experiment) {
		
		this.experiment = experiment;
		
		if (experiment != null) {
			
			experimentId = experiment.getId();
			experimentTitle = experiment.getTitle();
			
			for (Participant p : participants) {
				
				p.setExperimentId(experiment.getId());
				p.setExperimentTitle(experiment.getTitle());
			}
		}
	}
	
	public void setParticipants(Set<Participant> participants) {
		
		for (Participant p : participants) {
			
			p.setTestGroup(this);
		}
		
		this.participants = participants;
	}
	
	public void setExperimentId(int experimentId) {
		
		this.experimentId = experimentId;
		
		for (Participant p : participants) {
			
			p.setExperimentId(experimentId);
		}
	}
	
	public void setExperimentTitle(String experimentTitle) {
		
		this.experimentTitle = experimentTitle;
		
		for (Participant p : participants) {
			p.setExperimentTitle(experimentTitle);
		}
	}
	
	public void addParticipant(Participant p) {
		
		p.setTestGroup(this);
		participants.add(p);
	}
	
	public void removeParticipant(Participant p) {
		
		participants.remove(p);
	}
	
	public void clearParticipants() {
		
		participants.clear();
	}
	
	public void setDocCollections(Set<DocCollection> docCollections) {
		this.docCollections = docCollections;
	}
	
	public void addDocCollection(DocCollection c) {
		
		docCollections.add(c);
	}
	
	public void removeDocCollection(DocCollection c) {
		
		docCollections.remove(c);
	}
	
	public void clearDocCollections() {
		
		docCollections.clear();
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












