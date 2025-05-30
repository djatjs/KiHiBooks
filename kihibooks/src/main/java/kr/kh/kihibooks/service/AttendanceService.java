package kr.kh.kihibooks.service;

import java.util.List;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import kr.kh.kihibooks.dao.AttendanceDAO;
import kr.kh.kihibooks.model.vo.AttendanceRewardVO;

@Service
public class AttendanceService {

	@Autowired
	AttendanceDAO attendanceDAO;

	// 출석했는지 확인
	public boolean hasAlreadyCheckedToday(String userId) {
		return attendanceDAO.hasCheckedToday(userId);
	}

	// 랜덤 보상 추첨
	public AttendanceRewardVO drawRandomReward() {
		List<AttendanceRewardVO> rewards = attendanceDAO.selectRewardList();
		if(rewards == null || rewards.isEmpty()){
			return null;
		}

		int total = 0;
		for(AttendanceRewardVO reward : rewards){
			total += reward.getAr_probability(); //확률 총합
		}

		int rand = new Random().nextInt(total) + 1;
		int cumulative = 0;

		for(AttendanceRewardVO reward : rewards){
			cumulative += reward.getAr_probability();
			if(rand < cumulative){
				return reward;
			}
		}
		// 예외 처리 : 확률 총합이 100이 안될 경우 대비 : 마지막 보상 반환
		return rewards.get(0);
	}

	public void saveAttendance(String userId, AttendanceRewardVO reward) {
		attendanceDAO.insertAttendance(userId, reward);
	}
	
}
