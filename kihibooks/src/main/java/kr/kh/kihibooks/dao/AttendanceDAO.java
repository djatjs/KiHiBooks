package kr.kh.kihibooks.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

public interface AttendanceDAO {

    /** 이메일로 회원 번호 조회 */
    int selectUserNumByEmail(@Param("email") String email);

    /** 오늘 출석 여부 확인 */
    boolean hasCheckedToday(@Param("userNum") int userNum);

    /** 출석 데이터 삽입 */
    void insertAttendance(@Param("userNum") int userNum, @Param("point") int point);

    /** 유저 포인트 업데이트 */
    void updateUserPoint(@Param("userNum") int userNum, @Param("point") int point);

    /** 이번 달 출석한 날짜 리스트 조회 */
    List<Integer> selectCheckedDays(@Param("userNum") int userNum);
}
