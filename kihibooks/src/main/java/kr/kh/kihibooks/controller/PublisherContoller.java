package kr.kh.kihibooks.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PublisherContoller {
    @GetMapping("/publisher/dashboard")
    public String publisherDashboard() {
        return "/publisher/publisherDashboard";
    }

    @GetMapping("publisher/editors")
    public String editors() {
        return "publisher/manageEditors";
    }
    
    
}
