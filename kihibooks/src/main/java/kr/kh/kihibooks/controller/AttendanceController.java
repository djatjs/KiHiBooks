package kr.kh.kihibooks.controller;

import java.security.Principal;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

import kr.kh.kihibooks.model.vo.AttendanceRewardVO;
import kr.kh.kihibooks.service.AttendanceService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;





@Controller
@RequestMapping("/attendance")
public class AttendanceController {

	@Autowired
	private AttendanceService attendanceService;

	@GetMapping("")
	public String attendancePage(Model model, Principal principal) {
			String userId = (principal != null) ?  principal.getName() : "guest";

			// 테스트용 더미값 세팅 (500 방지)
			model.addAttribute("attendanceCount", 0);
			model.addAttribute("totalPoint", 0);
			model.addAttribute("todayChecked", false);

			// calendarDays 더미 리스트로 대체
			List<String> dummyDays = Arrays.asList("1", "2", "3", "4", "5", "6", "7");
			model.addAttribute("calendarDays", dummyDays);

			return "attendance/attendance";
	}

	@PostMapping("/check")
	@ResponseBody
	public Map<String, Object> checkAttendance(Principal principal) {
		
		Map<String, Object> result = new HashMap<>();
		if(principal == null){
			result.put("status", "fail");
			result.put("message", "로그인이 필요합니다.");
			return result;
		}

		 String userId = principal.getName();
		// 이미 출석했는지 확인
		if (attendanceService.hasAlreadyCheckedToday(userId)) {
				result.put("status", "already");
				result.put("message", "오늘은 이미 출석하셨습니다.");
				return result;
		}
		// 랜덤 보상 추첨
		AttendanceRewardVO reward = attendanceService.drawRandomReward();
		// 출석 기록 저장
		attendanceService.saveAttendance(userId, reward);

		result.put("status", "success");
		result.put("point", reward.getAr_point());
		result.put("message", reward.getAr_message());
		
		return result;
	}
	

	
	
}
