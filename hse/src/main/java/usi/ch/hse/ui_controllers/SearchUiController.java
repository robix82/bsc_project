package usi.ch.hse.ui_controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class SearchUiController {

	@GetMapping("/")
	public ModelAndView getSearchUi() {
		
		ModelAndView mav = new ModelAndView();
		mav.setViewName("search");
		
		return mav;
	}
}
