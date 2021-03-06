package ch.usi.hse.indexing;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;


public class IndexingResultTest {

	private String collectionName = "c_name";
	private String urlListName = "u_name";
	private int urls = 23;
	private int indexed = 10;
	private int skipped = 3;
	private long timeElapsed = 721;
	
	@Test
	public void testConstructor1() {
		
		IndexingResult res = new IndexingResult();
		
		assertEquals("", res.getCollectionName());
		assertEquals("", res.getUrlListName());
		assertEquals(0, res.getProcessedUrls());
		assertEquals(0, res.getIndexed());
		assertEquals(0, res.getSkipped());
		assertEquals(0, res.getTimeElapsed());
	}
	
	@Test
	public void testConstructor2() {
		
		IndexingResult res = new IndexingResult(collectionName, urlListName, 
												urls, indexed, skipped, timeElapsed);
		
		assertEquals(collectionName, res.getCollectionName());
		assertEquals(urlListName, res.getUrlListName());
		assertEquals(urls, res.getProcessedUrls());
		assertEquals(indexed, res.getIndexed());
		assertEquals(skipped, res.getSkipped());
		assertEquals(timeElapsed, res.getTimeElapsed());
	}
	
	@Test
	public void testSetters() {
		
		IndexingResult res = new IndexingResult();
		
		assertNotEquals(collectionName, res.getCollectionName());
		assertNotEquals(urlListName, res.getUrlListName());
		assertNotEquals(urls, res.getProcessedUrls());
		assertNotEquals(indexed, res.getIndexed());
		assertNotEquals(skipped, res.getSkipped());
		assertNotEquals(timeElapsed, res.getTimeElapsed());
		
		res.setCollectionName(collectionName);
		res.setUrlListName(urlListName);
		res.setProcessedUrls(urls);
		res.setIndexed(indexed);
		res.setSkipped(skipped);
		res.setTimeElapsed(timeElapsed);
		
		assertEquals(collectionName, res.getCollectionName());
		assertEquals(urlListName, res.getUrlListName());
		assertEquals(urls, res.getProcessedUrls());
		assertEquals(indexed, res.getIndexed());
		assertEquals(skipped, res.getSkipped());
		assertEquals(timeElapsed, res.getTimeElapsed());
	}
	
	@Test
	public void testEqualsAndHashCode() {
		
		IndexingResult r1 = new IndexingResult(collectionName, urlListName, 
											   urls, indexed, skipped, timeElapsed);
		
		IndexingResult r2 = new IndexingResult(collectionName, urlListName, 
				   							   urls, indexed, skipped, timeElapsed);
		
		List<IndexingResult> diffs = new ArrayList<>();
		
		diffs.add(new IndexingResult("diff", urlListName, 
									 urls, indexed, skipped, timeElapsed));
		
		diffs.add(new IndexingResult(collectionName, "diff", 
				 					 urls, indexed, skipped, timeElapsed));
		
		diffs.add(new IndexingResult(collectionName, urlListName, 
				 					 921, indexed, skipped, timeElapsed));
		
		diffs.add(new IndexingResult(collectionName, urlListName, 
				 					 urls, 921, skipped, timeElapsed));
		
		diffs.add(new IndexingResult(collectionName, urlListName, 
				 					 urls, indexed, 921, timeElapsed));
		
		assertTrue(r1.equals(r1));
		assertTrue(r1.equals(r2));
		assertEquals(r1.hashCode(), r2.hashCode());
		
		for (IndexingResult r : diffs) {
			
			assertFalse(r1.equals(r));
			assertNotEquals(r1.hashCode(), r.hashCode());
		}
	}
	
	@Test
	public void testIncProcessed() {
		
		IndexingResult r1 = new IndexingResult(collectionName, urlListName, 
				   							   urls, indexed, skipped, timeElapsed);
		
		int before = r1.getProcessedUrls();
		
		r1.incProcessed();
		
		assertEquals(before +1, r1.getProcessedUrls());
	}
	
	@Test
	public void testIncIndexed() {
		
		IndexingResult r1 = new IndexingResult(collectionName, urlListName, 
				   							   urls, indexed, skipped, timeElapsed);
		
		int before = r1.getIndexed();
		
		r1.incIndexed();
		
		assertEquals(before +1, r1.getIndexed());
	}
	
	@Test
	public void testIncSkipped() {
		
		IndexingResult r1 = new IndexingResult(collectionName, urlListName, 
				   							   urls, indexed, skipped, timeElapsed);
		
		int before = r1.getSkipped();
		
		r1.incSkipped();
		
		assertEquals(before +1, r1.getSkipped());
	}
}









