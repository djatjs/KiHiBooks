package kr.kh.kihibooks.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import kr.kh.kihibooks.model.vo.UserVO;
import kr.kh.kihibooks.service.UserService;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Controller
@RequestMapping("/admin")
public class AdminController {
    
    @Autowired
	UserService userService;

    @GetMapping("/dashboard")
    public String dashboard() {
        return "admin/dashboard";
    }
    @GetMapping("/publishers")
    public String publishers() {
        // 출판사 목록을 가져오는 로직 추가 필요
        // 예: List<Publisher> publishers = publisherService.getAllPublishers();
        // model.addAttribute("publishers", publishers);
        return "admin/publishers";
    }

    @GetMapping("/addPublisher")
    public String addPublisher() {
        return "admin/addPublisher";
    }
    @PostMapping("addPublisher")
    public String addPublisherPost(UserVO user, String pu_name) {
        System.out.println(user);
        System.out.println(pu_name);
        //user user테이블에 넣기.
        userService.signup(user);
        return "redirect:/admin/publishers";
    }
    
    
}
