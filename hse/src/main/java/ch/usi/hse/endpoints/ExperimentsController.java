package ch.usi.hse.endpoints;



import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

/**
 * Controller class for the experiments UI page
 * 
 * @author robert.jans@usi.ch
 *
 */
@Controller
@CrossOrigin
@RequestMapping("/experiments")
public class ExperimentsController {

	/**
	 * Serves the main experiments UI
	 * 
	 * @return ModelAndView
	 */
	@GetMapping("/ui")
	public ModelAndView getExperimentsUi() {
		
		ModelAndView mav = new ModelAndView();
		mav.setViewName("experiments");
		
		return mav;
	}
	
	@GetMapping("/setup/ui")
	public ModelAndView getExperimentsSetupUi(@RequestParam(required=true) int expId) {
		
		ModelAndView mav = new ModelAndView();
		mav.setViewName("exp_setup");
		
		return mav;
	}
	
	@GetMapping("/run/ui")
	public ModelAndView getExperimentsRunUi(@RequestParam(required=true) int expId) {
		
		ModelAndView mav = new ModelAndView();
		mav.setViewName("exp_run");
		
		return mav;
	}
	
	@GetMapping("/eval/ui")
	public ModelAndView getExperimentsEvalUi(@RequestParam(required=true) int expId) {
		
		ModelAndView mav = new ModelAndView();
		mav.setViewName("exp_eval");
		
		return mav;
	}
}






