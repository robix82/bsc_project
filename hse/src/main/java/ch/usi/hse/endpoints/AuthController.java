package ch.usi.hse.endpoints;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
@CrossOrigin
public class AuthController {

	@Value("${baseUrl}")
	private String baseUrl;

	@RequestMapping("/login")
	public ModelAndView login() {
		
		ModelAndView mav = new ModelAndView();
		mav.setViewName("login");
		mav.addObject("baseUrl", baseUrl);
		
		return mav;
	}
}
