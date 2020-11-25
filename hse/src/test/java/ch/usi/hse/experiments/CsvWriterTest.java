package ch.usi.hse.experiments;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static org.mockito.Matchers.*;
import static org.mockito.MockitoAnnotations.initMocks;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import ch.usi.hse.db.entities.DocClickEvent;
import ch.usi.hse.db.entities.DocCollection;
import ch.usi.hse.db.entities.Experiment;
import ch.usi.hse.db.entities.Participant;
import ch.usi.hse.db.entities.QueryEvent;
import ch.usi.hse.db.entities.SessionEvent;
import ch.usi.hse.db.entities.TestGroup;
import ch.usi.hse.db.entities.UsageEvent;
import ch.usi.hse.db.repositories.DocClickEventRepository;
import ch.usi.hse.db.repositories.ExperimentRepository;
import ch.usi.hse.db.repositories.QueryEventRepository;
import ch.usi.hse.db.repositories.SessionEventRepository;
import ch.usi.hse.db.repositories.UsageEventRepository;
import ch.usi.hse.exceptions.NoSuchExperimentException;
import ch.usi.hse.retrieval.SearchResult;
import ch.usi.hse.retrieval.SearchResultList;

public class CsvWriterTest {

	@Mock
	private ExperimentRepository experimentRepo;
	
	@Mock
	private UsageEventRepository usageEventRepo;
	
	@Mock
	private SessionEventRepository sessionEventRepo;
	
	@Mock
	private QueryEventRepository queryEventRepo;
	
	@Mock
	private DocClickEventRepository docClickEventRepo;
	
	private CsvWriter csvWriter;
	private Experiment testExperiment;
	private String expectedString;
	
