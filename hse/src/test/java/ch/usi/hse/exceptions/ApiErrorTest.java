package ch.usi.hse.exceptions;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

public class ApiErrorTest {

	private HttpStatus testStatus = HttpStatus.INTERNAL_SERVER_ERROR;
	private String testType = "SomeException";
	private String testMessage = "SomErrorMessage";	
	
	@Test
	public void testConstructor1() {
		
		ApiError err = new ApiError();
		
		assertNull(err.getStatus());
		assertNull(err.getErrorType());
		assertNull(err.getErrorMessage());
		assertNull(err.getTimestamp());
	}

	@Test
	public void testConstructor2() {
		
		ApiError err = new ApiError(testStatus, testType, testMessage);
		
		assertEquals(testStatus, err.getStatus());
		assertEquals(testType, err.getErrorType());
		assertEquals(testMessage, err.getErrorMessage());
		assertEquals(approxNow(), approx(err.getTimestamp()));
	}

	@Test
	public void testConstructor3() {
		
		NoSuchUserException e = new NoSuchUserException(23);
		
		ApiError err = new ApiError(testStatus, e);
		
		assertEquals(testStatus, err.getStatus());
		assertEquals("NoSuchUserException", err.getErrorType());
		assertEquals(e.getMessage(), err.getErrorMessage());
		assertEquals(approxNow(), approx(err.getTimestamp()));
	}
	
	@Test
	public void testSetters() {
		
		ApiError err = new ApiError();
		
		assertNull(err.getStatus());
		assertNull(err.getErrorType());
		assertNull(err.getErrorMessage());
		assertNull(err.getTimestamp());
		
		LocalDateTime t = LocalDateTime.of(2020, 10, 25, 9, 33);
		
		err.setStatus(testStatus);
		err.setErrorType(testType);
		err.setErrorMessage(testMessage);
		err.setTimstamp(t);
		
		assertEquals(testStatus, err.getStatus());
		assertEquals(testType, err.getErrorType());
		assertEquals(testMessage, err.getErrorMessage());
		assertEquals(t, err.getTimestamp());
	}
	
	private LocalDateTime approx(LocalDateTime t) {
		
		return t.truncatedTo(ChronoUnit.SECONDS);
	}
	
	private LocalDateTime approxNow() {
		
		return LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);
	}
}















