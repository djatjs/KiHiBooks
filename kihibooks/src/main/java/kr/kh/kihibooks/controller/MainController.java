package kr.kh.kihibooks.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MainController {

	@Value("${kakao.client.id}")
	private String kakaoClientId;

	@Value("${kakao.redirect.uri}")
	private String kakaoRedirectUri;

	@GetMapping("/")
	public String main(Model model) {
		List<String> banners = List.of(
				"/banners/banner1.png",
				"/banners/banner2.png",
				"/banners/banner3.png");
		model.addAttribute("banners", banners);
		return "home";
	}

	@GetMapping("/login")
	public String login(Model model) {
		model.addAttribute("kakaoClientId", kakaoClientId);
		model.addAttribute("kakaoRedirectUri", kakaoRedirectUri);
		return "user/login";
	}

}
