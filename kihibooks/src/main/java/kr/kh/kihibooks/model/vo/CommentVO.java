package kr.kh.kihibooks.model.vo;


import java.sql.Timestamp;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;

@Data
public class CommentVO {
	private int co_num;
	private String co_spoiler;
	private String co_content;
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
	private Timestamp co_date;
	private String co_del;
	private int co_ori_num;
	private String co_ep_code;
	private int co_ur_num;
	private String co_user;
	private int co_li_count;
}
