package kr.kh.kihibooks.controller;

import java.security.Principal;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import kr.kh.kihibooks.service.ApiService;
import kr.kh.kihibooks.service.UserService;
import kr.kh.kihibooks.model.vo.EmailVO;
import kr.kh.kihibooks.model.vo.UserVO;

@Controller
public class UserController {

	@Autowired
	UserService userService;

    @Autowired
    ApiService apiService;

    @Value("${kakao.client.id}")
    private String kakaoClientId;

    @Value("${kakao.redirect.uri}")
    private String kakaoRedirectUri;

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

    //회원가입 선택 창
    @GetMapping("/signup")
	public String signup(Model model) {
		model.addAttribute("kakaoClientId", kakaoClientId);
        model.addAttribute("kakaoRedirectUri", kakaoRedirectUri);
		return "user/signup";
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
    public String signupEmailPost(UserVO user) {
        if(userService.signup(user)){
            return "redirect:/";
        }
        return "redirect:/signup/email";
    }

    @GetMapping("/findPw")
    public String getMethodName() {
        return "user/findPw";
    }
    



    @GetMapping("/signup/kakao") // 실제 Redirect URI 경로로 수정
    public String kakaoLogin(@RequestParam String code) {
        System.out.println("인가 코드: " + code);

        // 1. 인가 코드를 사용하여 액세스 토큰을 요청
        String accessToken = apiService.getKakaoAccessToken(code); 

        // 2. 액세스 토큰을 사용하여 사용자 정보를 요청
        Map<String, Object> userInfo = apiService.getKakaoUserInfo(accessToken); 
        System.out.println("사용자 정보:" + userInfo);
        // 3. 받은 사용자 정보(이메일, 닉네임 등)를 기반으로 회원가입 또는 로그인 처리
        apiService.processKakaoUser(userInfo); 

        // 처리가 완료되면 적절한 페이지로 리다이렉트
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