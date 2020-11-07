package ch.usi.hse.db.repositories;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import ch.usi.hse.db.entities.Experiment;
import ch.usi.hse.db.entities.Experimenter;

/**
 * Repository for accessing Participant Experiments persistence
 * 
 * @author robert.jans@usi.ch
 *
 */
@Repository
public interface ExperimentRepository extends CrudRepository<Experiment, Integer> {

	public List<Experiment> findAll();
	public Experiment findById(int id);
	public List<Experiment> findByExperimenter(Experimenter experimenter);
}
