package ch.usi.hse.endpoints;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import ch.usi.hse.db.entities.Administrator;
import ch.usi.hse.db.entities.Experimenter;
import ch.usi.hse.db.entities.Participant;
import ch.usi.hse.exceptions.UserExistsException;
import ch.usi.hse.services.UserService;

/**
 * Controller class for the admin UI page
 * 
 * @author robert.jans@usi.ch
 *
 */
@Controller
@RequestMapping("/admin")
public class AdminController {

	@Autowired
	private UserService userService;
	
	/**
	 * Serves the main admin UI page
	 * 
	 * @return ModelAndView
	 */
	@GetMapping("/ui")
	public ModelAndView getAdminUi() {
		
		
		ModelAndView mav = new ModelAndView();
		mav.setViewName("admin");
		 
		mav.addObject("administrators", userService.allAdministrators());
		mav.addObject("experimenters", userService.allExperimenters());
		mav.addObject("participants", userService.allParticipants());
		
		return mav;
	}
	
	// REST endpoints
	
	/**
	 * create a new Administrator
	 * 
	 * @param administrator
	 * @return Http response containing the saved Administrator as json
	 * @throws UserExistsException 
	 */
	@PostMapping("/administrators")
	public ResponseEntity<Administrator> postAdministrator(@RequestBody Administrator administrator) 
			throws UserExistsException {
		
		Administrator saved = userService.addAdministrator(administrator);
		
		return new ResponseEntity<>(saved, HttpStatus.CREATED);
	}
	
	/**
	 * create a new Experimenter
	 * 
	 * @param experimenter
	 * @return Http response containing the saved Experimenter as json
	 * @throws UserExistsException 
	 */
	@PostMapping("/experimenters")
	public ResponseEntity<Experimenter> postExperimenter(@RequestBody Experimenter experimenter) 
			throws UserExistsException {
		
		Experimenter saved = userService.addExperimenter(experimenter);
		
		return new ResponseEntity<>(saved, HttpStatus.CREATED);
	}
	
	/**
	 * create a new Participant
	 * 
	 * @param participant
	 * @return Http response containing the saved Participant as json
	 * @throws UserExistsException 
	 */
	@PostMapping("/participants")
	public ResponseEntity<Participant> postParticipant(@RequestBody Participant participant) 
			throws UserExistsException {
		
		Participant saved = userService.addParticipant(participant);
		
		return new ResponseEntity<>(saved, HttpStatus.CREATED);
	}
}





