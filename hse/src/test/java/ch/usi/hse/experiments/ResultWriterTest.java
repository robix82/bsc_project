package ch.usi.hse.experiments;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static org.mockito.Matchers.*;
import static org.mockito.MockitoAnnotations.initMocks;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import ch.usi.hse.db.entities.DocCollection;
import ch.usi.hse.db.entities.Experiment;
import ch.usi.hse.db.entities.Participant;
import ch.usi.hse.db.entities.TestGroup;
import ch.usi.hse.db.entities.UsageEvent;
import ch.usi.hse.db.repositories.DocClickEventRepository;
import ch.usi.hse.db.repositories.ExperimentRepository;
import ch.usi.hse.db.repositories.QueryEventRepository;
import ch.usi.hse.db.repositories.SessionEventRepository;
import ch.usi.hse.db.repositories.UsageEventRepository;
import ch.usi.hse.retrieval.SearchResult;
import ch.usi.hse.retrieval.SearchResultList;

public class ResultWriterTest {

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
	
	private ResultWriter resWriter;
	private Experiment testExperiment;
	private byte[] expectedData;
	
	@BeforeEach
	public void setUp() {
		
		initMocks(this);
		
		resWriter = new ResultWriter(experimentRepo,
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
		
		SearchResultList resList1 = new SearchResultList(query1, List.of(res1, res2));
		SearchResultList resList2 = new SearchResultList(query2, List.of(res2, res3));
		
		List<UsageEvent> events = new ArrayList<>();
		
		
	//	events.add(new SessionEvent())
		
		when(experimentRepo.existsById(anyInt())).thenReturn(false);
		when(experimentRepo.existsById(testExperiment.getId())).thenReturn(true);
	}
	
	@Test
	public void testSetup() {
		
		assertNotNull(resWriter);
		assertFalse(experimentRepo.existsById(999999));
		assertTrue(experimentRepo.existsById(testExperiment.getId()));
	}
}









