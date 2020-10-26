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
	
	@BeforeEach
	public void setUp() {
		
		testId = 23;
		testName = "testName";
		testParticipants = new HashSet<>();
		
		testParticipants.add(new Participant("p1", "pwd"));
		testParticipants.add(new Participant("p2", "pwd"));
	}
	
	@Test
	public void testConstructor1() {
		
		TestGroup g = new TestGroup();
		
		assertEquals(0, g.getId());
		assertNotNull(g.getParticipants());
	}
	
	@Test
	public void testConstructor2() {
		
		TestGroup g = new TestGroup(testId, testName, testParticipants);
		
		assertEquals(testId, g.getId());
		assertEquals(testName, g.getName());
		assertIterableEquals(testParticipants, g.getParticipants());
	}
	
	@Test
	public void testConstructor3() {
		
		TestGroup g = new TestGroup(testName, testParticipants);
		
		assertEquals(0, g.getId());
		assertEquals(testName, g.getName());
		assertEquals(testParticipants, g.getParticipants());
	}
	
	@Test
	public void testConstructor4() {
		
		TestGroup g = new TestGroup(testName);
		
		assertEquals(0, g.getId());
		assertEquals(testName, g.getName());
		assertNotNull(g.getParticipants());
	}
	
	@Test
	public void testSetters() {
		
		TestGroup g = new TestGroup();
		
		assertNotEquals(testId, g.getId());
		assertNotEquals(testName, g.getName());
		assertEquals(0, g.getParticipants().size());
		
		g.setId(testId);
		g.setName(testName);
		g.setParticipants(testParticipants);
		
		assertEquals(testId, g.getId());
		assertEquals(testName, g.getName());
		assertIterableEquals(testParticipants, g.getParticipants());
	}
	
	@Test
	public void testAddParticipant() {
		
		TestGroup g = new TestGroup(testName, testParticipants);
		Participant newParticipant = new Participant("p3", "pwd");
		
		assertEquals(2, g.getParticipants().size());
		assertFalse(g.getParticipants().contains(newParticipant));
		
		g.addParticipant(newParticipant);
		
		assertEquals(3, g.getParticipants().size());
		assertTrue(g.getParticipants().contains(newParticipant));
	}
	
	@Test
	public void testRemoveParticipant() {
		
		TestGroup g = new TestGroup(testName, testParticipants);
		Participant p = (Participant) testParticipants.toArray()[0];  
		
		assertEquals(2, g.getParticipants().size());
		assertTrue(g.getParticipants().contains(p));
		
		g.removeParticipant(p);
		
		assertEquals(1, g.getParticipants().size());
		assertFalse(g.getParticipants().contains(p));
	}
	
	@Test
	public void testEqualsAndHashCode() {
		
		TestGroup g1 = new TestGroup(1, "name1", testParticipants);
		TestGroup g2 = new TestGroup(1, "name1", testParticipants);
		TestGroup g3 = new TestGroup(2, "name1", testParticipants);
		
		assertTrue(g1.equals(g1));
		assertTrue(g1.equals(g2));
		assertFalse(g1.equals(g3));
		
		assertEquals(g1.hashCode(), g2.hashCode());
		assertNotEquals(g1.hashCode(), g3.hashCode());
	}
}











