package kr.kh.kihibooks.model.vo;

import java.sql.Timestamp;

import lombok.Data;

@Data
public class WaitForFreeVO {
	private int wf_num;
	private Timestamp wf_can_date;
	private String wf_bo_code;
	private int wf_ur_num;
}
