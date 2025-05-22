package kr.kh.kihibooks.model.vo;

import java.util.List;

import lombok.Data;

@Data
public class KeywordCategoryVO {
	private int kc_num;
	private String kc_name;

	private List<KeywordVO> keywordList;

}
