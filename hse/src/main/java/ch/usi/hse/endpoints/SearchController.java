package ch.usi.hse.endpoints;


import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.highlight.InvalidTokenOffsetsException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.servlet.ModelAndView;

import ch.usi.hse.db.entities.HseUser;
import ch.usi.hse.db.entities.Participant;
import ch.usi.hse.exceptions.FileReadException;
import ch.usi.hse.exceptions.NoSuchExperimentException;
import ch.usi.hse.exceptions.NoSuchFileException;
import ch.usi.hse.exceptions.NoSuchUserException;
import ch.usi.hse.retrieval.SearchResult;
import ch.usi.hse.retrieval.SearchResultList;
import ch.usi.hse.services.SearchService;
import ch.usi.hse.services.UserService;

/**
 * Controller class for the search UI page
 * 
 * @author robert.jans@usi.ch
 * 
 */
@Controller
@CrossOrigin
public class SearchController {

	@Autowired
	private SearchService searchService;
	
	@Value("${baseUrl}")
	private String baseUrl;
	
	@Autowired
	private UserService userService;
	
	/**
	 * Serves the main search UI page
	 * 
	 * @return ModelAndView
	 */
	@GetMapping("/")
	public ModelAndView getSearchUi() {
    		
		ModelAndView mav = new ModelAndView();
		mav.addObject("baseUrl", baseUrl);
		mav.setViewName("search");
		
		return mav;
	}
	
	@GetMapping("/participantLogout")
	public ModelAndView getParticipantLogout() {
		
		ModelAndView mav = new ModelAndView();
		mav.addObject("baseUrl", baseUrl);
		mav.setViewName("participantLogout");
		mav.addObject("baseUrl", baseUrl);
		
		return mav;
	}
	
	@PostMapping("/")
	public ModelAndView postQuery(@AuthenticationPrincipal User user, String queryString) 
			throws NoSuchUserException, 
				   ParseException, 
				   FileReadException, 
				   InvalidTokenOffsetsException, 
				   NoSuchExperimentException, 
				   NoSuchFileException {
		
		
		
		ModelAndView mav = new ModelAndView();
		mav.addObject("baseUrl", baseUrl);
		mav.setViewName("search");
		
		if (! queryString.isBlank()) {
			
			HseUser hseUser = userService.findUser(user.getUsername());
			SearchResultList srl = searchService.search(queryString, hseUser);
			mav.addObject("searchResultList", srl);
		}
				
		return mav;
	}
	
	@PostMapping("/browse")
	public ResponseEntity<String> postBrowseEvent(@AuthenticationPrincipal User user,
												  @RequestBody SearchResult clickedResult) 
			throws NoSuchExperimentException, NoSuchUserException {
		
		
		HseUser hseUser = userService.findUser(user.getUsername());
		
		if (hseUser instanceof Participant) {
			
			searchService.addDocClickEvent(clickedResult, (Participant) hseUser);
		}
			
		return new ResponseEntity<>("ok", HttpStatus.OK);
	}
}









