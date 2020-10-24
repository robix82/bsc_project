package ch.usi.hse.endpoints;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

import ch.usi.hse.services.UserService;

/**
 * Controller class for the admin UI page
 * 
 * @author robert.jans@usi.ch
 *
 */
@Controller
public class AdminController {

	@Autowired
	private UserService userService;
	
	/**
	 * Serves the main admin UI page
	 * 
	 * @return ModelAndView
	 */
	@GetMapping("/admin")
	public ModelAndView getAdminUi() {
		
		
		ModelAndView mav = new ModelAndView();
		mav.setViewName("admin");
		
		mav.addObject("administrators", userService.allAdministrators());
		mav.addObject("experimenters", userService.allExperimenters());
		mav.addObject("participants", userService.allParticipants());
		
		return mav;
	}
}





