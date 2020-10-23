package ch.usi.hse.exceptions;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

public class ExceptionsTest {

	@Test
	public void testNoSuchUserException() {
		
		int id = 23;
		String name = "abc";
		String role = "<role>";
		String msg1 = "No User with id 23 found";
		String msg2 = "No User named abc found";
		String msg3 = "No <role> with id 23 found";
		String msg4 = "No <role> named abc found";
		boolean exc1 = false;
		boolean exc2 = false;
		boolean exc3 = false;
		boolean exc4 = false;
		
		try {
			throw new NoSuchUserException(id);
		}
		catch (Exception e) {
			
			assertEquals(msg1, e.getMessage()); // check message
			exc1 = true;						// confirm exception was thrown
		}
		
		try {
			throw new NoSuchUserException(name);
		}
		catch (Exception e) {
			
			assertEquals(msg2, e.getMessage());
			exc2 = true;
		}
		
		try {
			throw new NoSuchUserException(role, id);
		}
		catch (Exception e) {
			
			assertEquals(msg3, e.getMessage());
			exc3 = true;
		}
		
		try {
			throw new NoSuchUserException(role, name);
		}
		catch (Exception e) {
			
			assertEquals(msg4, e.getMessage());
			exc4 = true; 
		}
		
		assertTrue(exc1);
		assertTrue(exc2);
		assertTrue(exc3);
		assertTrue(exc4);
	}
	
	@Test
	public void testUserExistsException() {
		
		int id = 23;
		String name = "abc";
		String msg1 = "A User with id 23 already exists";
		String msg2 = "A User named abc already exists";
		boolean exc1 = false;
		boolean exc2 = false;
		
		try {
			throw new UserExistsException(id);
		}
		catch (Exception e) {	
			
			assertEquals(msg1, e.getMessage());
			exc1 = true;
		}
		
		try {
			throw new UserExistsException(name);
		}
		catch (Exception e) {
			
			assertEquals(msg2, e.getMessage());
			exc2 = true;
		}
		
		assertTrue(exc1);
		assertTrue(exc2);
	}
}













