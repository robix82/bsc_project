package ch.usi.hse.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import java.util.List;

import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.highlight.InvalidTokenOffsetsException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import ch.usi.hse.db.entities.DocCollection;
import ch.usi.hse.db.entities.Experiment;
import ch.usi.hse.db.entities.Participant;
import ch.usi.hse.db.entities.TestGroup;
import ch.usi.hse.db.repositories.DocCollectionRepository;
import ch.usi.hse.db.repositories.ExperimentRepository;
import ch.usi.hse.db.repositories.ParticipantRepository;
import ch.usi.hse.exceptions.FileReadException;
import ch.usi.hse.retrieval.SearchAssembler;
import ch.usi.hse.retrieval.SearchResultList;
import ch.usi.hse.storage.UrlListStorage;
import ch.usi.hse.testData.SearchData;

public class SearchServiceTest {
	
	@Mock
	private DocCollectionRepository collectionRepo;
	
	@Mock
	private ExperimentRepository experimentRepo;
	
	@Mock
	private ParticipantRepository participantRepo;
	
	@Mock
	private SearchAssembler searchAssembler;
	
	@Mock
	private SimpMessagingTemplate simpMessagingTemplate;
	
	@Mock
	private UrlListStorage urlListStorage;

	private SearchService testService;
	private String testQueryString = "test query";
	private Participant testParticipant;
	private Experiment testExperiment;
	private SearchResultList expectedResults;
	private List<DocCollection> docCollections;
	
	@BeforeEach
	public void setUp() throws ParseException, FileReadException, InvalidTokenOffsetsException {
		
		initMocks(this);
		
		testService = new SearchService(collectionRepo, experimentRepo, participantRepo, 
										searchAssembler, simpMessagingTemplate, urlListStorage);
		
		docCollections = List.of(new DocCollection("c1", "l1"));
		
		TestGroup g = new TestGroup("g");
		g.setId(21);
		g.addDocCollection(docCollections.get(0));
		
		testParticipant = new Participant("testParticipant", "pwd");
		testParticipant.setId(23);
		g.addParticipant(testParticipant);
		
		testExperiment = new Experiment("test");
		testExperiment.setId(42);
		testExperiment.addTestGroup(g);
	
		expectedResults = SearchData.dummieSearchResultList(10);
		
		when(searchAssembler.getSearchResults(testQueryString, docCollections)).thenReturn(expectedResults);
		when(experimentRepo.existsById(testExperiment.getId())).thenReturn(true);
		when(experimentRepo.findById(testExperiment.getId())).thenReturn(testExperiment);
	}
	
	@Test
	public void testSetup() throws Exception{
		
		assertEquals(testExperiment.getId(), testParticipant.getExperimentId());
	}
	
	@Test
	public void testSearch1() throws Exception {
		
		assertTrue(testExperiment.getUsageEvents().isEmpty());
		testParticipant.setQueryCount(2);
		
		SearchResultList srl = testService.search(testQueryString, testParticipant);
		
		assertEquals(expectedResults, srl);
		assertFalse(testExperiment.getUsageEvents().isEmpty());
	}
} 






