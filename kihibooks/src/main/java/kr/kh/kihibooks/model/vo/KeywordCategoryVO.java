package kr.kh.kihibooks.model.vo;

import java.util.List;

import lombok.Data;

@Data
public class KeywordCategoryVO {
	private String kc_code;
	private String kc_name;

	private List<KeywordVO> keywordList;

}
