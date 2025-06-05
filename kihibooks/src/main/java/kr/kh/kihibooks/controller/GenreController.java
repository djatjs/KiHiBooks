package kr.kh.kihibooks.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class GenreController {

    @GetMapping("/genre/rofan")
    public String ropanPage(Model model) {
        model.addAttribute("genre", "로판");
        return "genre/rofan";
    }

    @GetMapping("/genre/fantasy")
    public String fantasyPage(Model model) {
        model.addAttribute("genre", "판타지");
        return "genre/fantasy";
    }

    @GetMapping("/genre/martial")
    public String martialPage(Model model) {
        model.addAttribute("genre", "무협");
        return "genre/martial";
    }
    @GetMapping("/test")
    public String testPage(Model model) {
        return "genre/genre-template"; // 또는 "genre/rofan"
    }
}
