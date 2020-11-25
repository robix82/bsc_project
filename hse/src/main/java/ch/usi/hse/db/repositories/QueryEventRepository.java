package ch.usi.hse.db.repositories;

import javax.transaction.Transactional;

import ch.usi.hse.db.entities.QueryEvent;

@Transactional
public interface QueryEventRepository extends UsageEventBaseRepository<QueryEvent> {

}
