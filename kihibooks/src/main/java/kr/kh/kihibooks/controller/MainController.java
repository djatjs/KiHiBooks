package kr.kh.kihibooks.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MainController {
    
    @GetMapping("/")
	public String main() {
		return "home";
	}
    @GetMapping("/signup")
	public String signup() {
		return "user/signup";
	}
    @GetMapping("/login")
	public String login() {
		return "user/login";
	}

}
