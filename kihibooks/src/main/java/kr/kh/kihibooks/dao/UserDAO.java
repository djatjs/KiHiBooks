package kr.kh.kihibooks.dao;

import kr.kh.kihibooks.model.vo.UserVO;

public interface UserDAO {

	UserVO checkPw(String ur_id, String ur_pw);
	
}