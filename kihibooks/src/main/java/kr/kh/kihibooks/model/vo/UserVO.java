package kr.kh.kihibooks.model.vo;

import lombok.Data;

@Data
public class UserVO {
	String ur_email;
	String ur_pw;
	String ur_nickname;
	String ur_authority;
	int ur_point;
	String ur_gender;
	String ur_year;
	String ur_del;
}
