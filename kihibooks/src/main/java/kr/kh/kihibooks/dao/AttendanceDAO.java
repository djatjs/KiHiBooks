package kr.kh.kihibooks.dao;

import java.util.List;

import kr.kh.kihibooks.model.vo.AttendanceRewardVO;

public interface AttendanceDAO {

	boolean isCheckedToday(String userId);

	List<AttendanceRewardVO> selectRewardList();

	void insertAttendance(String userId, AttendanceRewardVO reward);

	
}