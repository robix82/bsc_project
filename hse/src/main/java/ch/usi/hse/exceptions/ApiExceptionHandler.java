package ch.usi.hse.exceptions;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

/**
 * used for error handling at controller level:
 * makes sure that the client receives a meaningful error message
 * when a REST request fails
 * 
 * based on https://www.toptal.com/java/spring-boot-rest-api-error-handling
 * 
 * @author robert.jans@usi.ch
 *
 */
@Order(Ordered.HIGHEST_PRECEDENCE)
@ControllerAdvice
public class ApiExceptionHandler extends ResponseEntityExceptionHandler {

	@ExceptionHandler(EntityExistsException.class) 
	public ResponseEntity<Object> handleEntityExistsException(EntityExistsException e) {
		
		HttpStatus status = HttpStatus.UNPROCESSABLE_ENTITY;
		
		ApiError err = new ApiError(status, e);  
		
		return new ResponseEntity<>(err, status);
	}
	 
	@ExceptionHandler(NoSuchEntityException.class)
	public ResponseEntity<Object> handleNoSuchEntityException(NoSuchEntityException e)  {
		
		HttpStatus status = HttpStatus.NOT_FOUND;
		
		ApiError err = new ApiError(status, e);
		
		return new ResponseEntity<>(err, status);
	}
	
	@ExceptionHandler(FileWriteException.class)
	public ResponseEntity<Object> handleFileWriteException(FileWriteException e) {
		
		HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
		
		ApiError err = new ApiError(status, e);
		
		return new ResponseEntity<>(err, status);
	}
	
	@ExceptionHandler(FileReadException.class)
	public ResponseEntity<Object> handleFileReadException(FileReadException e) {
		
		HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
		
		ApiError err = new ApiError(status, e);
		
		return new ResponseEntity<>(err, status);
	}
	
	@ExceptionHandler(NoSuchFileException.class)
	public ResponseEntity<Object> handleNoSuchFileException(NoSuchFileException e) {
		
		HttpStatus status = HttpStatus.NOT_FOUND;
		
		ApiError err = new ApiError(status, e);
		
		return new ResponseEntity<>(err, status);
	}
	
	@ExceptionHandler(FileDeleteException.class)
	public ResponseEntity<Object> handleFileDeleteException(FileDeleteException e) {
		
		HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
		
		ApiError err = new ApiError(status, e);
		
		return new ResponseEntity<>(err, status);
	}
	
	@ExceptionHandler(LanguageNotSupportedException.class)
	public ResponseEntity<Object> handleLanguageNotSupportedException(LanguageNotSupportedException e) {
		
		HttpStatus status = HttpStatus.UNPROCESSABLE_ENTITY;
		
		ApiError err = new ApiError(status, e);
		
		return new ResponseEntity<>(err, status);
	}
	
	@ExceptionHandler(ExperimentStatusException.class)
	public ResponseEntity<Object> handleExperimentStatusException(ExperimentStatusException e) {
		
		HttpStatus status = HttpStatus.UNPROCESSABLE_ENTITY;
		
		ApiError err = new ApiError(status, e);
		
		return new ResponseEntity<>(err, status);
	}
	
	@ExceptionHandler(ConfigParseException.class)
	public ResponseEntity<Object> handleConfigParseException(ConfigParseException e) {
		
		HttpStatus status = HttpStatus.UNPROCESSABLE_ENTITY;
		
		ApiError err = new ApiError(status, e);
		
		return new ResponseEntity<>(err, status);
	}
}










