package ch.usi.hse.endpoints;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import ch.usi.hse.db.entities.Administrator;
import ch.usi.hse.db.entities.Experimenter;
import ch.usi.hse.db.entities.Participant;
import ch.usi.hse.exceptions.NoSuchUserException;
import ch.usi.hse.exceptions.UserExistsException;
import ch.usi.hse.services.UserService;

/**
 * Controller class for the admin UI page
 * 
 * @author robert.jans@usi.ch
 *
 */
@Controller
@CrossOrigin
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
	
	// REST ENDPOINTS FOR USER MANAGEMENT
	
	// create new users
	
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
	
	// update existing users
	
	/**
	 * update an existing Administrator
	 * 
	 * @param administrator
	 * @return Http response containing the updated Administrator as json
	 * @throws NoSuchUserException
	 * @throws UserExistsException
	 */
	@PutMapping("/administrators")
	public ResponseEntity<Administrator> updateAdministrator(@RequestBody Administrator administrator)
		throws NoSuchUserException, UserExistsException {
		
		Administrator updated = userService.updateAdministrator(administrator);
		
		return new ResponseEntity<>(updated, HttpStatus.OK);
	}
	
	/**
	 * update an existing Experimenter
	 * 
	 * @param experimenter
	 * @return Http response containing the updated Experimenter as json
	 * @throws NoSuchUserException
	 * @throws UserExistsException
	 */
	@PutMapping("/experimenters")
	public ResponseEntity<Experimenter> updateExperimenter(@RequestBody Experimenter experimenter)
		throws NoSuchUserException, UserExistsException {
		
		Experimenter updated = userService.updateExperimenter(experimenter);
		
		return new ResponseEntity<>(updated, HttpStatus.OK);
	}
	
	/**
	 * update an existing Participant
	 * 
	 * @param participant
	 * @return Http response containing the updated Participant as json
	 * @throws NoSuchUserException
	 * @throws UserExistsException
	 */
	@PutMapping("/participants")
	public ResponseEntity<Participant> updateParticipant(@RequestBody Participant participant)
		throws NoSuchUserException, UserExistsException {
		
		Participant updated = userService.updateParticipant(participant);
		
		return new ResponseEntity<>(updated, HttpStatus.OK);
	}
	
	// delete single users
	
	/**
	 * removes the given Administrator from the database
	 * 
	 * @param administrator
	 * @return administrator
	 * @throws NoSuchUserException
	 */
	@DeleteMapping("/administrators")
	public ResponseEntity<Administrator> deleteAdministrator(@RequestBody Administrator administrator)
		throws NoSuchUserException {
		
		userService.removeUser(administrator.getId());
		
		return new ResponseEntity<>(administrator, HttpStatus.OK);
	}
	
	/**
	 * removes the given Experimenter from the database
	 * 
	 * @param experimenter
	 * @return experimenter
	 * @throws NoSuchUserException
	 */
	@DeleteMapping("/experimenters")
	public ResponseEntity<Experimenter> deleteExperimenter(@RequestBody Experimenter experimenter)
		throws NoSuchUserException {
		
		userService.removeUser(experimenter.getId());
		
		return new ResponseEntity<>(experimenter, HttpStatus.OK);
	}
	
	/**
	 * removes the given Participant from the database
	 * 
	 * @param participant
	 * @return participant
	 * @throws NoSuchUserException
	 */
	@DeleteMapping("/participants")
	public ResponseEntity<Participant> deleteParticipant(@RequestBody Participant participant)
		throws NoSuchUserException {
		
		userService.removeUser(participant.getId());
		
		return new ResponseEntity<>(participant, HttpStatus.OK);
	}
	
	// clear user repositories
	
	/**
	 * remove all Experimenters from database
	 * 
	 * @return Success message ("Experimenters Cleared")
	 */
	@DeleteMapping("/experimenters/all")
	public ResponseEntity<String> delteAllExperimenters() {
		
		userService.clearExperimenters();
		
		return new ResponseEntity<>("Experimenters Cleared", HttpStatus.OK);
	}
	
	/**
	 * remove all Participants from database
	 * 
	 * @return Success message ("Participants Cleared")
	 */
	@DeleteMapping("/participants/all")
	public ResponseEntity<String> delteAllParticipants() {
		
		userService.clearParticipants();
		
		return new ResponseEntity<>("Participants Cleared", HttpStatus.OK);
	}
}










 








