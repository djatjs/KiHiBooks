package kr.kh.kihibooks.controller;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import kr.kh.kihibooks.service.UserService;
import kr.kh.kihibooks.model.vo.EmailVO;

@Controller
public class UserController {

	@Autowired
	UserService userService;

	@GetMapping("/account/mykihi")
	public String mypage() {
		return "user/mypage";
	}

	@GetMapping("/account/modify")
	public String edit() {
		return "user/edit";
	}

    @GetMapping("/user/editForm")
	public String editForm() {
		return "user/editForm";
	}

    @GetMapping("/signup/email")
    public String signupEmail() {
        return "user/signup_email";
    }

    @ResponseBody
    @PostMapping("/email/sendVerificationCode")
    public boolean sendEmail(@RequestBody String email) {
        EmailVO newEmail = new EmailVO();
        newEmail.setEv_email(email.trim());
        boolean evRes = userService.sendEmail(newEmail);
        System.out.println(evRes);
        if(!evRes) return false;
        return evRes;
    }

    @ResponseBody
    @PostMapping("/check/email")
    public boolean checkEmail(@RequestBody String email) {
        if(email == null){
            return false;
        }
        return userService.checkEmail(email);
    }

    @ResponseBody
    @PostMapping("/check/nickName")
    public boolean checkNickName(@RequestBody String nickName) {
        if(nickName == null){
            return false;
        }
        return userService.checkNickName(nickName);
    }

    @ResponseBody
    @PostMapping("/email/verifyCode")
    public boolean verifyCode(@RequestParam String userCode, @RequestParam String email) {
        if(userCode == null || email == null){
            return false;
        }
        return userService.checkCode(email, userCode);
    }

    @PostMapping("/signup/email")
    public String signupEmailPost() {
        
        return "redirect:/";
    }
    
    @GetMapping("/order/checkout/point")
    public String point(){
        return "user/point";
    }

    @GetMapping("/order/history_point")
    public String historyPoint(){
        return "user/historyPoint";
    }

    @GetMapping("/order/history")
    public String history(){
        return "/user/history";
    }
}