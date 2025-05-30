package kr.kh.kihibooks.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MainController {
    
  @GetMapping("/")
	public String main(Model model) {
		List<String> banners = List.of(
			"/banners/banner1.png",
			"/banners/banner2.png",
			"/banners/banner3.png"
		);
		model.addAttribute("banners", banners);
		System.out.println("배너 리스트 전달됨");
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
