package ch.usi.hse.config;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

@Component("HseAuthenticationFailureHandler")
public class HseAuthenticationFailureHandler implements AuthenticationFailureHandler {

	@Value("${baseUrl}")
	private String baseUrl;
	
	@Override
    public void onAuthenticationFailure(HttpServletRequest request,
    									HttpServletResponse response,
    									AuthenticationException exception) 
    		throws IOException, ServletException {
 
		System.out.println("LOGIN FAILURE");
		
		response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
		response.sendRedirect(baseUrl + "login");
    }
}
