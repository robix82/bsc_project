package ch.usi.hse.db.repositories;

import javax.transaction.Transactional;

import ch.usi.hse.db.entities.DocClickEvent;

@Transactional
public interface DocClickEventRepository extends UsageEventBaseRepository<DocClickEvent> {

}
