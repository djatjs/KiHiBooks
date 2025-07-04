package kr.kh.kihibooks.model.vo;

import java.sql.Timestamp;

import lombok.Data;

@Data
public class BuyListVO {
	private String bl_id;
	private Timestamp bl_date;
	private Timestamp bl_end_date;
	private String bl_ep_code;
	private int bl_ur_num;
	private String ep_is_free;
	private String bo_wff;
	private String bl_ep_title;
	private int bl_total;
}
