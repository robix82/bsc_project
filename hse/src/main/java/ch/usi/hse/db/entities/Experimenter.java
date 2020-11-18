package ch.usi.hse.db.entities;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;

/**
 * User implementation for experimenters
 * 
 * @author robert.jans@usi.ch
 *
 */
@Entity(name="experimenter")
public class Experimenter extends HseUser {

	@OneToMany(mappedBy="experimenter", fetch=FetchType.EAGER, orphanRemoval=true, cascade=CascadeType.ALL)
	private Set<Experiment> experiments;
	
	public Experimenter() {
		
		super();
		experiments = new HashSet<>();
	}
	
	public Experimenter(int id, String userName, String password, Set<Role> roles) {
		
		super(id, userName, password, roles);
		experiments = new HashSet<>();
	}
	
	public Experimenter(String userName, String password) {
		
		super(userName, password);
		experiments = new HashSet<>();
	}
	
	public Set<Experiment> getExperiments() {
		return experiments;
	}
	
	public void setExperiments(Set<Experiment> experiments) {
		
		if (experiments != null) {

			for (Experiment e : experiments) {
				
				e.setExperimenter(this);
			}
		}
		
		this.experiments = experiments;
	}
	
	public void addExperiment(Experiment e) {
		
		e.setExperimenter(this);
		
		experiments.add(e);
	}
	
	public void removeExperiment(Experiment e) {
		
		experiments.remove(e);
	}
}








