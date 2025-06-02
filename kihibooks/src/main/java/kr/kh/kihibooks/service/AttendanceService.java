package kr.kh.kihibooks.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kr.kh.kihibooks.dao.AttendanceDAO;

@Service
public class AttendanceService {

    @Autowired
    private AttendanceDAO attendanceDAO;

    /** 이메일로 회원 번호 조회 */
    public int getUserNumByEmail(String email) {
        return attendanceDAO.selectUserNumByEmail(email);
    }

    /** 오늘 이미 출석했는지 확인 */
    public boolean hasAlreadyCheckedToday(int userNum) {
        Boolean result = attendanceDAO.hasCheckedToday(userNum);
        if (result != null && result) {
            return true;
        } else {
            return false;
        }
    }

    /** 출석 정보 저장 및 포인트 지급 (트랜잭션 처리) */
    @Transactional
    public void saveAttendance(int userNum, int point) {
        attendanceDAO.insertAttendance(userNum, point);
        attendanceDAO.updateUserPoint(userNum, point);
    }

    /** 이번 달 출석한 날짜 리스트 조회 */
    public List<Integer> getCheckedDays(int userNum) {
        return attendanceDAO.selectCheckedDays(userNum);
    }
}
