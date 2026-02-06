package kr.kh.kihibooks.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.DisabledException;
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
		UserVO user = userDao.selectEmailIncludeDel(username);
		String authority = null, pu_code = null;
		int pi_num= 0;

		if(user != null){
			PublisherIdVO publisherId = new PublisherIdVO();
			publisherId.setPi_ur_num(user.getUr_num());

			PublisherIdVO tmp = publisherDAO.selectPublisherIdByNum(user.getUr_num());
			//출판사 계정인 경우
			if(tmp != null){
				publisherId.setPi_pu_code(tmp.getPi_pu_code());
				PublisherIdVO pu_id = publisherDAO.selectPublisherId(publisherId);
				authority = pu_id.getPi_authority();
				pu_code = pu_id.getPi_pu_code();
				pi_num = pu_id.getPi_num();
			}
			else authority = user.getUr_authority(); //사용자 : USER, 관리자 : ADMIN
		}
		else throw new UsernameNotFoundException("해당 사용자를 찾을 수 없습니다.");
		return user == null ? null : new CustomUser(user, authority, pu_code, pi_num);
	}

}