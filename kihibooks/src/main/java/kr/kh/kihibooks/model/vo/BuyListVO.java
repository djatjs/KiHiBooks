package kr.kh.kihibooks.model.vo;

import java.sql.Timestamp;

import lombok.Data;

@Data
public class BuyListVO {
	private int bl_num;
	private Timestamp bl_date;
	private Timestamp bl_end_date;
	private String bl_ep_code;
	private int bl_ur_num;
	private String ep_is_free;
	private String bo_wff;
}
