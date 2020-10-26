package ch.usi.hse.db.repositories;

import java.util.List;

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

	public List<Participant> findByExperimentId(int experimentId);
	public List<Participant> findByGroupId(int groupId);
	public List<Participant> findByGroupName(String groupName);
}
