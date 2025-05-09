package kr.kh.kihibooks.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.internet.MimeMessage;
import kr.kh.kihibooks.dao.UserDAO;
import kr.kh.kihibooks.model.vo.EmailVO;

@Service
public class UserService {
	
	@Autowired
	UserDAO userDAO;

	@Autowired
	private JavaMailSender mailSender;

	@Value("${spring.mail.username}")
	private String setfrom; //보내는 이의 이메일

    public boolean sendEmail(EmailVO email) {
        if(email == null || email.getEv_ur_email().length() < 1){
			return false;
		}
		try{
			//코드 생성
			String code = createCode(6);
			System.out.println(code);
			//코드 발송
			boolean sendRes = sendCodeToMail(email.getEv_ur_email(), "[KIHIBooks]이메일 인증", "인증번호는 <b>"+code+"</b> 입니다. 유출되지 않도록 해주세요.");
			//DB에 이메일, 코드, 유효시간 저장
			boolean saveRes = userDAO.

			if(!sendRes){
				return false;
			}

		}
		catch(Exception e){
			return false;
		}
		
		return true;
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

			System.out.println(message);

			mailSender.send(message);
			return true;
		}
		catch(Exception e){
			return false;
		}
	}

}
