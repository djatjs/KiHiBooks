package kr.kh.kihibooks.controller;

import java.security.Principal;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

import kr.kh.kihibooks.service.AttendanceService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;




@Controller
public class AttendanceController {

	@Autowired
	private AttendanceService attendanceService;

	@GetMapping("/attendance")
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

	
	
}
