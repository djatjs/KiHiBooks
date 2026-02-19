package kr.kh.kihibooks.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

import kr.kh.kihibooks.model.vo.UserVO;
import lombok.Data;

@Data
public class CustomUser extends User {
	
	private UserVO user;
	private String authority;
	private String pu_code;
	private int pi_num;
	
	public CustomUser(String username, String password, Collection<? extends GrantedAuthority> authorities) {
		super(username, password, authorities);
	}
	public CustomUser(UserVO vo, String authority, String pu_code, int pi_num) {
		super(	vo.getUr_email(),
				vo.getUr_pw(),
				getAuthorities(vo, authority));
		this.user = vo;
		this.authority = authority;
		this.pu_code = pu_code;
		this.pi_num = pi_num;
	}

	private static Collection<? extends GrantedAuthority> getAuthorities(UserVO vo, String authority) {
		List<SimpleGrantedAuthority> authorities = new ArrayList<>();
		authorities.add(new SimpleGrantedAuthority("ROLE_" + vo.getUr_authority()));
		
		// 출판사 사용자인 경우 세부 권한(SUPER, EDITOR)을 추가
		if ("PUBLISHER".equals(vo.getUr_authority()) && authority != null && !authority.equals("PUBLISHER")) {
			authorities.add(new SimpleGrantedAuthority("ROLE_" + authority));
		}
		return authorities;
	}
}
