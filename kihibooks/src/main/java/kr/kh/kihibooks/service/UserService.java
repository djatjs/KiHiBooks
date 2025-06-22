package kr.kh.kihibooks.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.security.SecurityProperties.User;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import jakarta.mail.internet.MimeMessage;
import kr.kh.kihibooks.dao.UserDAO;
import kr.kh.kihibooks.model.dto.PaymentDTO;
import kr.kh.kihibooks.model.vo.BuyListVO;
import kr.kh.kihibooks.model.vo.EmailVO;
import kr.kh.kihibooks.model.vo.EpisodeVO;
import kr.kh.kihibooks.model.vo.OrderListVO;
import kr.kh.kihibooks.model.vo.OrderVO;
import kr.kh.kihibooks.model.vo.UserVO;
import kr.kh.kihibooks.model.vo.WaitForFreeVO;
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
	private String setfrom; // 보내는 이의 이메일

	public boolean checkPw(CustomUser customUser, String pw) {
		if (customUser == null) {
			return false;
		}

		if (!passwordEncoder.matches(pw, customUser.getUser().getUr_pw())) {
			return false;
		}

		return true;
	}

	public boolean sendEmail(EmailVO email) {
		if (email == null || email.getEv_email().length() < 1) {
			return false;
		}
		try {
			// 코드 생성
			String code = createCode(6);
			// 코드 발송
			boolean sendRes = sendCodeToMail(email.getEv_email(), "[KIHIBooks]이메일 인증",
					"인증번호는 <b>" + code + "</b> 입니다. 유출되지 않도록 해주세요.");
			if (!sendRes) {
				return false;
			}
			email.setEv_code(code);

			// DB에 인증정보 저장되어 있다면 삭제 후
			EmailVO dbEmail = userDAO.selectEV(email.getEv_email());
			if (dbEmail != null) {
				userDAO.deleteEV(email.getEv_email());
			}
			// DB에 이메일, 코드, 유효시간 저장
			boolean saveRes = userDAO.insertEV(email);
			if (!saveRes) {
				return false;
			}
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	private String createCode(int size) {
		String code = "";
		while (code.length() < size) {
			// 램덤 정수 생성
			int r = (int) (Math.random() * (62));
			if (r < 10) code += r;
			else if (r < 36) code += (char) (r - 10 + 'a');
			else code += (char) (r - 36 + 'A');
		}
		return code;
	}

	private boolean sendCodeToMail(String to, String title, String content) {
		try {
			MimeMessage message = mailSender.createMimeMessage();
			MimeMessageHelper messageHelper = new MimeMessageHelper(message, true, "UTF-8");

			messageHelper.setFrom(setfrom);
			messageHelper.setTo(to);
			messageHelper.setSubject(title);
			messageHelper.setText(content, true);

			mailSender.send(message);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public boolean checkCode(String email, String userCode) {
		int count = userDAO.selectCode(email, userCode);
		if (count == 0) {
			return false;
		}
		return true;
	}

	public boolean checkEmail(String email) {
		UserVO count = userDAO.selectEmail(email);
		if (count != null) {
			return false;
		}
		return true;
	}

	public boolean checkNickName(String nickName) {
		int count = userDAO.selectNickName(nickName);
		if (count != 0) {
			return false;
		}
		return true;
	}

	public boolean signup(UserVO user) {
		// 이메일로 가입중
		if (user == null) {
			return false;
		}
		if (userDAO.selectEmail(user.getUr_email()) != null) {
			return false;
		}

		// 비번 암호화
		String encPw = passwordEncoder.encode(user.getUr_pw());
		user.setUr_pw(encPw);
		try {
			return userDAO.insertUserWithPw(user);
		} catch (Exception e) {
			return false;
		}
	}

	public UserVO selectUser(String userEmail) {
		if (userEmail == null) {
			return null;
		}
		return userDAO.selectEmail(userEmail);
	}

	public boolean resetPw(UserVO user) {
		if (user == null || user.getUr_email() == null || user.getUr_pw() == null) {
			return false;
		}
		// 비번 암호화
		String encPw = passwordEncoder.encode(user.getUr_pw());
		user.setUr_pw(encPw);
		if (userDAO.updatePw(user)) {
			return true;
		}
		return false;
	}

	public UserVO getUserByNickName(String searchInput) {
		if (searchInput == null) {
			return null;
		}
		return userDAO.selectUserByNickName(searchInput);
	}

	public List<EpisodeVO> getCartEpList(int ur_num) {
		return userDAO.getCartEpList(ur_num);
	}

	public boolean deleteCart(int ur_num, String ep_code) {
		boolean res = false;
		int del = userDAO.deleteCart(ur_num, ep_code);

		if (del > 0) {
			res = true;
		}

		return res;
	}

	public boolean deleteSelected(int urNum, List<String> epCodes) {
		int deleteCnt = 0;

		for (String epCode : epCodes) {
			deleteCnt += userDAO.deleteCart(urNum, epCode);
		}

		return deleteCnt == epCodes.size();
	}

	public WaitForFreeVO getWff(int ur_num, String bo_code) {
		return userDAO.getWff(ur_num, bo_code);
	}

	public List<EpisodeVO> getEpisodeByCodes(List<String> epCodes) {
		List<EpisodeVO> epList = new ArrayList<>();
		for (String ep_code : epCodes) {
			EpisodeVO epi = userDAO.getEpisodeByCode(ep_code);

			epList.add(epi);
		}

		return epList;
	}

	public boolean changeNickname(int ur_num, String ur_nickname) {
		return userDAO.updateNickname(ur_num, ur_nickname);
	}

	public Integer getUrItNum(int ur_num, String bo_code) {
		return userDAO.getUrItNum(ur_num, bo_code);
	}

	public Integer getUrNsNum(int ur_num, String bo_code) {
		return userDAO.getUrNsNum(ur_num, bo_code);
	}

	public boolean insertInterest(int ur_num, String bo_code) {
		return userDAO.insertInterest(ur_num, bo_code);
	}

	public boolean deleteInterest(int ur_num, String bo_code) {
		return userDAO.deleteInterest(ur_num, bo_code);
	}

	public boolean insertNotiSet(int ur_num, String bo_code) {
		return userDAO.insertNotiSet(ur_num, bo_code);
	}

	public boolean deleteNotiSet(int ur_num, String bo_code) {
		return userDAO.deleteNotiSet(ur_num, bo_code);
	}

	private String generateOdId() {
		String date = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
		long count = userDAO.countTodayOrders() + 1;

		return date + String.format("%05d", count);
	}

	public String insertFreeOrder(List<String> epCodes, int ur_num) {

		String od_id = generateOdId();

		OrderVO order = new OrderVO();

		order.setOd_id(od_id);
		order.setOd_ur_num(ur_num);
		order.setOd_total_amount(0);
		order.setOd_created_at(LocalDateTime.now());
		order.setOd_isFree(true);
		order.setOd_method("FREE");
		System.out.println("order 확인 : " + order);
		userDAO.insertFreeOrder(order);

		for (String epCode : epCodes) {
			BuyListVO buy = new BuyListVO();
			buy.setBl_id(od_id);
			buy.setBl_ep_code(epCode);
			buy.setBl_ur_num(ur_num);

			userDAO.insertBuyList(buy);
		}

		return od_id;
	}

	public boolean deleteUser(String ur_email) {
		return userDAO.updateUserDeleted(ur_email);
	}

	public boolean saveOrderList(List<String> epCodes, int urNum) {
		if (epCodes == null || epCodes.isEmpty()) {
			return false;
		}

		for (String epCode : epCodes) {
			OrderListVO order = new OrderListVO();
			order.setOl_ep_code(epCode);
			order.setOl_ur_num(urNum);

			userDAO.insertOrderList(order);
		}

		return true;
	}

	public void deleteOrderList(int urNum) {
		userDAO.deleteOrderList(urNum);
	}

	public List<String> selectOrderList(int urNum) {
		return userDAO.selectOrderList(urNum);
	}

	public String saveTempOrder(PaymentDTO payment, int userNum) {
		String od_id = generateOdId();

		OrderVO order = new OrderVO();
		order.setOd_id(od_id);
		order.setOd_ur_num(userNum);
		order.setOd_total_amount(payment.getTotalAmount());
		order.setOd_use_point(payment.getUsePoint());
		order.setOd_final_amount(Math.max(payment.getTotalAmount() - payment.getUsePoint(), 0));
		order.setOd_method(payment.getMethod());
		order.setOd_created_at(LocalDateTime.now());

		System.out.println(order);
		System.out.println(userNum);

		userDAO.insertOrder(order);

		return od_id;
	}

	public OrderVO findById(String od_id) {
		return userDAO.selectByOdId(od_id);
	}

	public String updatePointOrder(List<String> epCodes, int ur_num, String orderId, int usePoint) {

		if (userDAO.updatePointOrder(orderId)) {
			userDAO.updateUsePoint(ur_num, usePoint);
		}

		userDAO.deleteOrderList(ur_num);

		return orderId;
	}

	public void chargeBeforePay(String orderId, int userNum, List<String> epCodes, Integer chargeAmount,
			Integer finalAmount) {
		for (String epCode : epCodes) {
			BuyListVO buy = new BuyListVO();
			buy.setBl_id(orderId);
			buy.setBl_ep_code(epCode);
			buy.setBl_ur_num(userNum);

			userDAO.insertBuyList(buy);
		}
		userDAO.updateChargeOrder(orderId);
		userDAO.updateChargePoint(userNum, chargeAmount);
		userDAO.updateUsePoint(userNum, finalAmount);
	}

	public void justPay(String orderId, int userNum, List<String> epCodes) {
		for (String epCode : epCodes) {
			BuyListVO buy = new BuyListVO();
			buy.setBl_id(orderId);
			buy.setBl_ep_code(epCode);
			buy.setBl_ur_num(userNum);

			userDAO.insertBuyList(buy);
		}

		userDAO.updateChargeOrder(orderId);
	}

	public List<String> getEpCodesByBlId(String contentsId) {
		return userDAO.selectEpCodesByBlId(contentsId);
	}

	public List<BuyListVO> getBuyList(int ur_num) {
		List<String> blIds = userDAO.getBlIds(ur_num);
		List<BuyListVO> buyList = new ArrayList<>();
		for(String bl_id : blIds) {
			BuyListVO buy = userDAO.selectBuyListUrNum(ur_num, bl_id);
			buyList.add(buy);
		}
		
		return buyList;
	}

	public List<BuyListVO> getBList(int ur_num) {
		return userDAO.selectBList(ur_num);
	}

	public Integer selectTotalByOdId(String blId) {
		return userDAO.selectTotalByOdId(blId);
	}

	public Integer countCart(String ur_email) {
		int ur_num = userDAO.selectUrNumByEmail(ur_email);

		return userDAO.countCart(ur_num);
	}

	public Integer countLib(String ur_email) {
		int ur_num = userDAO.selectUrNumByEmail(ur_email);
		return userDAO.countLib(ur_num);
	}

	public void charge(int userNum, int totalCredit) {
		userDAO.updatePoint(userNum, totalCredit);
	}

}
