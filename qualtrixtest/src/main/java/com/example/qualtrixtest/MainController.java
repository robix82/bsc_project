package com.example.qualtrixtest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class MainController {

	@GetMapping("/")
	public ModelAndView getHome() {
		
		ModelAndView mav = new ModelAndView();
		
		mav.setViewName("home");
		
		return mav;
	}
	
	@GetMapping("/wg")
	public ModelAndView getHomeWg(@RequestParam(name="g", required=true) String group) {
		
		ModelAndView mav = new ModelAndView();
		
		mav.setViewName("home");
		mav.addObject("group", group);
		
		return mav;
	}
}
