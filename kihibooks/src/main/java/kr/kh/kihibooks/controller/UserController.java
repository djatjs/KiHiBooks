package kr.kh.kihibooks.controller;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import kr.kh.kihibooks.service.UserService;

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

	@PostMapping("/checkPw")
	@ResponseBody
	public boolean checkPw(@RequestParam String pw, Principal principal) {
		return userService.checkPw(principal.getName(), pw);
	}

  @GetMapping("/user/editForm")
	public String editForm() {
		return "user/editForm";
	}

}
