package ch.usi.hse.db.repositories;

import javax.transaction.Transactional;

import ch.usi.hse.db.entities.HseUser;

/**
 * Repository for accessing User persintence via base class 
 * 
 * @author robert.jans@usi.ch
 *
 */
@Transactional
public interface UserRepository extends UserBaseRepository<HseUser> {


}
