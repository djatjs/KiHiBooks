package kr.kh.kihibooks.model.vo;

import lombok.Data;

@Data
public class AttendanceRewardVO {
	
	private int ar_id;
	private String ar_type;
	private int ar_point;
	private int ar_probability;
	private String ar_message;
	
}
