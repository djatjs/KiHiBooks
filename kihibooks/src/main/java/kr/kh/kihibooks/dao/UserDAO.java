package kr.kh.kihibooks.dao;

import org.apache.ibatis.annotations.Param;

import kr.kh.kihibooks.model.vo.EmailVO;
import kr.kh.kihibooks.model.vo.UserVO;

public interface UserDAO {
    UserVO checkPw(String ur_id, String ur_pw);

    boolean insertEV(EmailVO email);

    EmailVO selectEV(String email);

    void deleteEV(String email);

    int selectCode(@Param("email") String email, @Param("userCode")String userCode);

    UserVO selectEmail(@Param("email") String email);

    int selectNickName(@Param("nickName") String nickName);

    boolean insertUserWithPw(UserVO user);

    boolean insertUserWithoutPw(UserVO user);

    boolean updatePw(UserVO user);
	
}