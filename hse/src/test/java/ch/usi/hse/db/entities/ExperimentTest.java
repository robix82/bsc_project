package ch.usi.hse.db.entities;

import static org.junit.jupiter.api.Assertions.*;

import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ExperimentTest {

	private int testId;
	private String testTitle;
	private Set<TestGroup> testGroups;
	
	@BeforeEach
	public void setUp() {
		
		testId = 23;
		testTitle = "title";
		testGroups = new HashSet<>();
		
		TestGroup g1 = new TestGroup("g1");
		g1.setId(1);
		TestGroup g2 = new TestGroup("g2");
		g2.setId(2);
		
		testGroups.add(g1);
		testGroups.add(g2);
	}
	
	@Test
	public void testConstructor1() {
		
		Experiment e = new Experiment();
		
		assertEquals(0, e.getId());
		assertNotNull(e.getTestGroups());
	}
	
	@Test
	public void testConstructor2() {
		
		Experiment e = new Experiment(testId, testTitle, testGroups);
		
		assertEquals(testId, e.getId());
		assertEquals(testTitle, e.getTitle());
		assertIterableEquals(testGroups, e.getTestGroups());
	}
	
	@Test
	public void testConstructor3() {
		
		Experiment e = new Experiment(testTitle);
		
		assertEquals(testTitle, e.getTitle());
		assertEquals(0, e.getId());
		assertNotNull(e.getTestGroups());
	}
	
	@Test
	public void testSetters() {
		
		Experiment e = new Experiment();
		
		assertNotEquals(testId, e.getId());
		assertNotEquals(testTitle, e.getTitle());
		assertEquals(0, e.getTestGroups().size());
		
		e.setId(testId);
		e.setTitle(testTitle);
		e.setTestGroups(testGroups);
		
		assertEquals(testId, e.getId());
		assertEquals(testTitle, e.getTitle());
		assertIterableEquals(testGroups, e.getTestGroups());
	}
	
	@Test
	public void testAddTestGroup() {
		
		Experiment e = new Experiment(testId, testTitle, testGroups);
		TestGroup g = new TestGroup("newGroup");
		g.setId(99);
		
		assertEquals(2, e.getTestGroups().size());
		assertFalse(e.getTestGroups().contains(g));
		
		e.addTestGroup(g);
		
		assertEquals(3, e.getTestGroups().size());
		assertTrue(e.getTestGroups().contains(g));
	}
	
	@Test
	public void testRemoveTestGroup() {
		
		Experiment e = new Experiment(testId, testTitle, testGroups);
		TestGroup g = (TestGroup) testGroups.toArray()[0];
		
		assertEquals(2, e.getTestGroups().size());
		assertTrue(e.getTestGroups().contains(g));
		
		e.removeTestGroup(g);
		
		assertEquals(1, e.getTestGroups().size());
		assertFalse(e.getTestGroups().contains(g));
	}
	
	@Test
	public void testClearTestGroups() {
		
		Experiment e = new Experiment(testId, testTitle, testGroups);
		
		assertNotEquals(0, e.getTestGroups().size());
		
		e.clearTestGroups();
		
		assertEquals(0, e.getTestGroups().size());
	}
	
	@Test
	public void testEqualsAndHashCode() {
		
		Experiment e1 = new Experiment(1, testTitle, testGroups);
		Experiment e2 = new Experiment(1, testTitle, testGroups);
		Experiment e3 = new Experiment(2, testTitle, testGroups);
		
		assertTrue(e1.equals(e1));
		assertTrue(e1.equals(e2));
		assertFalse(e1.equals(e3));
		
		assertEquals(e1.hashCode(), e2.hashCode());
		assertNotEquals(e1.hashCode(), e3.hashCode());
	}
}






 








