package ch.usi.hse.db.entities;

import static org.junit.jupiter.api.Assertions.*;

import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class AdministratorTest {

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
		
		Administrator a = new Administrator();
		
		assertEquals(0, a.getId());
		assertEquals("", a.getUserName());
		assertEquals("", a.getPassword());
		assertNotNull(a.getRoles());
		assertEquals(0, a.getRoles().size());
	}
	
	@Test
	public void testConstructor2() {
		
		Administrator a = new Administrator(testId, testName, testPwd, testRoles);
		
		assertEquals(testId, a.getId());
		assertEquals(testName, a.getUserName());
		assertEquals(testPwd, a.getPassword());
		assertIterableEquals(testRoles, a.getRoles());
	}
	
	@Test
	public void testConstructor3() {
		
		Administrator a = new Administrator(testName, testPwd, testRoles);
		
		assertEquals(testName, a.getUserName());
		assertEquals(testPwd, a.getPassword());
		assertIterableEquals(testRoles, a.getRoles());
	}
}









