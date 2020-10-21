package ch.usi.hse.db.repositories;

import javax.transaction.Transactional;

import ch.usi.hse.db.entities.Administrator;

/**
 * Repository for accessing Administrator Users persistence
 * 
 * @author robert.jans@usi.ch
 *
 */
@Transactional
public interface AdministratorRepository extends UserBaseRepository<Administrator> {

}
