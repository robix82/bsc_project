package ch.usi.hse.config;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;

import ch.usi.hse.db.entities.Experiment;
import ch.usi.hse.db.entities.Participant;
import ch.usi.hse.db.entities.SessionEvent;
import ch.usi.hse.db.repositories.ExperimentRepository;
import ch.usi.hse.db.repositories.ParticipantRepository;

/**
 * based on https://mainul35.medium.com/spring-security-demonstrating-custom-authentication-success-handler-3b6fcb572a53
 * 
 * @author robert.jans@usi.ch
 *
 */
@Component("HseAuthenticationSuccessHandler")
public class HseAuthenticationSuccessHandler implements AuthenticationSuccessHandler {
	
	@Autowired
	private ParticipantRepository participantRepo; 
	
	@Autowired
	private ExperimentRepository experimentRepo; 
	
	@Override
	public void onAuthenticationSuccess(HttpServletRequest request,
										HttpServletResponse response,
										Authentication authentication)
		throws IOException {
		
		String uName = authentication.getName();
		System.out.println("LOGIN: " + uName);
		
		User authUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();	
		
		HttpSession session = request.getSession();
		session.setAttribute("user", authUser);
		session.setAttribute("username", uName);
		session.setAttribute("authorities", authentication.getAuthorities());
		
		response.setStatus(HttpServletResponse.SC_OK);
		response.sendRedirect("/");
		
		if (participantRepo.existsByUserName(uName)) {
			
			Participant participant = participantRepo.findByUserName(uName);
			participant.setOnline(true);
			participantRepo.save(participant);
			int experimentId = participant.getExperimentId();
			
			if (experimentRepo.existsById(experimentId)) {
				
				Experiment experiment = experimentRepo.findById(experimentId);
				
				experiment.addUsageEvent(new SessionEvent(participant, SessionEvent.Event.LOGIN));
				experimentRepo.save(experiment);
			}
		}
	}
}










