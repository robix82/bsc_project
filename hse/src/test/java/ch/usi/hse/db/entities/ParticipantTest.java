package ch.usi.hse.db.entities;

import static org.junit.jupiter.api.Assertions.*;

import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class ParticipantTest {

	private static int testId;
	private static String testName;
	private static String testPwd;
	private static Set<Role> testRoles;
	
	@BeforeAll
	public static void init() {
		
		testId = 23;
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
	public void testConstructor2() {
		
		Participant p = new Participant(testId, testName, testPwd, testRoles);
		
		assertEquals(testId, p.getId());
		assertEquals(testName, p.getUserName());
		assertEquals(testPwd, p.getPassword());
		assertIterableEquals(testRoles, p.getRoles());
	}
	
	@Test
	public void testConstructor3() {
		
		Participant p = new Participant(testName, testPwd);
		
		assertEquals(testName, p.getUserName());
		assertEquals(testPwd, p.getPassword());
	}
}




















