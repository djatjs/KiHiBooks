package kr.kh.kihibooks.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import kr.kh.kihibooks.dao.UserDAO;
import kr.kh.kihibooks.utils.*;
import kr.kh.kihibooks.model.vo.UserVO;

@Service
public class MemberDetailService implements UserDetailsService{

	@Autowired
	UserDAO userDao;
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		UserVO user = userDao.selectEmail(username);

		return user == null ? null : new CustomUser(user);
	}

}