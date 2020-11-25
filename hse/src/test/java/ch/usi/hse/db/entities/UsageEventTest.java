package ch.usi.hse.db.entities;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import ch.usi.hse.retrieval.SearchResult;
import ch.usi.hse.retrieval.SearchResultList;

public class UsageEventTest {

	private UsageEvent.Type testType = UsageEvent.Type.QUERY;
	private Participant testUser;
	private TestGroup testGroup;
	private Experiment testExperiment;
	
	@BeforeEach
	public void setUp() {
		
		testUser = new Participant("p1", "pwd");
		testUser.setId(43);
		testGroup = new TestGroup("g1");
		testGroup.setId(12);
		testExperiment = new Experiment("e1");
		testExperiment.setId(35);
		
		testGroup.addParticipant(testUser);
		testExperiment.addTestGroup(testGroup);
	}
	
	// BASE CLASS
	
	@Test
	public void testSetup() {
		
		assertEquals(testGroup, testUser.getTestGroup());
		assertEquals(testExperiment, testGroup.getExperiment());
	}
	
	@Test
	public void testConstructor1() {
		
		UsageEvent evt = new UsageEvent();
		
		assertEquals(0, evt.getId());
		assertTrue(timeApproxEquals(LocalDateTime.now(), evt.getTimestamp()));
	}
	
	@Test
	public void testConstructor2() {
		
		UsageEvent evt = new UsageEvent(testType, testUser);
		
		assertEquals(0, evt.getId());
		assertTrue(timeApproxEquals(LocalDateTime.now(), evt.getTimestamp()));
		assertEquals(testUser.getId(), evt.getUserId());
		assertEquals(testGroup.getId(), evt.getGroupId());
		assertEquals(testGroup.getName(), evt.getGroupName());
		assertEquals(testType, evt.getEventType());
	}
	
	@Test
	public void testSetters() {
		
		int testId = 23;
		int userId = 12;
		int groupId = 72;
		String groupName = "gr32";
		LocalDateTime testTime = LocalDateTime.of(2020, 11, 20, 11, 11, 21);
		
		UsageEvent evt = new UsageEvent();
		
		assertNotEquals(testId, evt.getId());
		assertNotEquals(testTime, evt.getTimestamp());
		assertNotEquals(userId, evt.getUserId());
		assertNotEquals(groupId, evt.getGroupId());
		assertNotEquals(groupName, evt.getGroupName());
		assertNotEquals(testType, evt.getEventType());
		
		evt.setId(testId);
		evt.setTimestamp(testTime);
		evt.setUserId(userId);
		evt.ssetGroupId(groupId);
		evt.setGroupName(groupName);
		evt.setEventType(testType);
		
		assertEquals(testId, evt.getId());
		assertEquals(testTime, evt.getTimestamp());
		assertEquals(userId, evt.getUserId());
		assertEquals(groupId, evt.getGroupId());
		assertEquals(groupName, evt.getGroupName());
		assertEquals(testType, evt.getEventType());
	}
	
	@Test
	public void testEqualsAndHashCode() {
		
		UsageEvent e1 = new UsageEvent();
		UsageEvent e2 = new UsageEvent();
		UsageEvent e3 = new UsageEvent();
		
		e1.setId(21);
		e2.setId(21);
		e3.setId(42);
		
		assertTrue(e1.equals(e1));
		assertTrue(e1.equals(e2));
		assertFalse(e1.equals(e3));
		
		assertEquals(e1.hashCode(), e2.hashCode());
		assertNotEquals(e1.hashCode(), e3.hashCode());
	}
	
	// SESSION EVENT
	
	@Test
	public void testSeConstructor1() {
		
		SessionEvent evt = new SessionEvent();
		assertEquals(0, evt.getId());
		assertEquals(UsageEvent.Type.SESSION, evt.getEventType());
	}
	
	@Test
	public void testSeConstructor2() {
		
		SessionEvent evt = new SessionEvent(testUser, SessionEvent.Event.LOGIN);
		
		assertEquals(0, evt.getId());
		assertEquals(SessionEvent.Event.LOGIN, evt.getEvent());
		assertEquals(UsageEvent.Type.SESSION, evt.getEventType());
		assertEquals(testUser.getId(), evt.getUserId());
	}
	
	@Test
	public void testSeSetters() {
		
		SessionEvent.Event newSessionEvent = SessionEvent.Event.LOGOUT;
		
		SessionEvent evt = new SessionEvent(testUser, SessionEvent.Event.LOGIN);
		
		assertNotEquals(newSessionEvent, evt.getEvent());
		
		evt.setEvent(newSessionEvent);
		
		assertEquals(newSessionEvent, evt.getEvent());
	}
	
