package ch.usi.hse.endpoints;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import ch.usi.hse.exceptions.NoSuchTestGroupException;
import ch.usi.hse.exceptions.NoSuchUserException;
import ch.usi.hse.exceptions.UserExistsException;
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
	
	@Value("${baseUrl}")
	private String baseUrl;

	@GetMapping("/")
	public ModelAndView getSearchUiFromSurvey(@RequestParam(name="xid") int experimentId,
											  @RequestParam(name="gid") int groupId,
											  @RequestParam(name="uid") int userId,
											  @RequestParam(name="qurl") String surveyUrl) {
		
		ModelAndView mav = new ModelAndView();
		mav.setViewName("search");
		mav.addObject("baseUrl", baseUrl);
		mav.addObject("experimentId", experimentId);
		mav.addObject("groupId", groupId);
		mav.addObject("userId", userId);
		mav.addObject("surveyUrl", surveyUrl);
		
		return mav;
	}
	
	@PostMapping("/addUser")
	public ResponseEntity<Integer> addUser(@RequestParam String name, 
										   @RequestParam int groupId, 
										   @RequestParam String surveyUrl)
	
			throws NoSuchUserException, 
				   UserExistsException, 
				   NoSuchTestGroupException {
		
		int userId = userService.addSurveyParticipant(name, groupId, surveyUrl);
		
		return new ResponseEntity<>(userId, HttpStatus.OK);
	}
}






