package ch.usi.hse.db.repositories;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import ch.usi.hse.db.entities.Experiment;

@Repository
public interface ExperimentRepository extends CrudRepository<Experiment, Integer> {

	public List<Experiment> findAll();
	public Experiment findById(int id);
}
