package ch.usi.hse.db.entities;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

public class DocCollectionTest {

	private int testId =23;
	private String testName = "testName";
	private String testUrlList = "testUrlList";
	private String testIndexDir = "testIndexDir";
	
	@Test
	public void testConstructor1() {
		
		DocCollection c = new DocCollection();
		
		assertEquals(0, c.getId());
		assertFalse(c.getIndexed());
	}
	
	@Test
	public void testConstructor2() {
		
		DocCollection c = new DocCollection(testName, testUrlList);
		
		assertEquals(0, c.getId());
		assertFalse(c.getIndexed());
		assertEquals(testName, c.getName());
		assertEquals(testUrlList, c.getUrlListName());
	}
	
	@Test
	public void testSetters() {
		
		DocCollection c = new DocCollection();
		
		assertNotEquals(testId, c.getId());
		assertNotEquals(testName, c.getName());
		assertNotEquals(testUrlList, c.getUrlListName());
		assertNotEquals(testIndexDir, c.getIndexDirName());
		assertFalse(c.getIndexed());
		
		c.setId(testId);
		c.setName(testName);
		c.setUrlListName(testUrlList);
		c.setIndexDirName(testIndexDir);
		c.setIndexed(true);
		
		assertEquals(testId, c.getId());
		assertEquals(testName, c.getName());
		assertEquals(testUrlList, c.getUrlListName());
		assertEquals(testIndexDir, c.getIndexDirName());
		assertTrue(c.getIndexed());
	}
	
	@Test
	public void testEqualsAndHashCode() {
		
		DocCollection c1 = new DocCollection();
		DocCollection c2 = new DocCollection();
		DocCollection c3 = new DocCollection();
		c1.setId(1);
		c2.setId(1);
		c3.setId(2);
		
		assertTrue(c1.equals(c1));
		assertTrue(c1.equals(c2));
		assertFalse(c1.equals(c3));
		
		assertEquals(c1.hashCode(), c2.hashCode());
		assertNotEquals(c1.hashCode(), c3.hashCode());
	}
}











