package ch.usi.hse.db.repositories;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import ch.usi.hse.db.entities.DocCollection;

@Repository
public interface DocCollectionRepository extends CrudRepository<DocCollection, Integer> {

	public DocCollection findById(int id);
	public List<DocCollection> findAll();
	public boolean existsByName(String name);
	public List<DocCollection> findByIndexed(boolean indexed);
}
