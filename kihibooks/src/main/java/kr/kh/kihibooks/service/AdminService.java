package kr.kh.kihibooks.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import kr.kh.kihibooks.model.vo.PublisherIdVO;
import kr.kh.kihibooks.model.vo.PublisherVO;
import kr.kh.kihibooks.model.vo.UserVO;

public class AdminService {
    @Autowired private PublisherService publisherService;
    @Autowired private UserService userService;

    @Transactional(rollbackFor = Exception.class)
    public boolean createPublisherAccount(UserVO user, String pu_name) {
        // 1. 출판사 등록 (PUBLISHER 테이블)
        if (!publisherService.signup(pu_name)) { return false; }
        
        // 2. 출판사 코드 가져오기
        PublisherVO publisher = publisherService.getPublisherByName(pu_name);
        if (publisher == null) { throw new RuntimeException("출판사 코드 조회 실패"); } // 예외 발생 유도
        
        // 3. 유저 등록 (USER 테이블)
        if (!userService.signup(user)) { return false; }
        
        // 4. 등록한 유저의 UR_NUM 가져오기
        UserVO registeredUser = userService.selectUser(user.getUr_email());
        if (registeredUser == null) { throw new RuntimeException("등록 유저 조회 실패"); } // 예외 발생 유도
        
        // 5. PUBLISHER_ID 테이블에 등록 (SUPER 권한)
        PublisherIdVO publisherId = new PublisherIdVO();
        publisherId.setPi_ur_num(registeredUser.getUr_num());
        publisherId.setPi_pu_code(publisher.getPu_code());
        publisherId.setPi_authority("super");

        if (!publisherService.insertPublisherId(publisherId)) { return false; }
        
        return true;
    }
}
