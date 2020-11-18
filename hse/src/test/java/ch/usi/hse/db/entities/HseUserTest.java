package ch.usi.hse.db.entities;

import static org.junit.jupiter.api.Assertions.*;

import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class HseUserTest {

	private class TestUser extends HseUser {
		
		public TestUser() {
			super();
		}
		
		public TestUser(int id, String userName, String password, Set<Role> roles) {
			super(id, userName, password, roles);
		}
		
		public TestUser(String userName, String password) {
			super(userName, password);
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
		
		HseUser u = new TestUser();
		
		assertEquals(0, u.getId());
		assertEquals("", u.getUserName());
		assertEquals("", u.getPassword());
		assertNotNull(u.getRoles());
		assertTrue(u.getActive());
		assertEquals(0, u.getRoles().size());
	}
	
	@Test
	public void testConstructor2() {
		
		HseUser u = new TestUser(testId, testName, testPwd, testRoles);
		
		assertEquals(testId, u.getId());
		assertEquals(testName, u.getUserName());
		assertEquals(testPwd, u.getPassword());
		assertTrue(u.getActive());assertTrue(u.getActive());
		assertIterableEquals(testRoles, u.getRoles());
	}
	
	@Test
	public void testConstructor3() {
		
		HseUser u = new TestUser(testName, testPwd);
		assertEquals(testName, u.getUserName());
		assertEquals(testPwd, u.getPassword());
		assertTrue(u.getActive());
	}
	
	@Test
	public void testSetters() {
		
		HseUser u = new TestUser();
		
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
		
		HseUser u = new TestUser();
		u.setRoles(testRoles);
		
		Role newRole = new Role(23, "NEW_ROLE");
		
		assertFalse(u.getRoles().contains(newRole));
		
		u.addRole(newRole);
		
		assertTrue(u.getRoles().contains(newRole));
	}
	
	@Test
	public void testRemoveRole() {
		
		HseUser u = new TestUser();
		u.setRoles(testRoles);
		
		Role r = (Role) testRoles.toArray()[0];
		
		assertTrue(u.getRoles().contains(r));
		
		u.removeRole(r);
		
		assertFalse(u.getRoles().contains(r));
	}
	
	@Test
	public void testEqualsAndHashCode() {
		
		HseUser u1 = new TestUser(1, "name1", "pwd", testRoles); 
		HseUser u2 = new TestUser(2, "name1", "pwd", testRoles);
		HseUser u3 = new TestUser(3, "name2", "pwd", testRoles);
		
		assertTrue(u1.equals(u1));
		assertTrue(u1.equals(u2));
		assertFalse(u1.equals(u3));
		
		assertEquals(u1.hashCode(), u2.hashCode());
		assertNotEquals(u1.hashCode(), u3.hashCode());
	}
}












