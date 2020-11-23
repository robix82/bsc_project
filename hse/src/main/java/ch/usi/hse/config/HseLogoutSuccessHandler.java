package ch.usi.hse.config;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.stereotype.Component;

import ch.usi.hse.db.entities.Experiment;
import ch.usi.hse.db.entities.Participant;
import ch.usi.hse.db.entities.SessionEvent;
import ch.usi.hse.db.repositories.ExperimentRepository;
import ch.usi.hse.db.repositories.ParticipantRepository;

@Component("HseLogoutSuccessHandler")
public class HseLogoutSuccessHandler implements LogoutSuccessHandler {

	@Autowired
	private ParticipantRepository participantRepo;
	
	@Autowired
	private ExperimentRepository experimentRepo;
	
	@Override
	public void onLogoutSuccess(HttpServletRequest request, 
								HttpServletResponse response, 
								Authentication authentication)
	
		throws IOException, ServletException {

		String uName = authentication.getName();		
		System.out.println("LOGOUT: " + uName);
		
		HttpSession session = request.getSession();
		
		if  (session != null) {
			session.removeAttribute("user");
		}
		
		if (participantRepo.existsByUserName(uName)) {
			
			Participant participant = participantRepo.findByUserName(uName);
			int experimentId = participant.getExperimentId();
			
			if (experimentRepo.existsById(experimentId)) {
				
				Experiment experiment = experimentRepo.findById(experimentId);
				experiment.addUsageEvent(new SessionEvent(participant, SessionEvent.Event.LOGOUT));
			}
		}
		
		response.setStatus(HttpServletResponse.SC_OK);
		response.sendRedirect("/login");
	}
}










