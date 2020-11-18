package ch.usi.hse.retrieval;

import static ch.usi.hse.testData.SearchData.dummieSearchResults;
import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;


public class SearchResultListTest {

	private static String testQueryString;
	private static List<SearchResult> testSearchResults;
	
	// for testing equals and hashCode
	private static SearchResultList testSrl, equalSrl;
	private static List<SearchResultList> differentSrls;
	
	@BeforeAll
	public static void init() {
		
		testQueryString = "test query";
		testSearchResults = dummieSearchResults(5);
		
		testSrl = new SearchResultList(testQueryString, testSearchResults);
		equalSrl = new SearchResultList(testQueryString, testSearchResults);
		
		List<SearchResult> l1 = dummieSearchResults(3); // different size
		List<SearchResult> l2 = dummieSearchResults(4); // different content
		l2.add(new SearchResult(11, "otherUrl", "otherSummary"));
		
		differentSrls = new ArrayList<>();
		differentSrls.add(new SearchResultList("otherQueryString",  testSearchResults));
		differentSrls.add(new SearchResultList(testQueryString, l1));
		differentSrls.add(new SearchResultList(testQueryString, l2));
	}
	
	@Test
	public void testConstructor1() {
		
		SearchResultList srl = new SearchResultList();
		
		String query = srl.getQueryString();
		List<SearchResult> results = srl.getSearchResults();
		
		assertEquals("", query);
		assertNotNull(results);
		assertEquals(0, results.size());
	}
	
	@Test
	public void testConstructor2() {
		
		SearchResultList srl = new SearchResultList(testQueryString, testSearchResults);
		
		assertEquals(testQueryString, srl.getQueryString());
		assertIterableEquals(testSearchResults, srl.getSearchResults());
	}
	
	@Test 
	public void testConstructor3() {
		
		// TODO: test construction from Lucene TopDocs
	}
	
	@Test
	public void testSetters() {
		
		SearchResultList srl = new SearchResultList();
		 
		assertNotEquals(testQueryString, srl.getQueryString());
		assertNotEquals(testSearchResults, srl.getSearchResults());
		
		srl.setQueryString(testQueryString);
		srl.setSearchResults(testSearchResults);
		
		assertEquals(testQueryString, srl.getQueryString());
		assertIterableEquals(testSearchResults, srl.getSearchResults());
	}
	
	@Test
	public void testEquals() {
		
		assertTrue(testSrl.equals(equalSrl));
		
		for (SearchResultList srl : differentSrls) {
			
			assertFalse(testSrl.equals(srl));
		}
	}
	
	@Test
	public void testHashCode() {
		
		assertEquals(testSrl.hashCode(), equalSrl.hashCode());
		
		for (SearchResultList srl : differentSrls) {
			
			assertNotEquals(testSrl.hashCode(), srl.hashCode());
		}
	}
}














