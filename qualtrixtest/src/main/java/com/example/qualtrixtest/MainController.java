package com.example.qualtrixtest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class MainController {

	@GetMapping("/")
	public ModelAndView getHome(@RequestParam(required=false) String uid) {
		
		ModelAndView mav = new ModelAndView();
		mav.addObject("uid", uid);
		
		mav.setViewName("home");
		
		return mav;
	}
}
