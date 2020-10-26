package ch.usi.hse.db.entities;

import static org.junit.jupiter.api.Assertions.*;

import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class ParticipantTest {

	private static int testId, testExpId, testGroupId;
	private static String testName, testPwd, testGroupName;
	private static Set<Role> testRoles;
	
	@BeforeAll
	public static void init() {
		
		testId = 23;
		testExpId = 24;
		testGroupId = 25;
		testGroupName = "g1";
		testName = "name";
		testPwd = "testPwd";
		testRoles = new HashSet<>(); 
		testRoles.add(new Role(1, "ROLE_1"));
		testRoles.add(new Role(2, "ROLE_2"));
	}
	
	
	
	@Test
	public void testConstructor1() {
		
		Participant p = new Participant();
		
		assertEquals(0, p.getId());
		assertEquals("", p.getUserName());
		assertEquals("", p.getPassword());
		assertNotNull(p.getRoles());
		assertEquals(0, p.getRoles().size());
	}
	
	@Test
	public void testCconstructor2() {
		
		Participant p = new Participant(testId, testName, testPwd, testRoles,
										testExpId, testGroupId, testGroupName);
		
		assertEquals(testId, p.getId());
		assertEquals(testExpId, p.getExperimentId());
		assertEquals(testGroupId, p.getGroupId());
		assertEquals(testGroupName, p.getGroupName());
		assertEquals(testName, p.getUserName());
		assertEquals(testPwd, p.getPassword());
		assertIterableEquals(testRoles, p.getRoles());
	}
	
	@Test
	public void testConstructor3() {
		
		Participant p = new Participant(testId, testName, testPwd, testRoles);
		
		assertEquals(testId, p.getId());
		assertEquals(testName, p.getUserName());
		assertEquals(testPwd, p.getPassword());
		assertIterableEquals(testRoles, p.getRoles());
	}
	
	@Test
	public void testConstructor4() {
		
		Participant p = new Participant(testName, testPwd);
		
		assertEquals(testName, p.getUserName());
		assertEquals(testPwd, p.getPassword());
	}
	
	@Test
	public void testSetters() {
		
		Participant p = new Participant();
		
		assertNotEquals(testExpId, p.getExperimentId());
		assertNotEquals(testGroupId, p.getGroupId());
		
		p.setExperimentId(testExpId);
		p.setGroupId(testGroupId);
		p.setGroupName(testGroupName);
		
		assertEquals(testExpId, p.getExperimentId());
		assertEquals(testGroupId, p.getGroupId());
		assertEquals(testGroupName, p.getGroupName());
	}
}




















