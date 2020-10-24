package ch.usi.hse.db.entities;

import static org.junit.jupiter.api.Assertions.*;

import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class ExperimenterTest {

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
		
		Experimenter e = new Experimenter();
		
		assertEquals(0, e.getId());
		assertEquals("", e.getUserName());
		assertEquals("", e.getPassword());
		assertNotNull(e.getRoles());
		assertEquals(0, e.getRoles().size());
	}
	
	@Test
	public void testConstructor2() {
		
		Experimenter e = new Experimenter(testId, testName, testPwd, testRoles);
		
		assertEquals(testId, e.getId());
		assertEquals(testName, e.getUserName());
		assertEquals(testPwd, e.getPassword());
		assertIterableEquals(testRoles, e.getRoles());
	}
	
	@Test
	public void testConstructor3() {
		
		Experimenter e = new Experimenter(testName, testPwd);
		
		assertEquals(testName, e.getUserName());
		assertEquals(testPwd, e.getPassword());
	}
}












