package ch.usi.hse.retrieval;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;


import java.util.ArrayList;
import java.util.List;

public class SearchResultTest {

	private static int testId = 23;
	private static String testUrl = "testUrl";
	private static String testSummary = "testSummary";
	
	// for testing equals and hashCode
	private static SearchResult testSr, equalSr;
	private static List<SearchResult> differentSrs;
	
	@BeforeAll
	public static void init() {
		
		testSr = new SearchResult(testId, testUrl, testSummary);
		equalSr = new SearchResult(testId, testUrl, testSummary);
		
		differentSrs = new ArrayList<>();
		differentSrs.add(new SearchResult(24, testUrl, testSummary));
		differentSrs.add(new SearchResult(24, "otherUrl", testSummary));
		differentSrs.add(new SearchResult(24, testUrl, "otherSummary"));
	}
	
	@Test
	public void testConstructor1() {
		
		SearchResult res = new SearchResult();
		
		assertEquals(0, res.getDocumentId());
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
	
	@Test
	public void testEquals() {
		
		assertTrue(testSr.equals(equalSr));
		
		for (SearchResult sr : differentSrs) {
			
			assertFalse(testSr.equals(sr));
		}
	}
	
	@Test
	public void testHashCode() {
		
		assertEquals(testSr.hashCode(), equalSr.hashCode());
		
		for (SearchResult sr : differentSrs) {
			
			assertNotEquals(testSr.hashCode(), sr.hashCode());
		}
	}
}














