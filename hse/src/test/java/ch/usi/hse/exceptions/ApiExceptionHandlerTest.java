package ch.usi.hse.exceptions;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class ApiExceptionHandlerTest {

	private ApiExceptionHandler handler;
	
	@BeforeEach
	public void setUp() {
		
		handler = new ApiExceptionHandler();
	}
	
	@Test
	public void testHandleUserExists() {
		
		UserExistsException ex = new UserExistsException(23);
		HttpStatus expectedStatus = HttpStatus.UNPROCESSABLE_ENTITY;
		ApiError expectedErr = new ApiError (expectedStatus, ex);
		
		ResponseEntity<Object> res = handler.handleUserExists(ex);
		
		HttpStatus actualStatus = res.getStatusCode(); 
		ApiError actualErr = (ApiError) res.getBody();
		
		assertEquals(expectedStatus, actualStatus);
		assertEquals(expectedErr.getStatus(), actualErr.getStatus());
		assertEquals(expectedErr.getErrorType(), actualErr.getErrorType());
		assertEquals(expectedErr.getErrorMessage(), actualErr.getErrorMessage());
	}
	
	@Test
	public void testHandleNoSuchUser() {
		
		NoSuchUserException ex = new NoSuchUserException(23);
		HttpStatus expectedStatus = HttpStatus.NOT_FOUND;
		ApiError expectedErr = new ApiError (expectedStatus, ex);
		
		ResponseEntity<Object> res = handler.handleNoSuchUser(ex);
		
		HttpStatus actualStatus = res.getStatusCode();
		ApiError actualErr = (ApiError) res.getBody();
		
		assertEquals(expectedStatus, actualStatus);
		assertEquals(expectedErr.getStatus(), actualErr.getStatus());
		assertEquals(expectedErr.getErrorType(), actualErr.getErrorType());
		assertEquals(expectedErr.getErrorMessage(), actualErr.getErrorMessage());
	}
}












