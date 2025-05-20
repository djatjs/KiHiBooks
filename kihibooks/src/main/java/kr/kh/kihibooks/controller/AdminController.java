package kr.kh.kihibooks.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import kr.kh.kihibooks.model.vo.PublisherIdVO;
import kr.kh.kihibooks.model.vo.PublisherVO;
import kr.kh.kihibooks.model.vo.UserVO;
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

    @GetMapping("/dashboard")
    public String dashboard() {
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
        System.out.println(user);
        System.out.println(pu_name);

        // 1. 출판사 등록
        if(!publisherService.signup(pu_name)){
            System.out.println("출판사 등록 실패");
            return "redirect:/admin/addPublisher";
        }
        // 2. 출판사 코드 가져오기 (방금 등록한 출판사)
        PublisherVO publisher = publisherService.getPublisherByName(pu_name);
        if (publisher == null) {
            System.out.println("출판사 코드 조회 실패");
            return "redirect:/admin/addPublisher";
        }

        // 3. 유저 등록
        if(!userService.signup(user)){
            System.out.println("회원가입 실패");
            return "redirect:/admin/addPublisher";
        }
        // 4. 등록한 유저의 UR_NUM 가져오기
        UserVO registeredUser = userService.selectUser(user.getUr_email());
        if (registeredUser == null) {
            System.out.println("유저 조회 실패");
            return "redirect:/admin/addPublisher";
        }

        // 5. PUBLISHER_ID 테이블에 등록 (super 권한)
        PublisherIdVO publisherId = new PublisherIdVO();
        publisherId.setPi_ur_num(registeredUser.getUr_num());
        publisherId.setPi_pu_code(publisher.getPu_code());
        publisherId.setPi_authority("super");

        if (!publisherService.insertPublisherId(publisherId)) {
            System.out.println("출판사 ID 등록 실패");
            return "redirect:/admin/addPublisher";
        }

        return "redirect:/admin/publishers";
    }
    
    
}
