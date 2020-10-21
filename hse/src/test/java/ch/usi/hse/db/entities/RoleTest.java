package ch.usi.hse.db.entities;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

public class RoleTest {

	private int testId = 23;
	private String testRole = "test";
	
	@Test
	public void testConstructor1() {
		
		Role r = new Role();
		
		assertEquals(0, r.getId());
		assertNull(r.getRole());
	}
	
	@Test
	public void testConstructor2() {
		
		Role r = new Role(testId, testRole);
		
		assertEquals(testId, r.getId());
		assertEquals(testRole, r.getRole());
	}
	
	@Test
	public void testSetters() {
		
		Role r = new Role();
		
		assertNotEquals(testId, r.getId());
		assertNotEquals(testRole, r.getRole());
		
		r.setId(testId);
		r.setRole(testRole);
		
		assertEquals(testId, r.getId());
		assertEquals(testRole, r.getRole());
	}
	
	@Test
	public void testEqualsAndHashCode() {
		
		Role r1 = new Role(1, "test1");
		Role r2 = new Role(1, "test2");
		Role r3 = new Role(2, "test3");
		
		assertTrue(r1.equals(r1));
		assertTrue(r1.equals(r2));
		assertFalse(r1.equals(r3));
		
		assertEquals(r1.hashCode(), r1.hashCode());
		assertEquals(r1.hashCode(), r2.hashCode());
		assertNotEquals(r1.hashCode(), r3.hashCode());
	}
}













