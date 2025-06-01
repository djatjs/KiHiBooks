package kr.kh.kihibooks.model.vo;

import java.sql.Timestamp;

import lombok.Data;

@Data
public class EpisodeVO {
	private String ep_code;
	private String ep_title;
	private String ep_author;
	private String ep_file_name;
	private String ep_cover_img;
	private int ep_total_page;
	private int ep_cmt_count;
	private int ep_price;
	private Timestamp ep_date;
	private String ep_bo_code;
}
