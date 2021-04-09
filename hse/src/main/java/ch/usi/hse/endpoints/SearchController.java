package ch.usi.hse.endpoints;


import java.io.IOException;

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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import ch.usi.hse.db.entities.HseUser;
import ch.usi.hse.db.entities.Participant;
import ch.usi.hse.exceptions.FileReadException;
import ch.usi.hse.exceptions.NoSuchExperimentException;
import ch.usi.hse.exceptions.NoSuchFileException;
import ch.usi.hse.exceptions.NoSuchTestGroupException;
import ch.usi.hse.exceptions.NoSuchUserException;
import ch.usi.hse.retrieval.SearchResult;
import ch.usi.hse.retrieval.SearchResultList;
import ch.usi.hse.services.ExperimentService;
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
	
	@Autowired
	private ExperimentService experimentService;

	/**
	 * Serves the main search UI page
	 * 
	 * @return ModelAndView
	 * @throws NoSuchTestGroupException 
	 */
	@GetMapping("/")
	public ModelAndView getSearchUi(@AuthenticationPrincipal User user, String queryString) 
			throws NoSuchUserException, 
				   ParseException, 
				   FileReadException, 
				   InvalidTokenOffsetsException, 
				   NoSuchExperimentException, 
				   NoSuchFileException, 
				   IOException, NoSuchTestGroupException {
		
		HseUser u = userService.findUser(user.getUsername());
		
		if (! (u instanceof Participant)) {
			
			ModelAndView mav = new ModelAndView();
			mav.addObject("baseUrl", baseUrl);
			mav.addObject("experiments", experimentService.allExperiments());
			mav.addObject("experimenters", userService.allExperimenters());
			mav.setViewName("experiments");
			
			return mav;
		}
		
		Participant participant = userService.findParticipant(user.getUsername());
		
		String query = null;
		SearchResultList srl = null;
		
		if (queryString != null && ! queryString.isBlank()) {
			
			query = queryString.trim().toLowerCase();
		}
		
		if (query == null) {
			
			String lastQuery = participant.getLastQuery();
			
			if (lastQuery != null) {
				
				srl = searchService.handleRepeatedQuery(lastQuery, participant);
			}
		}
		else if (participant.getQueryCount() == 0) {
			
			srl = searchService.handleFirstQuery(query, participant);
		}
		else if (query.equals(participant.getLastQuery())) {
			
			srl = searchService.handleRepeatedQuery(query, participant);
		}
		else {
			
			srl = searchService.handleNewQuery(query, participant);
		}

		
		ModelAndView mav = new ModelAndView();
		mav.addObject("baseUrl", baseUrl);
		mav.setViewName("search");
		mav.addObject("searchResultList", srl);
				
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

	@GetMapping("/doc")
	public ModelAndView getResultDoc(@RequestParam String url,
									 @RequestParam String fileType) {
		
		ModelAndView mav = new ModelAndView();
		mav.setViewName("document");
		mav.addObject("baseUrl", baseUrl);
		mav.addObject("docUrl", url);
		mav.addObject("fileType", fileType);
		
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









