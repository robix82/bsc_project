package com.example.qualtrixtest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class MainController {

	@GetMapping("/")
	public ModelAndView getHome(@RequestParam(name="uid") String uid, 
								@RequestParam(name="gid") String group, 
								@RequestParam(name="qUrl") String surveyUrl) {
		
		ModelAndView mav = new ModelAndView();
		
		mav.setViewName("home");
		mav.addObject("uid", uid);
		mav.addObject("group", group);
		mav.addObject("surveyUrl", surveyUrl);
		
		return mav;
	}
}
