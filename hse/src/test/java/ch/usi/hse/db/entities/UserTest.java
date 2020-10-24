package ch.usi.hse.db.entities;

import static org.junit.jupiter.api.Assertions.*;

import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class UserTest {

	private class TestUser extends User {
		
		public TestUser() {
			super();
		}
		
		public TestUser(int id, String userName, String password, Set<Role> roles) {
			super(id, userName, password, roles);
		}
		
		public TestUser(String userName, String password, Set<Role> roles) {
			super(userName, password, roles);
		}
	}
	
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
		
		User u = new TestUser();
		
		assertEquals(0, u.getId());
		assertEquals("", u.getUserName());
		assertEquals("", u.getPassword());
		assertNotNull(u.getRoles());
		assertTrue(u.getActive());
		assertEquals(0, u.getRoles().size());
	}
	
	@Test
	public void testConstructor2() {
		
		User u = new TestUser(testId, testName, testPwd, testRoles);
		
		assertEquals(testId, u.getId());
		assertEquals(testName, u.getUserName());
		assertEquals(testPwd, u.getPassword());
		assertTrue(u.getActive());assertTrue(u.getActive());
		assertIterableEquals(testRoles, u.getRoles());
	}
	
	@Test
	public void testConstructor3() {
		
		User u = new TestUser(testName, testPwd, testRoles);
		assertEquals(testName, u.getUserName());
		assertEquals(testPwd, u.getPassword());
		assertTrue(u.getActive());
		assertIterableEquals(testRoles, u.getRoles());
	}
	
	@Test
	public void testSetters() {
		
		User u = new TestUser();
		
		assertNotEquals(testId, u.getId());
		assertNotEquals(testName, u.getUserName());
		assertNotEquals(testPwd, u.getPassword());
		assertNotEquals(testRoles, u.getRoles());
		
		u.setId(testId);
		u.setUserName(testName);
		u.setPassword(testPwd);
		u.setActive(false);
		u.setRoles(testRoles);
		
		assertEquals(testId, u.getId());
		assertEquals(testName, u.getUserName());
		assertEquals(testPwd, u.getPassword());
		assertFalse(u.getActive());
		assertIterableEquals(testRoles, u.getRoles());
	}
	
	@Test
	public void testAddRole() {
		
		User u = new TestUser();
		u.setRoles(testRoles);
		
		Role newRole = new Role(23, "NEW_ROLE");
		
		assertFalse(u.getRoles().contains(newRole));
		
		u.addRole(newRole);
		
		assertTrue(u.getRoles().contains(newRole));
	}
	
	@Test
	public void testRemoveRole() {
		
		User u = new TestUser();
		u.setRoles(testRoles);
		
		Role r = (Role) testRoles.toArray()[0];
		
		assertTrue(u.getRoles().contains(r));
		
		u.removeRole(r);
		
		assertFalse(u.getRoles().contains(r));
	}
}












