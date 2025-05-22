package kr.kh.kihibooks.model.vo;

import java.util.List;

import lombok.Data;

@Data
public class KeywordCategoryVO {
	int kc_num;
	String kc_name;

	List<KeywordVO> keywordList;

}
