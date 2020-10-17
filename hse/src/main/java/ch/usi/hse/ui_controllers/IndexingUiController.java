package ch.usi.hse.ui_controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

/**
 * Controller class for the indexing UI page
 * 
 * @author robert.jans@usi.ch
 *
 */
@Controller
public class IndexingUiController {

	/**
	 * Serves the main indexing UI page
	 * 
	 * @return ModelAndView
	 */
	@GetMapping("/indexing")
	public ModelAndView getIndexingUi() {
		
		ModelAndView mav = new ModelAndView();
		mav.setViewName("indexing");
		
		return mav;
	}
}
