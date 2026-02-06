package kr.kh.kihibooks.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import kr.kh.kihibooks.model.vo.PublisherIdVO;
import kr.kh.kihibooks.model.vo.PublisherVO;
import kr.kh.kihibooks.model.vo.UserVO;
import kr.kh.kihibooks.service.AdminService;
import kr.kh.kihibooks.service.PublisherService;
import kr.kh.kihibooks.service.UserService;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequestMapping("/admin")
public class AdminController {
    
    @Autowired
	UserService userService;

    @Autowired
    PublisherService publisherService;

    @Autowired
    AdminService adminService;

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        List<PublisherVO> publishers = publisherService.getAllPublishers();
        int count = publishers.size();
        model.addAttribute("count", count);
        return "admin/dashboard";
    }
    @GetMapping("/publishers")
    public String publishers(Model model) {
        List<PublisherVO> publishers = publisherService.getAllPublishers();
        model.addAttribute("publishers", publishers);
        return "admin/publishers";
    }

    @GetMapping("/addPublisher")
    public String addPublisher() {
        return "admin/addPublisher";
    }
    
    @PostMapping("addPublisher")
    public String addPublisherPost(UserVO user, String pu_name) {

        try {
            // 단 하나의 통합 서비스 메서드만 호출합니다.
            if (!adminService.createPublisherAccount(user, pu_name)) {
                 // 내부적으로 false 반환 시 처리
                return "redirect:/admin/addPublisher?error=fail";
            }
        } catch (Exception e) {
            // 트랜잭션 실패로 인한 롤백 발생 시 처리
            return "redirect:/admin/addPublisher?error=exception";
        }

        return "redirect:/admin/publishers";
    }
    
    
}
