package ch.usi.hse.dto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import org.junit.jupiter.api.Test;

public class SearchResultTest {

	private long testId = 23;
	private String testUrl = "testUrl";
	private String testSummary = "testSummary";
	
	@Test
	public void testConstructor1() {
		
		SearchResult res = new SearchResult();
		
		assertEquals(-1, res.getDocumentId());
		assertEquals("", res.getUrl());
		assertEquals("", res.getSummary());
	}
	
	@Test
	public void testConstructor2() {
		
		SearchResult res = new SearchResult(testId, testUrl, testSummary);
		
		assertEquals(testId, res.getDocumentId());
		assertEquals(testUrl, res.getUrl());
		assertEquals(testSummary, res.getSummary());
	}
	
	@Test
	public void testConstructor3() {
		
		// TODO: test construction from Lucene ScoreDoc
	}
	
	@Test
	public void testSetters() {
		
		SearchResult res = new SearchResult();
		
		assertNotEquals(testId, res.getDocumentId());
		assertNotEquals(testUrl, res.getUrl());
		assertNotEquals(testSummary, res.getSummary());
		
		res.setDocumentId(testId);
		res.setUrl(testUrl);
		res.setSummary(testSummary);
		
		assertEquals(testId, res.getDocumentId());
		assertEquals(testUrl, res.getUrl());
		assertEquals(testSummary, res.getSummary());
	}
}














