package ch.usi.hse.db.repositories;

import javax.transaction.Transactional;

import ch.usi.hse.db.entities.Participant;

/**
 * Repository for accessing Participant Users persistence
 * 
 * @author robert.jans@usi.ch
 *
 */
@Transactional
public interface ParticipantRepository extends UserBaseRepository<Participant> {

}
