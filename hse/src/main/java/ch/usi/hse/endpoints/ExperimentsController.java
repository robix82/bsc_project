package ch.usi.hse.endpoints;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

/**
 * Controller class for the experiments UI page
 * 
 * @author robert.jans@usi.ch
 *
 */
@Controller
public class ExperimentsController {

	/**
	 * Serves the main experiments UI
	 * 
	 * @return ModelAndView
	 */
	@GetMapping("/experiments")
	public ModelAndView getExperimentsUi() {
		
		ModelAndView mav = new ModelAndView();
		mav.setViewName("experiments");
		
		return mav;
	}
}
