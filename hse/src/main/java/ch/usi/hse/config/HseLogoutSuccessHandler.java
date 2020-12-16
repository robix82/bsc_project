package ch.usi.hse.config;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.simp.SimpMessagingTemplate;
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

	@Value("${baseUrl}")
	private String baseUrl;
	
	@Autowired
	private ParticipantRepository participantRepo;
	
	@Autowired
	private ExperimentRepository experimentRepo;
	
	@Autowired
	private SimpMessagingTemplate simpMessagingTemplate;
	
	@Override
	public void onLogoutSuccess(HttpServletRequest request, 
								HttpServletResponse response, 
								Authentication authentication)
	
		throws IOException, ServletException {

		String uName = authentication.getName();		
		String redirect = baseUrl + "login";
		
		HttpSession session = request.getSession();
		
		if  (session != null) {
			session.removeAttribute("user");
		}
		
		response.setStatus(HttpServletResponse.SC_OK);
		
		if (participantRepo.existsByUserName(uName)) {
			
			Participant participant = participantRepo.findByUserName(uName);
			participant.setOnline(false);
			participantRepo.save(participant);
			int experimentId = participant.getExperimentId();
			
			if (experimentRepo.existsById(experimentId)) {
				
				Experiment experiment = experimentRepo.findById(experimentId);
				experiment.addUsageEvent(new SessionEvent(participant, SessionEvent.Event.LOGOUT));
				experimentRepo.save(experiment);
				simpMessagingTemplate.convertAndSend("/userActions", experiment);
			}
			
			String surveyUrl = participant.getSurveyUrl();
			
			if (surveyUrl != null) {
				
				redirect = surveyUrl;
			}
			else {
				
				redirect = baseUrl + "participantLogout";
			}
			 
		}

		response.sendRedirect(redirect);
	}
}










