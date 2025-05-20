package kr.kh.kihibooks.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;


@Controller
public class Publisher {
    @GetMapping("/publisher/dashboard")
    public String publisherDashboard() {
        return "/publisher/dashboard";
    }
    
}
