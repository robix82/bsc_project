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

	@ExceptionHandler(UserExistsException.class) 
	public ResponseEntity<Object> handleUserExists(UserExistsException e) {
		
		HttpStatus status = HttpStatus.BAD_REQUEST;
		
		ApiError err = new ApiError(status, e);
		
		return new ResponseEntity<>(err, status);
	}
	
	@ExceptionHandler(NoSuchUserException.class)
	public ResponseEntity<Object> handleNoSuchUser(NoSuchUserException e)  {
		
		HttpStatus status = HttpStatus.NOT_FOUND;
		
		ApiError err = new ApiError(status, e);
		
		return new ResponseEntity<>(err, status);
	}
}





