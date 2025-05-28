package kr.kh.kihibooks.model.vo;

import java.sql.Timestamp;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;

@Data
public class ReviewVO {
	private int rv_num;
	private int rv_rating;
	private String rv_spoiler;
	private String rv_content;
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
	private Timestamp rv_date;
	private String rv_del;
	private int rv_ori_num;
	private String rv_bo_code;
	private int rv_ur_num;
	private String rv_user;
	private int rv_li_count;
}
