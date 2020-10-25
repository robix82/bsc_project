package ch.usi.hse.exceptions;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;

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
public class ApiError {

	private HttpStatus status;
	private String errorType;
	private String errorMessage;
	private LocalDateTime timestamp;
	
	public ApiError() {}
	
	public ApiError(HttpStatus status, 
					String errorType, 
					String errorMessage) {
		
		this.status = status;
		this.errorType = errorType;
		this.errorMessage = errorMessage;
		timestamp = LocalDateTime.now();
	}
	
	public ApiError(HttpStatus status, Exception ex) {
		
		this.status = status;
		errorType = ex.getClass().getSimpleName();
		errorMessage = ex.getMessage();
		timestamp = LocalDateTime.now();
	}
	
	public HttpStatus getStatus() {
		return status;
	}
	
	public String getErrorType() {
		return errorType;
	}
	
	public String getErrorMessage() {
		return errorMessage;
	}
	
	public LocalDateTime getTimestamp() {
		return timestamp;
	}
	
	public void setStatus(HttpStatus status) {
		this.status = status;
	}
	
	public void setErrorType(String errorType) {
		this.errorType = errorType;
	}
	
	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}
	
	public void setTimstamp(LocalDateTime timestamp) {
		this.timestamp = timestamp;
	}
}














