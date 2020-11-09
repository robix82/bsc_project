package ch.usi.hse.endpoints;




import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import ch.usi.hse.exceptions.NoSuchExperimentException;
import ch.usi.hse.services.ExperimentService;
import ch.usi.hse.services.UserService;

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

	@Autowired
	private ExperimentService experimentService;
	
	@Autowired
	private UserService userService;
	
	/**
	 * Serves the main experiments UI
	 * 
	 * @return ModelAndView
	 */
	@GetMapping("/ui")
	public ModelAndView getExperimentsUi() {
		 
		ModelAndView mav = new ModelAndView();
		mav.setViewName("experiments");
		
		// TODO: maybe select only experiments of currently logged in experimenter
		mav.addObject("experiments", experimentService.allExperiments());
		mav.addObject("experimenters", userService.allExperimenters());
		
		return mav;
	}
	
	/**
	 * Serves the Setup page for the Experiment with the given Id
	 * 
	 * @param expId
	 * @return
	 * @throws NoSuchExperimentException 
	 */
	@GetMapping("/setup/ui")
	public ModelAndView getExperimentsSetupUi(@RequestParam(required=true) int expId) 
			throws NoSuchExperimentException {
		
		ModelAndView mav = new ModelAndView();
		mav.setViewName("exp_setup");
		mav.addObject("experiment", experimentService.findExperiment(expId));
		
		return mav;
	}
	
	/**
	 * Serves the experiment execution page for the Experiment with the given id
	 * 
	 * @param expId
	 * @return
	 * @throws NoSuchExperimentException 
	 */
	@GetMapping("/run/ui")
	public ModelAndView getExperimentsRunUi(@RequestParam(required=true) int expId) 
			throws NoSuchExperimentException {
		
		ModelAndView mav = new ModelAndView();
		mav.setViewName("exp_run");
		mav.addObject("experiment", experimentService.findExperiment(expId));
		
		return mav;
	}
	
	/**
	 * Serves the experiment evaluation page for the Experiment with the given id
	 * 
	 * @param expId
	 * @return
	 * @throws NoSuchExperimentException 
	 */
	@GetMapping("/eval/ui")
	public ModelAndView getExperimentsEvalUi(@RequestParam(required=true) int expId) 
			throws NoSuchExperimentException {
		
		ModelAndView mav = new ModelAndView();
		mav.setViewName("exp_eval");
		mav.addObject("experiment", experimentService.findExperiment(expId));
		
		return mav;
	}
}






