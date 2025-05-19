package kr.kh.kihibooks.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import jakarta.mail.internet.MimeMessage;
import kr.kh.kihibooks.dao.UserDAO;
import kr.kh.kihibooks.model.vo.EmailVO;
import kr.kh.kihibooks.model.vo.UserVO;
import kr.kh.kihibooks.utils.CustomUser;

@Service
public class UserService {
	
	@Autowired
	UserDAO userDAO;

	@Autowired
	private JavaMailSender mailSender;

	@Autowired
	PasswordEncoder passwordEncoder;

	@Value("${spring.mail.username}")
	private String setfrom; //보내는 이의 이메일

	public boolean checkPw(CustomUser customUser, String pw) {
		if(customUser == null ) {
			return false;
		}

		if(!passwordEncoder.matches(pw, customUser.getUser().getUr_pw())){
			return false;
		}

		return true;
	}

    public boolean sendEmail(EmailVO email) {
        if(email == null || email.getEv_email().length() < 1){
			return false;
		}
		try{
			//코드 생성
			String code = createCode(6);
			System.out.println(code);
			//코드 발송
			boolean sendRes = sendCodeToMail(email.getEv_email(), "[KIHIBooks]이메일 인증", "인증번호는 <b>"+code+"</b> 입니다. 유출되지 않도록 해주세요.");
			if(!sendRes){
				return false;
			}
			email.setEv_code(code);

			//DB에 인증정보 저장되어 있다면 삭제 후
			EmailVO dbEmail = userDAO.selectEV(email.getEv_email());
			if(dbEmail != null){
				userDAO.deleteEV(email.getEv_email());
			}
			//DB에 이메일, 코드, 유효시간 저장
			boolean saveRes = userDAO.insertEV(email);
			if(!saveRes){
				return false;
			}
			return true;
		}
		catch(Exception e){
			e.printStackTrace();
			return false;
		}
    }

	private String createCode(int size) {
		String code = "";
		while (code.length()<size) {
			//램덤 정수 생성
			int r = (int)(Math.random()*(62));

			if(r < 10){
				code += r;
			}
			else if(r < 36){
				code += (char)(r- 10 + 'a');
			}
			else{
				code += (char)(r- 36 + 'A');
			}
		}
		return code;
	}

	private boolean sendCodeToMail(String to, String title, String content) {
		try{
			MimeMessage message = mailSender.createMimeMessage();
			MimeMessageHelper messageHelper = new MimeMessageHelper(message, true, "UTF-8");
			
			messageHelper.setFrom(setfrom);
			messageHelper.setTo(to);
			messageHelper.setSubject(title);
			messageHelper.setText(content, true);

			mailSender.send(message);
			return true;
		}
		catch(Exception e){
			e.printStackTrace();
			return false;
		}
	}

    public boolean checkCode(String email, String userCode) {
		int count = userDAO.selectCode(email, userCode);
		if(count == 0){
			return false;
		}
		return true;
    }

    public boolean checkEmail(String email) {
		UserVO count = userDAO.selectUserByEmailAndProvider(email, "EMAIL");
		if(count != null){
			return false;
		}
		return true;
    }

	public boolean checkNickName(String nickName) {
		int count = userDAO.selectNickName(nickName);
		if(count != 0){
			return false;
		}
		return true;
    }

    public boolean signup(UserVO user) {
		//이메일로 가입중
		if(user == null){
			return false;
		}
		//이미 가입된 계정인지 확인
		if(userDAO.selectUserByEmailAndProvider(user.getUr_email(), "EMAIL") != null){
			return false;
		}

		//비번 암호화
		String encPw = passwordEncoder.encode(user.getUr_pw());
		user.setUr_pw(encPw);
		// 가입된 이메일, 닉네임인지는 html에서 확인했으므로 생략

		try {
			return userDAO.insertUserWithPw(user);
		} catch (Exception e) {
			return false;
		}
    }

	public UserVO selectUser(String userEmail) {
		if(userEmail == null){
			return null;
		}
		return userDAO.selectEmail(userEmail);
	}

    public boolean resetPw(UserVO user) {
		if(user == null || user.getUr_email() == null || user.getUr_pw() == null){
			return false;
		}
		//비번 암호화
		String encPw = passwordEncoder.encode(user.getUr_pw());
		user.setUr_pw(encPw);
		
		return userDAO.updatePw(user);
    }

	public UserVO getCurrentUser() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (auth == null || !auth.isAuthenticated()) {
			return null;
		}
		System.out.println(auth);
		String userEmail = auth.getName();
		return selectUser(userEmail);
	}

}
