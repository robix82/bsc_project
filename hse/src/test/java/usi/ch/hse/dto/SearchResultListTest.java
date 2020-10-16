package usi.ch.hse.dto;

import static org.junit.jupiter.api.Assertions.*;
import static usi.ch.hse.dummie_data.SearchData.dummieSearchResults;

import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class SearchResultListTest {

	private static String testQueryString;
	private static List<SearchResult> testSearchResults;
	
	@BeforeAll
	public static void init() {
		
		testQueryString = "test query";
		testSearchResults = dummieSearchResults(5);
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
}














