package kr.kh.kihibooks.utils;

import java.util.Arrays;
import java.util.Collection;

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
	
	public CustomUser(String username, String password, Collection<? extends GrantedAuthority> authorities) {
		super(username, password, authorities);
	}
	public CustomUser(UserVO vo, String authority, String pu_code) {
		super(	vo.getUr_email(),
				vo.getUr_pw(),
				
				Arrays.asList(new SimpleGrantedAuthority("ROLE_"+vo.getUr_authority())));
		this.user = vo;
		this.authority = authority;
		this.pu_code = pu_code;
	}
}
