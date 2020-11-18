package ch.usi.hse.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import java.util.List;

import org.apache.lucene.queryparser.classic.ParseException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import ch.usi.hse.db.entities.DocCollection;
import ch.usi.hse.db.entities.Participant;
import ch.usi.hse.db.entities.TestGroup;
import ch.usi.hse.db.repositories.DocCollectionRepository;
import ch.usi.hse.exceptions.FileReadException;
import ch.usi.hse.retrieval.SearchAssembler;
import ch.usi.hse.retrieval.SearchResultList;
import ch.usi.hse.testData.SearchData;

public class SearchServiceTest {
	
	@Mock
	private DocCollectionRepository collectionRepo;
	
	@Mock
	private SearchAssembler searchAssembler;

	private SearchService testService;
	private String testQueryString = "test query";
	private Participant testParticipant;
	private SearchResultList expectedResults;
	private List<DocCollection> docCollections;
	
	@BeforeEach
	public void setUp() throws ParseException, FileReadException {
		
		initMocks(this);
		
		testService = new SearchService(collectionRepo, searchAssembler);
		
		docCollections = List.of(new DocCollection("c1", "l1"));
		
		TestGroup g = new TestGroup("g");
		g.addDocCollection(docCollections.get(0));
		testParticipant = new Participant("testPArticipant", "pwd");
		testParticipant.setTestGroup(g);
	
		expectedResults = SearchData.dummieSearchResultList(10);
		
		when(searchAssembler.getSearchResults(testQueryString, docCollections)).thenReturn(expectedResults);
	}
	
	@Test
	public void testQueryStringIsPreserved() throws ParseException, FileReadException {
		
		SearchResultList srl = testService.search(testQueryString, testParticipant);
		
		assertEquals(expectedResults, srl);
	}

} 
