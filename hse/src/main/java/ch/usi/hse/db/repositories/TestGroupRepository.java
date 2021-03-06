package ch.usi.hse.db.repositories;

import org.springframework.stereotype.Repository;

import ch.usi.hse.db.entities.TestGroup;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

/**
 * Repository for accessing TestGroup persistence
 * 
 * @author robert.jans@usi.ch
 *
 */
@Repository
public interface TestGroupRepository extends CrudRepository<TestGroup, Integer> {

	public List<TestGroup> findAll();
	public TestGroup findById(int id);
	public boolean existsByNameAndExperimentId(String name, int experimentId);
}
