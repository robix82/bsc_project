package ch.usi.hse.db.repositories;

import javax.transaction.Transactional;

import ch.usi.hse.db.entities.Experimenter;

/**
 * Repository for accessing Experimenter Users persistence
 * 
 * @author robert.jans@usi.ch
 *
 */
@Transactional
public interface ExperimenterRepository extends UserBaseRepository<Experimenter> {

}
