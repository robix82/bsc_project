package ch.usi.hse.db.repositories;

import javax.transaction.Transactional;

import ch.usi.hse.db.entities.SessionEvent;

@Transactional
public interface SessionEventRepository extends UsageEventBaseRepository<SessionEvent> {

}
