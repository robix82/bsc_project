package ch.usi.hse.services;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import ch.usi.hse.dto.SearchResultList;

public class SearchServiceTest {

	private SearchService testService;
	private String testQueryString = "test query";
	
	@BeforeEach
	public void setUp() {
		
		testService = new SearchService();
	}
	 
	@Test
	public void testQueryStringIsPreserved() {
		
		SearchResultList srl = testService.search(testQueryString);
		
		assertEquals(testQueryString, srl.getQueryString());
	}
} 