	@BeforeEach
	public void setUp() {
		
		initMocks(this);
		
		csvWriter = new CsvWriter(experimentRepo,
									 usageEventRepo,
									 sessionEventRepo,
									 queryEventRepo,
									 docClickEventRepo);
		
		// set up test experiment
		
		testExperiment = new Experiment("testExperiment");
		testExperiment.setId(23);
		
		Participant testParticipant = new Participant("testParticipant", "pwd");
		testParticipant.setId(11);	
		
		TestGroup testGroup = new TestGroup("testGroup");
		testGroup.setId(21);
		
		DocCollection c1 = new DocCollection("c1", "list1");
		DocCollection c2 = new DocCollection("c2", "list2");
		c1.setId(31);
		c2.setId(32);
		
		testGroup.addParticipant(testParticipant);
		testGroup.addDocCollection(c1);
		testGroup.addDocCollection(c2);
		
		testExperiment.addTestGroup(testGroup);
		
		// add usage events
		
		String query1 = "test query 1";
		String query2 = "test query 2";
		String url1 = "url1";
		String url2 = "url2";
		String url3 = "url3";
		
		SearchResult res1 = new SearchResult(1, url1, "");
		SearchResult res2 = new SearchResult(2, url2, "");
		SearchResult res3 = new SearchResult(3, url3, "");
		res1.setDocCollection(c1);
		res2.setDocCollection(c1);
		res3.setDocCollection(c2);
		
		SearchResultList resList1 = new SearchResultList(query1, Arrays.asList(res1, res2));
		SearchResultList resList2 = new SearchResultList(query2, Arrays.asList(res2, res3));
		
		SessionEvent se1 = new SessionEvent(testParticipant, SessionEvent.Event.LOGIN);
		QueryEvent qe1 = new QueryEvent(testParticipant, resList1);
		DocClickEvent de1 = new DocClickEvent(testParticipant, res1);
		QueryEvent qe2 = new QueryEvent(testParticipant, resList2);
		DocClickEvent de2 = new DocClickEvent(testParticipant, res2);
		DocClickEvent de3 = new DocClickEvent(testParticipant, res3);
		SessionEvent se2 = new SessionEvent(testParticipant, SessionEvent.Event.LOGOUT);
		se1.setId(1);
		se1.setTimestamp(LocalDateTime.of(2020, 11, 20, 0, 0));
		qe1.setId(2);
		qe1.setTimestamp(LocalDateTime.of(2020, 11, 20, 0, 1));
		de1.setId(3);
		de1.setTimestamp(LocalDateTime.of(2020, 11, 20, 0, 2));
		qe2.setId(4);
		qe2.setTimestamp(LocalDateTime.of(2020, 11, 20, 0, 3));
		de2.setId(5);
		de2.setTimestamp(LocalDateTime.of(2020, 11, 20, 0, 4));
		de3.setId(6);
		de3.setTimestamp(LocalDateTime.of(2020, 11, 20, 0, 5));
		se2.setId(7);
		se2.setTimestamp(LocalDateTime.of(2020, 11, 20, 0, 6));
		
		List<UsageEvent> events = Arrays.asList(se1, qe1, de1, qe2, de2, de3, se2);
		
		for (UsageEvent e : events) {
			
			testExperiment.addUsageEvent(e);
		}
		
		// set up mock repositories
		
		when(experimentRepo.existsById(anyInt())).thenReturn(false);
		when(experimentRepo.existsById(testExperiment.getId())).thenReturn(true);
			
		when(usageEventRepo.findByExperiment(testExperiment)).thenReturn(events);

		for (SessionEvent e : Arrays.asList(se1, se2)) {
			
			when(sessionEventRepo.existsById(e.getId())).thenReturn(true);
			when(sessionEventRepo.findById(e.getId())).thenReturn(e);
		}
		
		for (QueryEvent e : Arrays.asList(qe1, qe2)) {
			
			when(queryEventRepo.existsById(e.getId())).thenReturn(true);
			when(queryEventRepo.findById(e.getId())).thenReturn(e);
		}
		
		for (DocClickEvent e : Arrays.asList(de1, de2, de3)) {
			
			when(docClickEventRepo.existsById(e.getId())).thenReturn(true);
			when(docClickEventRepo.findById(e.getId())).thenReturn(e);
		}
		
		// build expected csv string
		
		// header
		expectedString = "eventId,eventType,timestamp,userId,groupId,groupName," +
						 "action," +
					     "queryString,resultCount,docDistribution," +
						 "url,documentId,documentRank,collectionId,collectionName\n";
		
		
		// se1
		expectedString += se1.getId() + ",SESSION," + se1.getTimestamp().toString() + "," +
						  testParticipant.getId() + "," +
						  testGroup.getId() + "," +
						  testGroup.getName() + ",LOGIN," +
						  "N.A.,N.A.,N.A.,N.A.,N.A.,N.A.,N.A.,N.A.\n";
		
		// qe1
		expectedString += qe1.getId() + ",QUERY," + qe1.getTimestamp().toString() + "," +
						  testParticipant.getId() + "," +
						  testGroup.getId() + "," +
						  testGroup.getName() + ",N.A.," +
						  query1 + "," +
						  qe1.getTotalResults() + "," +
						  "[" + c1.getName() + ":2]," +
						  "N.A.,N.A.,N.A.,N.A.,N.A.\n";
		
		// de1
		expectedString += de1.getId() + ",DOC_CLICK," + de1.getTimestamp().toString() + "," +
						  testParticipant.getId() + "," +
						  testGroup.getId() + "," +
						  testGroup.getName() +
						  ",N.A.,N.A.,N.A.,N.A.," +
						  url1 + "," + de1.getDocumentId() + "," +
						  de1.getDocumentRank() + "," +
						  de1.getCollectionId() + "," +
						  de1.getCollectionName() + "\n";
		
		// qe2
		expectedString += qe2.getId() + ",QUERY," + qe2.getTimestamp().toString() + "," +
						  testParticipant.getId() + "," +
						  testGroup.getId() + "," +
						  testGroup.getName() + ",N.A.," +
						  query2 + "," +
						  qe1.getTotalResults() + "," +
					      "[" + c1.getName() + ":1][" + c2.getName() + ":1]," +
						  "N.A.,N.A.,N.A.,N.A.,N.A.\n";
		
		// de2
		expectedString += de2.getId() + ",DOC_CLICK," + de2.getTimestamp().toString() + "," +
						  testParticipant.getId() + "," +
						  testGroup.getId() + "," +
						  testGroup.getName() +
						  ",N.A.,N.A.,N.A.,N.A.," +
						  url2 + "," + de2.getDocumentId() + "," +
						  de2.getDocumentRank() + "," +
						  de2.getCollectionId() + "," +
						  de2.getCollectionName() + "\n";
		
		// de3
		expectedString += de3.getId() + ",DOC_CLICK," + de3.getTimestamp().toString() + "," +
						  testParticipant.getId() + "," +
						  testGroup.getId() + "," +
						  testGroup.getName() +
						  ",N.A.,N.A.,N.A.,N.A.," +
						  url3 + "," + de3.getDocumentId() + "," +
						  de3.getDocumentRank() + "," +
						  de3.getCollectionId() + "," +
						  de3.getCollectionName() + "\n";
		
		// se2
		expectedString += se2.getId() + ",SESSION," + se2.getTimestamp().toString() + "," +
						  testParticipant.getId() + "," +
						  testGroup.getId() + "," +
						  testGroup.getName() + ",LOGOUT," +
						  "N.A.,N.A.,N.A.,N.A.,N.A.,N.A.,N.A.,N.A.\n";
	}
	
	@Test
	public void testSetup() {
		
		assertNotNull(csvWriter);
		assertFalse(experimentRepo.existsById(999999));
		assertTrue(experimentRepo.existsById(testExperiment.getId()));
		
		//System.out.println(expectedString);
	}
	
	@Test
	public void testWriteExperimentData1() throws Exception {
		
		String str = csvWriter.writeExperimentData(testExperiment);
		
		assertEquals(expectedString, str);
	}
	
	@Test
	public void testWriteExperimentData2() {
		
		int badId = 999999;
		Experiment experiment = new Experiment("exp");
		experiment.setId(badId);
		boolean exc;
		
		try {
			
			csvWriter.writeExperimentData(experiment);
			exc = false; 
		}
		catch (NoSuchExperimentException e) {
			
			assertTrue(e.getMessage().contains(Integer.toString(badId)));
			exc = true;
		}
		
		assertTrue(exc);
	}
}









