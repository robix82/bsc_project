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
	
	@Test
	public void testFileWriteException() {
		
		String fName = "fName";
		String msg = "Error writing file " + fName;
		boolean exc = false;
		
		try {
			throw new FileWriteException(fName);
		}
		catch (Exception e) {
			
			assertEquals(msg, e.getMessage());
			exc = true;
		}
		
		assertTrue(exc);
	}
	
	@Test
	public void testFileReadException() {
		
		String fName = "fName";
		String msg = "Error reading file " + fName;
		boolean exc = false;
		
		try {
			throw new FileReadException(fName);
		}
		catch (Exception e) {
			
			assertEquals(msg, e.getMessage());
			exc = true;
		}
		
		assertTrue(exc);
	}
	
	@Test
	public void testNoSuchFileException() {
		
		String fName = "fName";
		String msg = "File " + fName + " not found";
		boolean exc = false;
		
		try {
			throw new NoSuchFileException(fName);
		}
		catch (Exception e) {
			
			assertEquals(msg, e.getMessage());
			exc = true;
		}
		
		assertTrue(exc);
	}
	
	@Test
	public void testFileDeleteException() {
		
		String fName = "fName";
		String msg = "Error deleting file " + fName;
		boolean exc = false;
		
		try {
			throw new FileDeleteException(fName);
		}
		catch (Exception e) {
			
			assertEquals(msg, e.getMessage());
			exc = true;
		}
		
		assertTrue(exc);
	}
	
	@Test
	public void testLanguageNotSupportedException() {
		
		String language = "XY";
		String msg = "Language " + language + " is not supported";
		boolean exc = false;
		
		try {
			throw new LanguageNotSupportedException(language);
		}
		catch (Exception e) {
			
			assertEquals(msg, e.getMessage());
			exc = true;
		}
		
		assertTrue(exc);
	}
	
	@Test
	public void testDocCollectionExxistsException() {
		
		int id = 23;
		String name = "name";
		String msg1 = "A DocCollection with id " + id + " already exists";
		String msg2 = "A DocCollection with name " + name + " already exists";
		boolean exc1 = false;
		boolean exc2 = false;
		
		try {
			throw new DocCollectionExistsException(id);
		}
		catch (Exception e) {
			
			assertEquals(msg1, e.getMessage());
			exc1 = true;
		}
		
		try {
			throw new DocCollectionExistsException(name);
		}
		catch (Exception e) {
			
			assertEquals(msg2, e.getMessage());
			exc2 = true;
		}
		
		assertTrue(exc1);
		assertTrue(exc2);
	}
	
	@Test
	public void testNoSuchDocCollectionExcception() {
		
		int id = 23;
		String msg = "No DocCollection with id " + id + " found";
		boolean exc = false;
		
		try {
			throw new NoSuchDocCollectionException(id);
		}
		catch (Exception e) {
			
			assertEquals(msg, e.getMessage());
			exc = true;
		}
		
		assertTrue(exc);
	}
	
	@Test
	public void testExperimentExistsException1() {
		
		int id = 23;
		String msg = "An experiment with id " + id + " already exists";
		boolean exc = false;
		
		try {
			throw new ExperimentExistsException(id);
		}
		catch (Exception e) {
			
			assertEquals(msg, e.getMessage());
			exc = true;
		}
		
		assertTrue(exc);
	}
	
	@Test
	public void testExperimentExistsException2() {
		
		String title = "name";
		String msg = "An experiment with title " + title + " already exists";
		boolean exc = false;
		
		try {
			throw new ExperimentExistsException(title);
		}
		catch (Exception e) {
			
			assertEquals(msg, e.getMessage());
			exc = true;
		}
		
		assertTrue(exc);
	}
	
	@Test
	public void testNoSuchExperimentException() {
		
		int id = 23;
		String msg = "No Experiment with id " + id + " found";
		boolean exc = false;
		
		try {
			throw new NoSuchExperimentException(id);
		}
		catch(Exception e) {
			
			assertEquals(msg, e.getMessage());
			exc = true;
		}
		
		assertTrue(exc);
	}
	
	@Test
	public void testTestGroupExistsException() {
		
		int id = 23;
		String msg = "A TestGroup with id " + id + " already exists";
		boolean exc = false;
		
		try {
			throw new TestGroupExistsException(id);
		}
		catch (Exception e) {
			
			assertEquals(msg, e.getMessage());
			exc = true;
		}
		
		assertTrue(exc);
	}
	
	@Test
	public void testNoSuchTestGroupException() {
		
		int id = 23;
		String msg = "No TestGroup with id " + id + " found";
		boolean exc = false;
		
		try {
			throw new NoSuchTestGroupException(id);
		}
		catch (Exception e) {
		
			assertEquals(msg, e.getMessage());
			exc = true;
		}
		
		assertTrue(exc);
	}
}



 









