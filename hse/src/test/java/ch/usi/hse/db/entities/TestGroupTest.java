package ch.usi.hse.db.entities;

import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import java.util.HashSet;

public class TestGroupTest {

	private int testId;
	private String testName;
	private Set<Participant> testParticipants;
	private Experiment testExperiment;
	private Set<DocCollection> testDocCollections;
	
	@BeforeEach
	public void setUp() {
		
		testId = 23;
		testExperiment = new Experiment();
		testExperiment.setId(42);
		testName = "testName";
		testParticipants = new HashSet<>();
		testDocCollections = new HashSet<>();
		
		testParticipants.add(new Participant("p1", "pwd"));
		testParticipants.add(new Participant("p2", "pwd"));
		
		DocCollection c1 = new DocCollection("c1", "l1");
		DocCollection c2 = new DocCollection("c2", "l2");
		c1.setId(1);
		c2.setId(2);
		
		testDocCollections.add(c1);
		testDocCollections.add(c2);
	}
	 
	@Test 
	public void testConstructor1() {
		
		TestGroup g = new TestGroup(); 
		
		assertEquals(0, g.getId());
		assertNotNull(g.getParticipants());
		assertNotNull(g.getDocCollections());
	}
	
	@Test
	public void testConstructor2() { 
		
		TestGroup g = new TestGroup(testId, testName, testParticipants, testExperiment);
		
		assertEquals(testId, g.getId());
		assertEquals(testName, g.getName());
		assertIterableEquals(testParticipants, g.getParticipants());
		assertEquals(testExperiment, g.getExperiment());
		assertEquals(testExperiment.getId(), g.getExperimentId());
		assertEquals(testExperiment.getTitle(), g.getExperimentTitle());
		assertNotNull(g.getDocCollections());
		
		for (Participant p : g.getParticipants()) {
			
			assertEquals(g, p.getTestGroup());
			assertEquals(g.getId(), p.getTestGroupId());
			assertEquals(g.getName(), p.getTestGroupName());
			assertEquals(testExperiment.getId(), p.getExperimentId());
			assertEquals(testExperiment.getTitle(), p.getExperimentTitle());
		}
	}
	
	@Test
	public void testConstructor3() {
		
		TestGroup g = new TestGroup(testName);
		
		assertEquals(0, g.getId());
		assertEquals(testName, g.getName());
		assertNotNull(g.getParticipants());
		assertNotNull(g.getDocCollections());
	}
	
	@Test
	public void testSetters() {
		
		TestGroup g = new TestGroup();
		
		int expId = 123;
		String expTitle = "abc";
		
		assertNotEquals(testId, g.getId());
		assertNotEquals(testName, g.getName());
		assertEquals(0, g.getParticipants().size());
		assertEquals(0, g.getDocCollections().size());
		assertNotEquals(testExperiment, g.getExperiment());
		assertNotEquals(testExperiment.getId(), g.getExperimentId());
		assertNull(g.getExperimentTitle());
		
		g.setId(testId);
		g.setName(testName);
		g.setParticipants(testParticipants);
		g.setExperiment(testExperiment);
		g.setDocCollections(testDocCollections);
		
		assertEquals(testId, g.getId());
		assertEquals(testName, g.getName());
		assertIterableEquals(testParticipants, g.getParticipants());
		assertEquals(testExperiment, g.getExperiment());
		assertEquals(testExperiment.getId(), g.getExperimentId());
		assertEquals(testExperiment.getTitle(), g.getExperimentTitle());
		assertIterableEquals(testDocCollections, g.getDocCollections());
		
		for (Participant p : g.getParticipants()) {
			
			assertEquals(g, p.getTestGroup());
			assertEquals(testId, p.getTestGroupId());
			assertEquals(testName, p.getTestGroupName());
			assertEquals(testExperiment.getId(), p.getExperimentId());
			assertEquals(testExperiment.getTitle(), p.getExperimentTitle());
		}
		
		g.setExperimentId(expId);
		g.setExperimentTitle(expTitle);
		
		assertEquals(expId, g.getExperimentId());
		assertEquals(expTitle, g.getExperimentTitle());
		
		for (Participant p : g.getParticipants()) {
			
			assertEquals(expId, p.getExperimentId());
			assertEquals(expTitle, p.getExperimentTitle());
		}
	}
	 
	@Test
	public void testAddParticipant() {
		
		TestGroup g = new TestGroup(testId, testName, testParticipants, testExperiment);
		Participant newParticipant = new Participant("p3", "pwd");
		
		assertEquals(2, g.getParticipants().size());
		assertFalse(g.getParticipants().contains(newParticipant));
		
		g.addParticipant(newParticipant);
		
		assertEquals(3, g.getParticipants().size());
		assertTrue(g.getParticipants().contains(newParticipant));
		
		assertEquals(g, newParticipant.getTestGroup());
		assertEquals(g.getId(), newParticipant.getTestGroupId());
		assertEquals(g.getName(), newParticipant.getTestGroupName());
		assertEquals(testExperiment.getId(), newParticipant.getExperimentId());
		assertEquals(testExperiment.getTitle(), newParticipant.getExperimentTitle());
	}
	
	@Test
	public void testRemoveParticipant() {
		
		TestGroup g = new TestGroup(testId, testName, testParticipants, testExperiment);
		Participant p = (Participant) testParticipants.toArray()[0];  
		
		assertEquals(2, g.getParticipants().size());
		assertTrue(g.getParticipants().contains(p));
		
		g.removeParticipant(p);
		
		assertEquals(1, g.getParticipants().size());
		assertFalse(g.getParticipants().contains(p));
	}
	
	@Test
	public void testClearParticipants() {
		
		TestGroup g = new TestGroup(testId, testName, testParticipants, testExperiment);
		
		assertNotEquals(0, g.getParticipants().size());
		g.clearParticipants();
		assertEquals(0, g.getParticipants().size());
	}
	
	@Test
	public void testAddDocCollection() {
		
		TestGroup g = new TestGroup("g");
		DocCollection c = new DocCollection();
		c.setId(23);
		
		assertEquals(0, g.getDocCollections().size());
		
		g.addDocCollection(c);
		
		assertEquals(1, g.getDocCollections().size());
		assertTrue(g.getDocCollections().contains(c));
	}
	
	@Test
	public void testRemoveDocCollection() {
		
		TestGroup g = new TestGroup("g");
		g.setDocCollections(testDocCollections);
		
		long sizeBefore = g.getDocCollections().size();
		
		g.removeDocCollection((DocCollection) testDocCollections.toArray()[0]);
		
		assertEquals(sizeBefore -1, g.getDocCollections().size());
	}
	
	@Test
	public void testClearDocCollections() {
		
		TestGroup g = new TestGroup("g");
		g.setDocCollections(testDocCollections);
		
		assertNotEquals(0, g.getDocCollections().size());
		
		g.clearDocCollections();
		
		assertEquals(0, g.getDocCollections().size());
	}
	 
	@Test 
	public void testEqualsAndHashCode() {
		
		TestGroup g1 = new TestGroup(1, "name1", testParticipants, testExperiment);
		TestGroup g2 = new TestGroup(1, "name1", testParticipants, testExperiment);
		TestGroup g3 = new TestGroup(2, "name2", testParticipants, testExperiment); 
		
		assertTrue(g1.equals(g1));
		assertTrue(g1.equals(g2));
		assertFalse(g1.equals(g3));
		
		assertEquals(g1.hashCode(), g2.hashCode());
		assertNotEquals(g1.hashCode(), g3.hashCode());
	}
}

 









