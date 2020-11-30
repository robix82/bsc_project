package ch.usi.hse.experiments;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.ArrayList;
import java.util.Arrays;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import ch.usi.hse.db.entities.DocClickEvent;
import ch.usi.hse.db.entities.DocCollection;
import ch.usi.hse.db.entities.Experiment;
import ch.usi.hse.db.entities.Participant;
import ch.usi.hse.db.entities.QueryEvent;
import ch.usi.hse.db.entities.QueryStat;
import ch.usi.hse.db.entities.SessionEvent;
import ch.usi.hse.db.entities.TestGroup;
import ch.usi.hse.db.entities.UsageEvent;
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
	private Participant p1, p2, p3, p4;
	private DocCollection c1, c2;
	private TestGroup g1, g2;
	
	private List<QueryEvent> p1Queries, p2Queries, p3Queries, p4Queries, 
							 g1Queries, g2Queries, allQueries;
	
	private List<DocClickEvent> p1Clicks, p2Clicks, p3Clicks, p4Clicks,
							    g1Clicks, g2Clicks, allClicks;
	
	@BeforeEach
	public void setUp() {
		
		initMocks(this);
		
		extractor = new EventDataExtractor(ueRepo, qeRepo, ceRepo);
		
		// create entities for test experiment
		
		p1 = new Participant("p1", "pwd");
		p2 = new Participant("p2", "pwd");
		p3 = new Participant("p3", "pwd");
		p4 = new Participant("p4", "pwd");
		
		c1 = new DocCollection("c1", "l1");
		c2 = new DocCollection("c2", "l2");
		
		g1 = new TestGroup("g1");
		g2 = new TestGroup("g2");
		
		testExperiment = new Experiment("testExperiment");
		testExperiment.setId(1);
		
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

		//  QUERY EVENTS
		
		QueryEvent qe1, qe2, qe3, qe4, qe5;
		
		// qe1 (user p1)
		
		qe1 = new QueryEvent();
		qe1.setUserId(p1.getId());
	
		QueryStat qe1Stat1 = new QueryStat(c1.getId(), c1.getName(), 10);
		qe1Stat1.setId(1);
		qe1Stat1.setQueryEvent(qe1);
		
		QueryStat qe1Stat2 = new QueryStat(c2.getId(), c2.getName(),20);
		qe1Stat2.setId(2);
		qe1Stat2.setQueryEvent(qe1);
		
		qe1.setQueryStats(Set.of(qe1Stat1, qe1Stat2));
		
		// qe2 (user p1)
		
		qe2 = new QueryEvent();
		qe2.setUserId(p1.getId());
	
		QueryStat qe2Stat1 = new QueryStat(c1.getId(), c1.getName(), 10);
		qe2Stat1.setId(3);
		qe2Stat1.setQueryEvent(qe2);
		
		qe2.setQueryStats(Set.of(qe2Stat1));
		
		// qe3 (user p2)
		qe3 = new QueryEvent();
		qe3.setUserId(p2.getId());
	
		QueryStat qe3Stat1 = new QueryStat(c1.getId(), c1.getName(), 10);
		qe3Stat1.setId(3);
		qe3Stat1.setQueryEvent(qe3);
		
		qe3.setQueryStats(Set.of(qe3Stat1));
		
		// qe4 (user p3)
		qe4 = new QueryEvent();
		qe4.setUserId(p3.getId());
			
		QueryStat qe4Stat1 = new QueryStat(c1.getId(), c1.getName(), 10);
		qe4Stat1.setId(4);
		qe4Stat1.setQueryEvent(qe4);
				
		qe4.setQueryStats(Set.of(qe4Stat1));
		
		// qe5 (user p4)
		qe5 = new QueryEvent();
		qe5.setUserId(p4.getId());
					
		QueryStat qe5Stat1 = new QueryStat(c1.getId(), c1.getName(), 10);
		qe5Stat1.setId(5);
		qe5Stat1.setQueryEvent(qe5);
						
		qe5.setQueryStats(Set.of(qe5Stat1));
		
		// CLICK EVENTS
		
		DocClickEvent ce1, ce2, ce3, ce4, ce5, ce6, ce7, ce8, ce9, ce10;
		
		// ce1 (user p1 query qe1)
		ce1 = new DocClickEvent();
		ce1.setUserId(p1.getId());
		ce1.setCollectionId(c1.getId());
		ce1.setCollectionName(c1.getName());
		
		// ce2 (user p1 query qe1)
		ce2 = new DocClickEvent();
		ce2.setUserId(p1.getId());
		ce2.setCollectionId(c2.getId());
		ce2.setCollectionName(c2.getName());
		
		// ce3 (user p1 query qe2)
		ce3 = new DocClickEvent();
		ce3.setUserId(p1.getId());
		ce3.setCollectionId(c1.getId());
		ce3.setCollectionName(c1.getName());
		
		// c4 (user p1 query qe2)
		ce4 = new DocClickEvent();
		ce4.setId(4);
		ce4.setUserId(p1.getId());
		ce4.setCollectionId(c2.getId());
		ce4.setCollectionName(c2.getName());
		
		// ce5 (user p2 query qe3)
		ce5 = new DocClickEvent();
		ce5.setId(5);
		ce5.setUserId(p2.getId());
		ce5.setCollectionId(c1.getId());
		ce5.setCollectionName(c1.getName());
		
		// ce6 (user p2 query qe3)
		ce6 = new DocClickEvent();
		ce6.setUserId(p2.getId());
		ce6.setCollectionId(c1.getId());
		ce6.setCollectionName(c1.getName());
		
		// ce7 (user p3 query qe4)
		ce7 = new DocClickEvent();
		ce7.setUserId(p3.getId());
		ce7.setCollectionId(c1.getId());
		ce7.setCollectionName(c1.getName());
		
		// ce8 (user p3 query qe4)
		ce8 = new DocClickEvent();
		ce8.setUserId(p3.getId());
		ce8.setCollectionId(c1.getId());
		ce8.setCollectionName(c1.getName());
		
		// ce9 (user p4 query qe5)
		ce9 = new DocClickEvent();
		ce9.setUserId(p4.getId());
		ce9.setCollectionId(c1.getId());
		ce9.setCollectionName(c1.getName());
		
		// ce10 (user p4 query qe5)
		ce10 = new DocClickEvent();
		ce10.setUserId(p4.getId());
		ce10.setCollectionId(c1.getId());
		ce10.setCollectionName(c1.getName());
		
		// login / logout
		SessionEvent p1Login = new SessionEvent(p1, SessionEvent.Event.LOGIN);
		SessionEvent p2Login = new SessionEvent(p2, SessionEvent.Event.LOGIN);
		SessionEvent p3Login = new SessionEvent(p3, SessionEvent.Event.LOGIN);
		SessionEvent p4Login = new SessionEvent(p4, SessionEvent.Event.LOGIN);
		SessionEvent p1Logout = new SessionEvent(p1, SessionEvent.Event.LOGOUT);
		SessionEvent p2Logout = new SessionEvent(p2, SessionEvent.Event.LOGOUT);
		SessionEvent p3Logout = new SessionEvent(p3, SessionEvent.Event.LOGOUT);
		SessionEvent p4Logout = new SessionEvent(p4, SessionEvent.Event.LOGOUT);
		
		// BUILD EVENT HISTORY
		
		p1Login.setTimestamp(LocalDateTime.of(2020, 11, 29, 12, 30, 1));
		p2Login.setTimestamp(LocalDateTime.of(2020, 11, 29, 12, 30, 2));
		p3Login.setTimestamp(LocalDateTime.of(2020, 11, 29, 12, 30, 3));
		p4Login.setTimestamp(LocalDateTime.of(2020, 11, 29, 12, 30, 4));
		
		qe1.setTimestamp(LocalDateTime.of(2020, 11, 29, 12, 30, 5));
		ce1.setTimestamp(LocalDateTime.of(2020, 11, 29, 12, 30, 6));
		ce2.setTimestamp(LocalDateTime.of(2020, 11, 29, 12, 30, 7));
		
		qe2.setTimestamp(LocalDateTime.of(2020, 11, 29, 12, 30, 8));
		ce3.setTimestamp(LocalDateTime.of(2020, 11, 29, 12, 30, 9));
		ce4.setTimestamp(LocalDateTime.of(2020, 11, 29, 12, 30, 10));
		
		p1Logout.setTimestamp(LocalDateTime.of(2020, 11, 29, 12, 30, 11));
		
		qe3.setTimestamp(LocalDateTime.of(2020, 11, 29, 12, 30, 12));
		ce5.setTimestamp(LocalDateTime.of(2020, 11, 29, 12, 30, 13));
		ce6.setTimestamp(LocalDateTime.of(2020, 11, 29, 12, 30, 14));
		
		p2Logout.setTimestamp(LocalDateTime.of(2020, 11, 29, 12, 30, 15));
		
		qe4.setTimestamp(LocalDateTime.of(2020, 11, 29, 12, 30, 16));
		ce7.setTimestamp(LocalDateTime.of(2020, 11, 29, 12, 30, 17));
		ce8.setTimestamp(LocalDateTime.of(2020, 11, 29, 12, 30, 18));
		
		p3Logout.setTimestamp(LocalDateTime.of(2020, 11, 29, 12, 30, 19));
		
		qe5.setTimestamp(LocalDateTime.of(2020, 11, 29, 12, 30, 20));
		ce9.setTimestamp(LocalDateTime.of(2020, 11, 29, 12, 30, 21));
		ce10.setTimestamp(LocalDateTime.of(2020, 11, 29, 12, 30, 22));
		
		p4Logout.setTimestamp(LocalDateTime.of(2020, 11, 29, 12, 30, 23));
		
		p1Login.setId(1);
		p2Login.setId(2);
		p3Login.setId(3);
		p4Login.setId(4);
		qe1.setId(5);
		qe2.setId(6);
		qe4.setId(7);
		qe5.setId(8);
		ce1.setId(9);
		ce2.setId(10);
		ce3.setId(11);
		ce4.setId(12);
		ce5.setId(13);
		ce6.setId(14);
		ce7.setId(15);
		ce8.setId(16);
		ce9.setId(17);
		ce10.setId(18);
		p1Logout.setId(19);
		p2Logout.setId(20);
		p3Logout.setId(21);
		p4Logout.setId(22);
		
		testExperiment.setUsageEvents(Set.of(
			p1Login,
			p2Login,
			p3Login,
			p4Login,
			qe1, ce1, ce2,
			qe2, ce3, ce4,
			qe3, ce5, ce6,
			qe4, ce7, ce8,
			qe5, ce9, ce10,
			p1Logout,
			p2Logout,
			p3Logout,
			p4Logout
		));
		
		// set up mock event repositories
		
		List<UsageEvent> p1Events = Arrays.asList(p1Login, qe1, ce1, ce2, qe2, ce3, ce4, p1Logout);
		List<UsageEvent> p2Events = Arrays.asList(p2Login, qe3, ce5, ce6, p2Logout);
		List<UsageEvent> p3Events = Arrays.asList(p3Login, qe4, ce7, ce8, p3Logout);
		List<UsageEvent> p4Events = Arrays.asList(p4Login, qe5, ce9, ce10, p4Logout);
		
		p1Queries = Arrays.asList(qe1, qe2);
		p2Queries = Arrays.asList(qe3);
		p3Queries = Arrays.asList(qe4);
		p4Queries = Arrays.asList(qe5);
		
		p1Clicks = Arrays.asList(ce1, ce2, ce3, ce4);
		p2Clicks = Arrays.asList(ce5, ce6);
		p3Clicks = Arrays.asList(ce7, ce8);
		p4Clicks = Arrays.asList(ce9, ce10);
		
		g1Queries = new ArrayList<>();
		g1Queries.addAll(p1Queries);
		g1Queries.addAll(p2Queries);
		
		g2Queries = new ArrayList<>();
		g2Queries.addAll(p3Queries);
		g2Queries.addAll(p4Queries);
		
		g1Clicks = new ArrayList<>();
		g1Clicks.addAll(p1Clicks);
		g1Clicks.addAll(p2Clicks);
		
		g2Clicks = new ArrayList<>();
		g2Clicks.addAll(p3Clicks);
		g2Clicks.addAll(p3Clicks);
		
		allQueries = new ArrayList<>();
		allQueries.addAll(g1Queries);
		allQueries.addAll(g2Queries);
		
		allClicks = new ArrayList<>();
		allClicks.addAll(g1Clicks);
		allClicks.addAll(g2Clicks);
		
		when(qeRepo.findByExperiment(testExperiment)).thenReturn(allQueries);
		when(qeRepo.findByGroupId(g1.getId())).thenReturn(g1Queries);
		when(qeRepo.findByGroupId(g2.getId())).thenReturn(g2Queries);
		when(qeRepo.findByUserId(p1.getId())).thenReturn(p1Queries);
		when(qeRepo.findByUserId(p2.getId())).thenReturn(p2Queries);
		when(qeRepo.findByUserId(p3.getId())).thenReturn(p3Queries);
		when(qeRepo.findByUserId(p4.getId())).thenReturn(p4Queries);
		
		when(ceRepo.findByExperiment(testExperiment)).thenReturn(allClicks);
		when(ceRepo.findByGroupId(g1.getId())).thenReturn(g1Clicks);
		when(ceRepo.findByGroupId(g2.getId())).thenReturn(g2Clicks);
		when(ceRepo.findByUserId(p1.getId())).thenReturn(p1Clicks);
		when(ceRepo.findByUserId(p2.getId())).thenReturn(p2Clicks);
		when(ceRepo.findByUserId(p3.getId())).thenReturn(p3Clicks);
		when(ceRepo.findByUserId(p4.getId())).thenReturn(p4Clicks);
		
		when(ueRepo.findByUserId(p1.getId())).thenReturn(p1Events);
		when(ueRepo.findByUserId(p2.getId())).thenReturn(p2Events);
		when(ueRepo.findByUserId(p3.getId())).thenReturn(p3Events);
		when(ueRepo.findByUserId(p4.getId())).thenReturn(p4Events);	
	}
	
	@Test
	public void testTotalQueriesPerExperiment() {
		
		int res = extractor.totalQueries(testExperiment);
		
		assertEquals(allQueries.size(), res);
	}
	
	@Test 
	public void testTotalQueriesPerTestGroup() {
		
		int res = extractor.totalQueries(g1);
		
		assertEquals(g1Queries.size(), res);
	}
	
	@Test
	public void testTotalClicksPerExperiment() {
		
		int res = extractor.totalClicks(testExperiment);
		
		assertEquals(allClicks.size(), res);
	}
	
	@Test
	public void testTotalClicksPerTestGroup() {
		
		int res = extractor.totalClicks(g1);
		
		assertEquals(g1Clicks.size(), res);
	}
	
	@Test
	public void testQueriesPerUserByExperiment() {
		
		List<Double> qpu = List.of((double) p1Queries.size(),
								   (double) p2Queries.size(),
								   (double) p3Queries.size(),
								   (double) p4Queries.size());

		DataStats expected = new DataStats(qpu);		
		DataStats actual = extractor.queriesPerUser(testExperiment);
		
		assertTrue(statsEquals(expected, actual));
	}
	
	@Test
	public void testQueriesPerUserByTestGroup() {
		
		List<Double> qpu = List.of((double) p1Queries.size(),
							   	   (double) p2Queries.size());
		
		DataStats expected = new DataStats(qpu);		
		DataStats actual = extractor.queriesPerUser(g1);
		
		assertTrue(statsEquals(expected, actual));
	}
	
	@Test
	public void testClicksPerUserByExperiment() {
		
		List<Double> cpu = List.of((double) p1Clicks.size(),
								   (double) p2Clicks.size(),
								   (double) p3Clicks.size(),
								   (double) p4Clicks.size());
		
		DataStats expected = new DataStats(cpu);		
		DataStats actual = extractor.clicksPerUser(testExperiment);
		
		assertTrue(statsEquals(expected, actual));
	}
	
	@Test
	public void testClicksPerUserByTestGroup() {
		
		List<Double> cpu = List.of((double) p1Clicks.size(),
								   (double) p2Clicks.size());
				
		DataStats expected = new DataStats(cpu);
		DataStats actual = extractor.clicksPerUser(g1);
		
		assertTrue(statsEquals(expected, actual));
	}
	
	@Test
	public void  testClicksPerQueryByExperiment() {
		
		// to each query in the test data correspond 2 clicks
		
		List<Double> cpq = new ArrayList<>();
		
		for (int i = 0; i < 5; ++i) {
			cpq.add(2.0);
		}
		
		DataStats expected = new DataStats(cpq);
		DataStats actual = extractor.clicksPerQuery(testExperiment);
		
		assertTrue(statsEquals(expected, actual));
	}
	
	@Test
	public void testClicksPerQueryByTestGroup() {
		
		// to each query in the test data correspond 2 clicks
		
		List<Double> cpq = new ArrayList<>();
		
		for (int i = 0; i < 3; ++i) {
			cpq.add(2.0);
		}
		
		DataStats expected = new DataStats(cpq);
		DataStats actual = extractor.clicksPerQuery(g1);
		
		assertTrue(statsEquals(expected, actual));
	}
	
	@Test
	public void testTimePerQueryByExperiment() {
		
		// all times per query in the test data are 3 sec.
		
		List<Double> tpq = new ArrayList<>();
		
		for (int i = 0; i < 5; ++i) {
			tpq.add(3.0);
		}
		
		DataStats expected = new DataStats(tpq);
		DataStats actual = extractor.timePerQuery(testExperiment);
		
		assertTrue(statsEquals(expected, actual));
	}
	
	@Test
	public void testTimePerQueryByETestGrou() {
		
		// all times per query in the test data are3 sec.
		
		List<Double> tpq = new ArrayList<>();
		
		for (int i = 0; i < 3; ++i) {
			tpq.add(3.0);
		}
		
		DataStats expected = new DataStats(tpq);
		DataStats actual = extractor.timePerQuery(g1);
		
		assertTrue(statsEquals(expected, actual));
	}

	@Test
	public void testTimePerClickByExperiment() {
		
		// all times per click in the test data are 1 sec.
		
		List<Double> tpc = new ArrayList<>();
		
		for (int i = 0; i < 10; ++i) {
			tpc.add(1.0);
		}
		
		DataStats expected = new DataStats(tpc);
		DataStats actual = extractor.timePerClick(testExperiment);
		
		assertTrue(statsEquals(expected, actual));
	}
	
	@Test
	public void testTimePerClickByTestGroup() {
		
		// all times per click in the test data are 1 sec.
		
		List<Double> tpc = new ArrayList<>();
				
		for (int i = 0; i < 6; ++i) {
			tpc.add(1.0);
		}
				
		DataStats expected = new DataStats(tpc);
		DataStats actual = extractor.timePerClick(g1);
				
		assertTrue(statsEquals(expected, actual));
	}

	private boolean statsEquals(DataStats stats1, DataStats stats2) {
		
		double eps = 0.00001;
		
		return stats1.getN() == stats2.getN() &&
			   Math.abs(stats1.getTotal() - stats2.getTotal()) < eps &&
			   Math.abs(stats1.getMean() - stats2.getMean()) < eps &&
			   Math.abs(stats1.getMedian() - stats2.getMedian()) < eps &&
			   Math.abs(stats1.getStandardDeviation() - stats2.getStandardDeviation()) < eps;
	}
}


















