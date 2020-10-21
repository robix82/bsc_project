package ch.usi.hse.db.repositories;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.NoRepositoryBean;

import ch.usi.hse.db.entities.User;

/**
 * Base for User repositories
 * 
 * @author robert.jans@usi.ch
 *
 * @param <T>
 */
@NoRepositoryBean
public interface UserBaseRepository<T extends User>  
	extends CrudRepository<T, Integer>{

	public T findById(int id);
	public T findByUserName(String userName);
}
