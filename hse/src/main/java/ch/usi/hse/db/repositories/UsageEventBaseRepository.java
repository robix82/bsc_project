package ch.usi.hse.db.repositories;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.NoRepositoryBean;

import ch.usi.hse.db.entities.Experiment;
import ch.usi.hse.db.entities.UsageEvent;

@NoRepositoryBean
public interface UsageEventBaseRepository<T extends UsageEvent>
	extends CrudRepository<T, Integer> {

	public T findById(int id);
	public List<T> findByExperiment(Experiment experiment);
	public List<T> findByGroupId(int groupId);
	public List<T> findByUserId(int userId);
	public boolean existsByExperiment(Experiment experiment);
	public boolean existsByGroupId(int groupId);
	public boolean existsByUserId(int userId);
}
