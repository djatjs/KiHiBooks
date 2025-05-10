package kr.kh.kihibooks.dao;

import org.apache.ibatis.annotations.Param;

import kr.kh.kihibooks.model.vo.EmailVO;

public interface UserDAO {

    boolean insertEV(EmailVO email);

    EmailVO selectEV(String email);

    void deleteEV(String email);

    int selectCode(@Param("email") String email, @Param("userCode")String userCode);
	
}