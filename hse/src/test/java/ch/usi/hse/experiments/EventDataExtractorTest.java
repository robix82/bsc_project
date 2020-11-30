package ch.usi.hse.experiments;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.MockitoAnnotations.initMocks;

import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mock;

import ch.usi.hse.db.entities.DocCollection;
import ch.usi.hse.db.entities.Experiment;
import ch.usi.hse.db.entities.Participant;
import ch.usi.hse.db.entities.QueryEvent;
import ch.usi.hse.db.entities.QueryStat;
import ch.usi.hse.db.entities.SessionEvent;
import ch.usi.hse.db.entities.TestGroup;
import ch.usi.hse.db.repositories.DocClickEventRepository;
import ch.usi.hse.db.repositories.QueryEventRepository;
import ch.usi.hse.db.repositories.UsageEventRepository;

@SuppressWarnings("unused")
public class EventDataExtractorTest {

	@Mock
	private  UsageEventRepository ueRepo; 
	
	@Mock
	private QueryEventRepository qeRepo;
	
	@Mock
	private DocClickEventRepository ceRepo;
	
	private EventDataExtractor extractor;
	
	private Experiment testExperiment;
	
	@BeforeEach
	public void setUp() {
		
		initMocks(this);
		
		extractor = new EventDataExtractor(ueRepo, qeRepo, ceRepo);
		
		// create entities for test experiment
		
		Participant p1 = new Participant("p1", "pwd");
		Participant p2 = new Participant("p2", "pwd");
		Participant p3 = new Participant("p3", "pwd");
		Participant p4 = new Participant("p4", "pwd");
		
		DocCollection c1 = new DocCollection("c1", "l1");
		DocCollection c2 = new DocCollection("c2", "l2");
		
		TestGroup g1 = new TestGroup("g1");
		TestGroup g2 = new TestGroup("g2");
		
		testExperiment = new Experiment("testExperiment");
		
		// set unique ids
		
		p1.setId(1);
		p2.setId(2);
		p3.setId(3);
		p4.setId(4);
		
		c1.setId(1);
		c2.setId(2);
		
		g1.setId(1);
		g2.setId(2);
		
		// compose entities
		
		g1.addDocCollection(c1);
		g1.addDocCollection(c2);
		g2.addDocCollection(c1);
		
		g1.addParticipant(p1);
		g1.addParticipant(p2);
		g2.addParticipant(p3);
		g2.addParticipant(p4);
		
		testExperiment.addTestGroup(g1);
		testExperiment.addTestGroup(g2);

		// add UsageEvents
		
		QueryEvent qe1, que2, qu3, qe4;
		
		qe1 = new QueryEvent();
		qe1.setId(1);
		qe1.setUserId(p1.getId());
	
		QueryStat qe1Stat1 = new QueryStat(c1.getId(), c1.getName(), 10);
		qe1Stat1.setId(1);
		qe1Stat1.setQueryEvent(qe1);
		
		QueryStat qe1Stat2 = new QueryStat(c2.getId(), c2.getName(),20);
		qe1Stat1.setId(1);
		qe1Stat1.setQueryEvent(qe1);
		
		// p1 
		testExperiment.addUsageEvent(new SessionEvent(p1, SessionEvent.Event.LOGIN));
//		testExperiment.addUsageEvent(new QueryEvent(p1, "query1", resList1));
		
	}
}


















