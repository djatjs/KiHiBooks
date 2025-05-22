package kr.kh.kihibooks.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import kr.kh.kihibooks.model.vo.UserVO;
import kr.kh.kihibooks.service.PublisherService;
import kr.kh.kihibooks.service.UserService;


@Controller
public class PublisherContoller {

    @Autowired
    UserService userService;

    @Autowired
    PublisherService publisherService;

    @GetMapping("/publisher/dashboard")
    public String publisherDashboard() {
        return "/publisher/publisherDashboard";
    }

    @GetMapping("/publisher/editors")
    public String editors() {
        return "publisher/manageEditors";
    }
    
    @ResponseBody
    @PostMapping("/publisher/searchUser")
    public UserVO searchUser(@RequestBody String searchInput) {
        // searchInput을 DB에서 검색해서 있으면 반환
        UserVO user = userService.getUserByNickName(searchInput);
        if (user == null) {
            return null;
        }
        return user;
    }

    @ResponseBody
    @PostMapping("/publisher/addEditor")
    public boolean addEditor(@RequestParam int userNum, String puCode) {
        try {
            return publisherService.addEditor(userNum, puCode);
        } catch (Exception e) {
            System.out.println("트랜잭션 실패: " + e.getMessage());
            return false;
        }
    }
    
    
}