	// QUERY EVENT
	
	@Test
	public void testQeConstructor1() {
		
		QueryEvent evt = new QueryEvent();
		
		assertEquals(0, evt.getId());
		assertEquals(0, evt.getQueryStats().size());
		assertEquals(UsageEvent.Type.QUERY, evt.getEventType());
	}
	
	@Test
	public void testQeConstructor2() {
		
		int cId = 21;
		
		DocCollection c = new DocCollection("c", "list");
		c.setId(cId);
		SearchResult r1 = new SearchResult();
		SearchResult r2 = new SearchResult();
		
		r1.setDocCollection(c);
		r2.setDocCollection(c);
		
		String queryString = "some query";
		
		SearchResultList resList = new SearchResultList();
		resList.setQueryString(queryString);
		resList.addSearchResult(r1);
		resList.addSearchResult(r2);
		
		QueryEvent evt = new QueryEvent(testUser, resList);
		
		assertEquals(0, evt.getId());
		assertEquals(UsageEvent.Type.QUERY, evt.getEventType());
		assertEquals(testUser.getId(), evt.getUserId());
		assertEquals(queryString, evt.getQueryString());
		assertEquals(2, evt.getTotalResults());
		assertEquals(1, evt.getQueryStats().size());
		
		QueryStat qs = (QueryStat) evt.getQueryStats().toArray()[0];
		assertEquals(cId, qs.getCollectionId());
		assertEquals(c.getName(), qs.getCollectionName());
		assertEquals(2, qs.getResultCount());
	}
	
	@Test
	public void testQeSetters() {
		
		String queryString = "some query";
		int totalResults = 23;
		QueryStat qs1 = new QueryStat();
		QueryStat qs2 = new QueryStat();
		qs1.setId(1);
		qs2.setId(2);
		Set<QueryStat> queryStats = Set.of(qs1, qs2);
		
		QueryEvent evt = new QueryEvent();
		
		assertNotEquals(queryString, evt.getQueryString());
		assertNotEquals(totalResults, evt.getTotalResults());
		assertNotEquals(queryStats, evt.getQueryStats());
		
		evt.setQueryString(queryString);
		evt.setTotalResults(totalResults);
		evt.setQueryStats(queryStats);
		
		assertEquals(queryString, evt.getQueryString());
		assertEquals(totalResults, evt.getTotalResults());
		assertEquals(queryStats, evt.getQueryStats());
	}
	
	// DOC_CLICK EVENT
	
	@Test
	public void testDcConstructor1() {
		
		DocClickEvent evt = new DocClickEvent();
		assertEquals(0, evt.getId());
		assertEquals(UsageEvent.Type.DOC_CLICK, evt.getEventType());
	}
	
	@Test
	public void testDcConstructor2() {
		
		String url = "www.test.com";
		int docId = 27;
		int collectionId = 12;
		String collectionName = "c12";
		SearchResult r = new SearchResult();
		r.setUrl(url);
		r.setDocumentId(docId);
		r.setCollectionId(collectionId);
		r.setCollectionName(collectionName);
		
		DocClickEvent evt = new DocClickEvent(testUser, r);
		
		assertEquals(0, evt.getId());
		assertEquals(UsageEvent.Type.DOC_CLICK, evt.getEventType());
		assertEquals(testUser.getId(), evt.getUserId());
		assertEquals(url, evt.getUrl());
		assertEquals(docId, evt.getDocumentId());
		assertEquals(collectionId, evt.getCollectionId());
		assertEquals(collectionName, evt.getCollectionName());
	}
	
	@Test
	public void testDcSetters() {
		
		String url = "www.test.com";
		int docId = 27;
		
		DocClickEvent evt = new DocClickEvent();
		
		assertNotEquals(url, evt.getUrl());
		assertNotEquals(docId, evt.getDocumentId());
		
		evt.setUrl(url);
		evt.setDocumentId(docId);
		
		assertEquals(url, evt.getUrl());
		assertEquals(docId, evt.getDocumentId());
	}
	
	
	///////////////////////////////
	
	private boolean timeApproxEquals(LocalDateTime t1, LocalDateTime t2) {
		
		if (t1 == null && t2 == null) {
			return true;
		}
		
		if (t1 == null || t2 == null) {
			return false;
		}
		
		return t1.getYear() == t2.getYear() &&
			   t1.getMonth() == t2.getMonth() &&
			   t1.getDayOfMonth() == t2.getDayOfMonth() &&
			   t1.getHour() == t2.getHour() &&
			   t1.getMinute() == t2.getMinute() &&
			   t1.getSecond() == t2.getSecond();
	}
}










