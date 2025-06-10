package kr.kh.kihibooks.controller;

import java.security.Principal;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
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
import kr.kh.kihibooks.service.BookService;
import kr.kh.kihibooks.service.UserService;
import kr.kh.kihibooks.utils.CustomUser;
import kr.kh.kihibooks.model.dto.PaymentDTO;
import kr.kh.kihibooks.model.vo.EmailVO;
import kr.kh.kihibooks.model.vo.EpisodeVO;
import kr.kh.kihibooks.model.vo.UserVO;

@Controller
public class UserController {

    @Autowired
    UserService userService;

    @Autowired
    BookService bookService;

    @Autowired
    ApiService apiService;

    @Value("${kakao.client.id}")
    private String kakaoClientId;

    @Value("${kakao.redirect.uri}")
    private String kakaoRedirectUri;

    @GetMapping("/account/mykihi")
    public String mypage(Model model) {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UserVO user = userService.selectUser(userDetails.getUsername());
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
            return false;
        }
		return userService.checkPw(customUser, pw);
	}

    @GetMapping("/user/editForm")
    public String editForm(Model model, @AuthenticationPrincipal CustomUser customUser) {
        model.addAttribute("user", customUser.getUser());
        return "user/editForm";
    }

    @ResponseBody
    @PostMapping("/edit/changeNickname")
    public boolean changeNickname(@RequestParam String nickname, @AuthenticationPrincipal CustomUser customUser){
        if(nickname == null || nickname.length() == 0 || customUser == null){
            return false;
        }
        return userService.changeNickname(customUser.getUser().getUr_num(), nickname);
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
        if(!evRes) return false;
        return evRes;
    }

    @ResponseBody
    @PostMapping("/check/email")
    public boolean checkEmail(@RequestBody String email) {
        if (email == null) {
            return false;
        }
        return userService.checkEmail(email);
    }

    @ResponseBody
    @PostMapping("/check/nickName")
    public boolean checkNickName(@RequestBody String nickName) {
        if (nickName == null) {
            return false;
        }
        return userService.checkNickName(nickName);
    }

    @ResponseBody
    @PostMapping("/email/verifyCode")
    public boolean verifyCode(@RequestParam String userCode, @RequestParam String email, HttpSession session) {
        if (userCode == null || email == null) {
            return false;
        }
        boolean res = userService.checkCode(email, userCode);
        if (res) {
            session.setAttribute("email", email);
        }
        return res;
    }

    @PostMapping("/signup/email")
    public String signupEmailPost(UserVO user) {
        if (userService.signup(user)) {
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
        System.out.println("이메일 : " + email);
        model.addAttribute("ur_email", email);
        return "user/resetPw";
    }
    @ResponseBody
    @PostMapping("/resetPw")
    public boolean resetPwPost(HttpSession session, @RequestBody UserVO user) {
        if(user == null || user.getUr_email() == null || user.getUr_pw() == null){
            return false;
        }
        // 비밀번호 재설정 후 세션 지우기
        boolean resetRes = userService.resetPw(user);
        if(!resetRes){
            return false;
        }
        // 비번 찾기를 통해 비번 변경하는 경우 세션에서 email 지우기
        if (session.getAttribute("email") != null) {
            session.removeAttribute("email");
        }
        return true;
    }

    @ResponseBody
    @PostMapping("/user/resign")
    public boolean resign(@RequestParam String ur_email){
        System.out.println(ur_email);
        if(ur_email == null){
            return false;
        }
        return userService.deleteUser(ur_email);
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
                userDetails.getAuthorities());

        SecurityContext securityContext = SecurityContextHolder.getContext();
        securityContext.setAuthentication(authentication);

        HttpSession session = request.getSession(true);
        session.setAttribute(
                HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,
                securityContext);

        System.out.println("Spring Security 로그인 처리 완료: " + userDetails.getUsername());

        return "redirect:/";
    }

    @GetMapping("/order/checkout/point")
    public String point() {
        return "user/point";
    }

    @GetMapping("/order/history_point")
    public String historyPoint() {
        return "user/historyPoint";
    }

    @GetMapping("/order/history")
    public String history() {
        return "/user/history";
    }

    @GetMapping("/cart")
    public String cart(Model model, @AuthenticationPrincipal CustomUser customUser) {
        List<EpisodeVO> epList = userService.getCartEpList(customUser.getUser().getUr_num());
        model.addAttribute("epList", epList);
        return "/user/cart";
    }

    @PostMapping("/cart/delete")
    @ResponseBody
    public boolean deleteCart(@RequestBody Map<String, String> payload,
            @AuthenticationPrincipal CustomUser customUser) {
        String epCode = payload.get("epCode");
        int urNum = customUser.getUser().getUr_num();

        if (epCode == null || epCode.isEmpty()) {
            return false;
        }

        return userService.deleteCart(urNum, epCode);
    }

    @PostMapping("/cart/deleteSelected")
    @ResponseBody
    public boolean deleteSelected(@RequestBody Map<String, List<String>> payload,
            @AuthenticationPrincipal CustomUser customUser) {
        List<String> epCodes = payload.get("epCodes");
        int urNum = customUser.getUser().getUr_num();

        if (epCodes == null || epCodes.isEmpty()) {
            return false;
        }

        return userService.deleteSelected(urNum, epCodes);
    }

    @GetMapping("/order/checkout/finished")
    public String checkoutFin(@RequestParam("contents_id") String contentsId, Model model) {
        model.addAttribute("contentsId", contentsId);
        return "/user/checkoutFin";
    }

    @PostMapping("/order/free")
    @ResponseBody
    public Map<String, Object> processFreeOrder(@RequestBody Map<String, List<String>> payload, @AuthenticationPrincipal CustomUser customUser) {

        List<String> epCodes = payload.get("epCodes");

        String contentsId = userService.insertFreeOrder(epCodes, customUser.getUser().getUr_num());

        Map<String, Object> res = new HashMap<>();
        res.put("contentsId", contentsId);

        return res;
    }

    @PostMapping("/order/checkout")
    @ResponseBody
    public boolean checkout(@RequestParam List<String> epCodes, @AuthenticationPrincipal CustomUser customUser,
            HttpSession session) {
        if (epCodes == null) {
            return false;
        }

        List<EpisodeVO> epList = userService.getEpisodeByCodes(epCodes);
        if (epList == null) {
            return false;
        }

        session.setAttribute("checkoutEpList", epList);

        return true;
    }

    @GetMapping("/order/checkout")
    public String checkout(Model model, @AuthenticationPrincipal CustomUser customUser, HttpSession session) {
        List<EpisodeVO> epList = (List<EpisodeVO>) session.getAttribute("checkoutEpList");
        int total = 0;
        if (epList != null) {
            for (EpisodeVO episode : epList) {
                total += episode.getEp_price();
            }
        }
        int point = customUser.getUser().getUr_point();
        NumberFormat formatter = NumberFormat.getInstance();
        String formattedPoint = formatter.format(point);
        String formattedTotal = formatter.format(total);

        model.addAttribute("epList", epList);
        model.addAttribute("total", total);
        model.addAttribute("formattedTotal", formattedTotal);
        model.addAttribute("point", point);
        model.addAttribute("formattedPoint", formattedPoint);

        return "user/checkout";
    }

    @PostMapping("/payment/process")
    @ResponseBody
    public Map<String, Object> processPayment(@RequestBody PaymentDTO payment, @AuthenticationPrincipal CustomUser customUser) {
        Map<String, Object> response = new HashMap<>();

        if(payment.getUsePoint() > payment.getTotalAmount()) {
            response.put("success", false);
            response.put("error", "사용 포인트가 결제 금액을 초과합니다.");
            return response;
        }

        int userNum = customUser.getUser().getUr_num();

        String orderId = userService.saveTempOrder(payment, userNum);

        String method = payment.getMethod();
        String redirectUrl = "/payment/" + method.toLowerCase() + "/start?orderId=" + orderId;

        response.put("success", true);
        response.put("orderId", orderId);
        response.put("amount", payment.getTotalAmount());
        response.put("method", method);
        response.put("redirectUrl", redirectUrl);

        return response;
    }
}