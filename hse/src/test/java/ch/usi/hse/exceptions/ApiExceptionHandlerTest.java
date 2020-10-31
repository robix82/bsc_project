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
		
		ResponseEntity<Object> res = handler.handleUserExistsException(ex);
		
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
		
		ResponseEntity<Object> res = handler.handleNoSuchUserException(ex);
		
		HttpStatus actualStatus = res.getStatusCode();
		ApiError actualErr = (ApiError) res.getBody();
		
		assertEquals(expectedStatus, actualStatus);
		assertEquals(expectedErr.getStatus(), actualErr.getStatus());
		assertEquals(expectedErr.getErrorType(), actualErr.getErrorType());
		assertEquals(expectedErr.getErrorMessage(), actualErr.getErrorMessage());
	}
	
	@Test
	public void testHandleFileWriteException() {
		
		FileWriteException ex = new FileWriteException("file");
		HttpStatus expectedStatus = HttpStatus.INTERNAL_SERVER_ERROR;
		ApiError expectedErr = new ApiError(expectedStatus, ex);
		
		ResponseEntity<Object> res = handler.handleFileWriteException(ex);
		
		HttpStatus actualStatus = res.getStatusCode();
		ApiError actualErr = (ApiError) res.getBody();
		
		assertEquals(expectedStatus, actualStatus);
		assertEquals(expectedErr.getStatus(), actualErr.getStatus());
		assertEquals(expectedErr.getErrorType(), actualErr.getErrorType());
		assertEquals(expectedErr.getErrorMessage(), actualErr.getErrorMessage());
	}
	
	@Test
	public void testHandleFileReadException() {
		
		FileReadException ex = new FileReadException("file");
		HttpStatus expectedStatus = HttpStatus.INTERNAL_SERVER_ERROR;
		ApiError expectedErr = new ApiError(expectedStatus, ex);
		
		ResponseEntity<Object> res = handler.handleFileReadException(ex);
		
		HttpStatus actualStatus = res.getStatusCode();
		ApiError actualErr = (ApiError) res.getBody();
		
		assertEquals(expectedStatus, actualStatus);
		assertEquals(expectedErr.getStatus(), actualErr.getStatus());
		assertEquals(expectedErr.getErrorType(), actualErr.getErrorType());
		assertEquals(expectedErr.getErrorMessage(), actualErr.getErrorMessage());
	}
	
	@Test
	public void testHandleNoSuchFileException() {
		
		NoSuchFileException ex = new NoSuchFileException("file");
		HttpStatus expectedStatus = HttpStatus.NOT_FOUND;
		ApiError expectedErr = new ApiError(expectedStatus, ex);
		
		ResponseEntity<Object> res = handler.handleNoSuchFileException(ex);
		
		HttpStatus actualStatus = res.getStatusCode();
		ApiError actualErr = (ApiError) res.getBody();
		
		assertEquals(expectedStatus, actualStatus);
		assertEquals(expectedErr.getStatus(), actualErr.getStatus());
		assertEquals(expectedErr.getErrorType(), actualErr.getErrorType());
		assertEquals(expectedErr.getErrorMessage(), actualErr.getErrorMessage());
	}
	
	@Test
	public void testHandleFileDeleteException() {
		
		FileDeleteException ex = new FileDeleteException("file");
		HttpStatus expectedStatus = HttpStatus.INTERNAL_SERVER_ERROR;
		ApiError expectedErr = new ApiError(expectedStatus, ex);
		
		ResponseEntity<Object> res = handler.handleFileDeleteException(ex);
		
		HttpStatus actualStatus = res.getStatusCode();
		ApiError actualErr = (ApiError) res.getBody();
		
		assertEquals(expectedStatus, actualStatus);
		assertEquals(expectedErr.getStatus(), actualErr.getStatus());
		assertEquals(expectedErr.getErrorType(), actualErr.getErrorType());
		assertEquals(expectedErr.getErrorMessage(), actualErr.getErrorMessage());
	}
	
	@Test
	public void testHandleLanguageNotSupportedException() {
		
		LanguageNotSupportedException ex = new LanguageNotSupportedException("XY");
		HttpStatus expectedStatus = HttpStatus.UNPROCESSABLE_ENTITY;
		ApiError expectedErr = new ApiError(expectedStatus, ex);
		
		ResponseEntity<Object> res = handler.handleLanguageNotSupportedException(ex);
		
		HttpStatus actualStatus = res.getStatusCode();
		ApiError actualErr = (ApiError) res.getBody();
		
		assertEquals(expectedStatus, actualStatus);
		assertEquals(expectedErr.getStatus(), actualErr.getStatus());
		assertEquals(expectedErr.getErrorType(), actualErr.getErrorType());
		assertEquals(expectedErr.getErrorMessage(), actualErr.getErrorMessage());
	}
	
	@Test
	public void testHandleDocCollectionExistsException() {
		
		DocCollectionExistsException ex = new DocCollectionExistsException(23);
		HttpStatus expectedStatus = HttpStatus.UNPROCESSABLE_ENTITY;
		ApiError expectedErr = new ApiError(expectedStatus, ex);
		
		ResponseEntity<Object> res = handler.handleDocCollectionExistsException(ex);
		
		HttpStatus actualStatus = res.getStatusCode();
		ApiError actualErr = (ApiError) res.getBody();
		
		assertEquals(expectedStatus, actualStatus);
		assertEquals(expectedErr.getStatus(), actualErr.getStatus());
		assertEquals(expectedErr.getErrorType(), actualErr.getErrorType());
		assertEquals(expectedErr.getErrorMessage(), actualErr.getErrorMessage());
	}
	
	@Test
	public void testHandleNoSuchDocCollectionException() {
		
		NoSuchDocCollectionException ex = new NoSuchDocCollectionException(23);
		HttpStatus expectedStatus = HttpStatus.NOT_FOUND;
		ApiError expectedErr = new ApiError(expectedStatus, ex);
		
		ResponseEntity<Object> res = handler.handleNoSuchDocCollectionException(ex);
		
		HttpStatus actualStatus = res.getStatusCode();
		ApiError actualErr = (ApiError) res.getBody();
		
		assertEquals(expectedStatus, actualStatus);
		assertEquals(expectedErr.getStatus(), actualErr.getStatus());
		assertEquals(expectedErr.getErrorType(), actualErr.getErrorType());
		assertEquals(expectedErr.getErrorMessage(), actualErr.getErrorMessage());
	}
}












