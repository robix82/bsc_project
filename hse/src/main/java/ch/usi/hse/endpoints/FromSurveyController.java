package ch.usi.hse.endpoints;


import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.fasterxml.jackson.annotation.JsonProperty;

import ch.usi.hse.db.entities.Experiment;
import ch.usi.hse.db.entities.Participant;
import ch.usi.hse.exceptions.NoSuchExperimentException;
import ch.usi.hse.exceptions.NoSuchTestGroupException;
import ch.usi.hse.exceptions.NoSuchUserException;
import ch.usi.hse.exceptions.UserExistsException;
import ch.usi.hse.services.ExperimentService;
import ch.usi.hse.services.UserService;

/**
 * Controller class for managing interactions from Qualtrics survey
 * 
 * @author robert.jans@usi.ch
 * 
 */
@Controller
@CrossOrigin
@RequestMapping("/from_survey")
public class FromSurveyController {
	
	@Autowired
	private UserService userService;
	
	@Autowired private ExperimentService experimentService;
	
	@Autowired
	private SimpMessagingTemplate simpMessagingTemplate;
	
	
	@Value("${baseUrl}")
	private String baseUrl;

	@GetMapping("/")
	public ModelAndView getSearchUiFromSurvey(HttpServletRequest request, 
											  @RequestParam(name="uid") int userId,
											  @RequestParam(name="svUrl") String surveyUrl) 
	
			throws NoSuchUserException, 
				   NoSuchExperimentException, 
				   UserExistsException {
		
		Participant user = userService.findParticipant(userId);
		int experimentId = user.getExperimentId();
		Experiment experiment = experimentService.findExperiment(experimentId);
		
		if (! experiment.getStatus().equals(Experiment.Status.RUNNING)) {
			
			return notRunning(experiment, user.getSurveyUrl());
		}
		
		try {
	        
			request.login(user.getUserName(), UserService.surveyUserPassword);
			
			user.setOnline(true);
			user.setSurveyUrl(surveyUrl);
			userService.updateParticipant(user);
			Experiment updatedExperiment = experimentService.findExperiment(experimentId);
			simpMessagingTemplate.convertAndSend("/userActions", updatedExperiment);
	    } 
		catch (ServletException e) {
			
	        System.out.println("Error while login");
	    }
		
		ModelAndView mav = new ModelAndView();
		mav.setViewName("search");
		mav.addObject("baseUrl", baseUrl);
		mav.addObject("timeout", experiment.getTimeout());

		return mav;
	}
	
	@PostMapping("/addUser")
	public ResponseEntity<UserIdInfo> addUser(@RequestParam int groupId)
	
			throws NoSuchUserException, 
				   UserExistsException, 
				   NoSuchTestGroupException, 
				   NoSuchExperimentException {
		
		Participant user = userService.addSurveyParticipant(groupId);

		simpMessagingTemplate.convertAndSend("/userAdded", "new survey user");
		
		return new ResponseEntity<>(new UserIdInfo(user.getId()), HttpStatus.OK);
	}
	
	@GetMapping("/is_running")
	public ResponseEntity<ExperimentInfo> checkIsRunning(@RequestParam int expId) 
			throws NoSuchExperimentException {
		
		Experiment experiment = experimentService.findExperiment(expId);
		boolean isRunning = experiment.getStatus().equals(Experiment.Status.RUNNING);
		ExperimentInfo info = new ExperimentInfo(expId, isRunning);
		
		return new ResponseEntity<>(info, HttpStatus.OK);
	}
	
	@GetMapping("/not_running")
	private ModelAndView notRunning(Experiment experiment, String surveyUrl) {
		
		ModelAndView mav = new ModelAndView();
		mav.setViewName("not_running");
		mav.addObject("experiment", experiment);
		mav.addObject("surveyUrl", surveyUrl);
		
		return mav;
	}
	
	private class UserIdInfo {
		
		private int id;
		
		public UserIdInfo(@JsonProperty("id") int id) {
			
			this.id = id;
		}
		
		@SuppressWarnings("unused")
		public int getId() {
			return id;
		}
	}
	
	private class ExperimentInfo {
		
		private int id;
		private boolean isRunning;
		
		public ExperimentInfo(@JsonProperty("id") int id,
							  @JsonProperty("isRunning") boolean isRunning) {
			
			this.id = id;
			this.isRunning = isRunning;
		}
		
		@SuppressWarnings("unused")
		public int getId() {
			return id;
		}
		
		@SuppressWarnings("unused")
		public boolean getIsRunning() {
			return isRunning;
		}
	}
}






