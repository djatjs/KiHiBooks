package kr.kh.kihibooks.controller;

import java.security.Principal;
import java.util.Map;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import kr.kh.kihibooks.service.ApiService;
import kr.kh.kihibooks.service.UserService;
import kr.kh.kihibooks.utils.CustomUser;
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
    public String mypage(Model model) {
        UserVO user = userService.getCurrentUser();
        model.addAttribute("user", user);
        return "user/mypage";
    }

	@GetMapping("/account/modify")
	public String edit() {
		return "user/edit";
	}

	@PostMapping("/edit/checkPw")
	@ResponseBody
	public boolean checkPw(@RequestParam String pw, @AuthenticationPrincipal CustomUser customUser) {
        if(pw == null || customUser == null) {
            System.out.println("1");
            return false;
        }
        System.out.println("2");
		return userService.checkPw(customUser, pw);
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
    public boolean verifyCode(@RequestParam String userCode, @RequestParam String email, HttpSession session) {
        if(userCode == null || email == null){
            return false;
        }
        boolean res = userService.checkCode(email, userCode);
        if(res){
            session.setAttribute("email", email);
        }
        return res;
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

    @GetMapping("/resetPw")
    public String resetPw(HttpSession session, Model model) {
        String email = (String) session.getAttribute("email");
        System.out.println("이메일 : "+email);
        model.addAttribute("ur_email", email);
        return "user/resetPw";
    }
    @PostMapping("/resetPw")
    public String resetPwPost(HttpSession session, UserVO user) {
        if(user == null || user.getUr_email() == null || user.getUr_pw() == null){
            return "redirect:/findPw";
        }
        //비밀번호 재설정 후 세션 지우기
        boolean resetRes = userService.resetPw(user);
        if(!resetRes){
            return "redirect:/findPw";
        }
        //세션에서 email 지우기
        session.removeAttribute("email");
        return "redirect:/";
    }
    
    
    



    @GetMapping("/signup/kakao") // 실제 Redirect URI 경로로 수정
    public String kakaoLogin(@RequestParam String code, HttpServletRequest request) {
        System.out.println("인가 코드: " + code);

        // 1. 인가 코드를 사용하여 액세스 토큰을 요청
        String accessToken = apiService.getKakaoAccessToken(code); 

        // 2. 액세스 토큰을 사용하여 사용자 정보를 요청
        Map<String, Object> userInfo = apiService.getKakaoUserInfo(accessToken); 
        System.out.println("사용자 정보:" + userInfo);
        // 3. 받은 사용자 정보(이메일, 닉네임 등)를 기반으로 회원가입 또는 로그인 처리
        UserDetails userDetails = apiService.processKakaoUser(userInfo);


        Authentication authentication = new UsernamePasswordAuthenticationToken(
            userDetails,
            null,
            userDetails.getAuthorities()
        );

        SecurityContext securityContext = SecurityContextHolder.getContext();
        securityContext.setAuthentication(authentication);

        // 💡 세션에 SecurityContext 저장
        HttpSession session = request.getSession(true);
        session.setAttribute(
            HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,
            securityContext
        );

        System.out.println("Spring Security 로그인 처리 완료: " + userDetails.getUsername());

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