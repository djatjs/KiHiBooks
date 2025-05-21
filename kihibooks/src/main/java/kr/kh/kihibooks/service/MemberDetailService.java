package kr.kh.kihibooks.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import kr.kh.kihibooks.dao.PublisherDAO;
import kr.kh.kihibooks.dao.UserDAO;
import kr.kh.kihibooks.utils.*;
import kr.kh.kihibooks.model.vo.PublisherIdVO;
import kr.kh.kihibooks.model.vo.UserVO;

@Service
public class MemberDetailService implements UserDetailsService{

	@Autowired
	UserDAO userDao;
	@Autowired
	PublisherDAO publisherDAO;
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		UserVO user = userDao.selectEmail(username);
		String publisher = null;
		String pi_pu_code = "";
		if(user != null){
			PublisherIdVO publisherId = new PublisherIdVO();
			publisherId.setPi_ur_num(user.getUr_num());

			PublisherIdVO tmp = publisherDAO.selectPublisherIdByNum(user.getUr_num());
			if(tmp != null){
				pi_pu_code = tmp.getPi_pu_code();
				publisherId.setPi_pu_code(pi_pu_code);

				publisher = publisherDAO.selectPublisherId(publisherId).getPi_authority();
			}
			else{
				publisher = user.getUr_authority();
			}
		}
		return user == null ? null : new CustomUser(user, publisher);
	}

}