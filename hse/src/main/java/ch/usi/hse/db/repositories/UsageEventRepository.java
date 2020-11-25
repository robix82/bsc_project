package ch.usi.hse.db.repositories;

import javax.transaction.Transactional;

import ch.usi.hse.db.entities.UsageEvent;

@Transactional
public interface UsageEventRepository extends UsageEventBaseRepository<UsageEvent> {

}
