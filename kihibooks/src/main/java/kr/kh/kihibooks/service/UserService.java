package kr.kh.kihibooks.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import kr.kh.kihibooks.dao.UserDAO;

@Service
public class UserService {
	
	@Autowired
	UserDAO userDAO;

	public boolean checkPw(String id, String pw) {
		if(id == null || pw == null) {
			return false;
		}

		return userDAO.checkPw(id, pw) == null;
	}

}
