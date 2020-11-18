package ch.usi.hse.db.repositories;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.NoRepositoryBean;

import ch.usi.hse.db.entities.HseUser;

/**
 * Base for User repositories
 * 
 * @author robert.jans@usi.ch
 *
 * @param <T>
 */
@NoRepositoryBean
public interface UserBaseRepository<T extends HseUser>  
	extends CrudRepository<T, Integer>{

	public T findById(int id);
	public T findByUserName(String userName);
	public List<T> findAll();
	public boolean existsById(int id);
	public boolean existsByUserName(String userName); 
}
