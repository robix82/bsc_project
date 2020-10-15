package usi.ch.hse.ui_controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

/**
 * Controller class for the indexing UI page
 * 
 * @author Robert Jans
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
