package usi.ch.hse.ui_controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;

import usi.ch.hse.dto.SearchResultList;
import static usi.ch.hse.dummie_data.SearchData.*;

/**
 * Controller class for the search UI page
 * 
 * @author robert.jans@usi.ch
 * 
 */
@Controller
public class SearchUiController {

	/**
	 * Serves the main search UI page
	 * 
	 * @return ModelAndView
	 */
	@GetMapping("/")
	public ModelAndView getSearchUi() {
    		
		ModelAndView mav = new ModelAndView();
		mav.setViewName("search");
		
		return mav;
	}
	
	@PostMapping("/")
	public ModelAndView postQuery(String queryString) {
		
		SearchResultList srl = dummieSearchResultList(10);
		
		ModelAndView mav = new ModelAndView();
		mav.setViewName("search");
		mav.addObject("searchResults", srl);
		
		return mav;
	}
}









