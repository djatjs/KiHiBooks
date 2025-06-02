package kr.kh.kihibooks.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import kr.kh.kihibooks.service.AttendanceService;
import kr.kh.kihibooks.utils.CustomUser;
import kr.kh.kihibooks.model.dto.AttendanceDTO;

@Controller
@RequestMapping("/attendance")
public class AttendanceController {

    @Autowired
    private AttendanceService attendanceService;

    @GetMapping
		public String attendancePage(Model model, @AuthenticationPrincipal CustomUser customUser) {
				System.out.println("✅ attendancePage() 호출됨");

				if (customUser == null) {
						System.out.println("❌ customUser is NULL");
						model.addAttribute("isLoggedIn", false); // ✅ 이름 맞춤
						return "attendance/attendance";
				}

				int userNum = customUser.getUser().getUr_num();
				List<Integer> checkedDays = attendanceService.getCheckedDays(userNum);

				model.addAttribute("isLoggedIn", true); // ✅ JS에서 쓰는 이름
				model.addAttribute("checkedDays", checkedDays);
				return "attendance/attendance";
		}

		@PostMapping("/check")
		@ResponseBody
		public AttendanceDTO checkAttendance(@AuthenticationPrincipal CustomUser customUser) {
				System.out.println("✅ checkAttendance() 호출됨");

				if (customUser == null) {
						System.out.println("❌ customUser is NULL");
						return new AttendanceDTO("fail", "로그인이 필요합니다.", 0);
				}

				int userNum = customUser.getUser().getUr_num();

				if (attendanceService.hasAlreadyCheckedToday(userNum)) {
						return new AttendanceDTO("already", "이미 출석하셨습니다.", 0);
				}

				int point = 100;
				attendanceService.saveAttendance(userNum, point);
				return new AttendanceDTO("success", "출석 완료! +" + point + "P 지급", point);
		}
}
