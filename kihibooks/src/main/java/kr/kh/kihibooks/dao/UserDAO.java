package kr.kh.kihibooks.dao;

import kr.kh.kihibooks.model.vo.EmailVO;

public interface UserDAO {

    boolean insertEV(EmailVO email);

    EmailVO selectEV(String email);

    void deleteEV(String email);
	
}