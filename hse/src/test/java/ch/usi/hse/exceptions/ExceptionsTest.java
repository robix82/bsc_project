package ch.usi.hse.exceptions;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

public class ExceptionsTest {

	@Test
	public void testNoSuchUserException() {
		
		int id = 23;
		String name = "abc";
		String msg1 = "No User with id 23 found";
		String msg2 = "No User named abc found";
		boolean exc1 = false;
		boolean exc2 = false;
		
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
		
		assertTrue(exc1);
		assertTrue(exc2);
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













