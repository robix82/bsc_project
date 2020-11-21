package ch.usi.hse.db.entities;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class QueryStatTest {

	private int testId = 23;
	private int testCollectionId = 72;
	private String testCollectionName = "testName";
	private int testResultCount = 123;
	private QueryEvent testEvent;
	
	@BeforeEach
	public void setUp() {
			
		testEvent = new QueryEvent();
		testEvent.setId(1);
	}
	
	@Test
	public void testConstructor1() {
		
		QueryStat qs = new QueryStat();
		
		assertEquals(0, qs.getId());
	}
	
	@Test
	public void testConstructor2() {
		
		QueryStat qs = new QueryStat(testCollectionId, testCollectionName, testResultCount);
		
		assertEquals(0, qs.getId());
		assertEquals(testCollectionId, qs.getCollectionId());
		assertEquals(testCollectionName, qs.getCollectionName());
		assertEquals(testResultCount, qs.getResultCount());
	}
	
	@Test
	public void testSetters() {
		
		QueryStat qs = new QueryStat();
		
		assertNotEquals(testId, qs.getId());
		assertNotEquals(testEvent, qs.getQueryEvent());
		assertNotEquals(testCollectionId, qs.getCollectionId());
		assertNotEquals(testCollectionName, qs.getCollectionName());
		assertNotEquals(testResultCount, qs.getResultCount());
		
		qs.setId(testId);
		qs.setQueryEvent(testEvent);
		qs.setCollectionId(testCollectionId);
		qs.setCollectionName(testCollectionName);
		qs.setResultCount(testResultCount);
		
		assertEquals(testId, qs.getId());
		assertEquals(testEvent, qs.getQueryEvent());
		assertEquals(testCollectionId, qs.getCollectionId());
		assertEquals(testCollectionName, qs.getCollectionName());
		assertEquals(testResultCount, qs.getResultCount());
	}
	
	@Test
	public void testEqualsAndHashode() {
		
		QueryStat qs1 = new QueryStat();
		QueryStat qs2 = new QueryStat();
		QueryStat qs3 = new QueryStat();
		
		qs1.setId(1);
		qs2.setId(1);
		qs3.setId(2);
		
		assertTrue(qs1.equals(qs1));
		assertTrue(qs1.equals(qs2));
		assertFalse(qs1.equals(qs3));
		
		assertEquals(qs1.hashCode(), qs2.hashCode());
		assertNotEquals(qs1.hashCode(), qs3.hashCode());
	}
}







