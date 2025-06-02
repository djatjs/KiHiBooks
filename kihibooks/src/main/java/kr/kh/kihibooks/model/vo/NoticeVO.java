package kr.kh.kihibooks.model.vo;

import lombok.Data;

@Data
public class NoticeVO {
	private int nt_num;
	private String nt_title;
	private String nt_content;
	private int nt_pi_num;
	private String nt_bo_code;
	private String ur_nickname;
	private String nt_date;
}
