package kr.kh.kihibooks.model.vo;

import java.util.Date;

import lombok.Data;

@Data
public class EventVO {
		private int ev_num;              // 이벤트 번호 (PK)
		private String ev_title;         // 이벤트 제목
		private String ev_content;       // 이벤트 설명 or 내용
		private String ev_img;           // 썸네일 이미지 경로
		private Date ev_start_date;      // 시작일
		private Date ev_end_date; 
}
