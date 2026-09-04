package kr.kh.kihibooks.utils;

import org.springframework.stereotype.Component;

@Component("ss")
public class SecurityService {


    /**
     * 도서 접근 권한 확인 (출판사/관리자용)
     * 보안 설정 해제: 항상 true 반환
     */
    public boolean canAccessBook(String bo_code) {
        return true;
    }

    /**
     * 특정 회차 파일 접근 권한 확인
     * 보안 설정 해제: 항상 true 반환
     */
    public boolean canAccessEpisode(String ep_code) {
        return true;
    }
}
