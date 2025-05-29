package kr.kh.kihibooks.dao;

import java.util.List;

import kr.kh.kihibooks.model.vo.KeywordCategoryVO;
import kr.kh.kihibooks.model.vo.KeywordVO;

public interface KeywordDAO {
	List<KeywordCategoryVO> selectKeywordCategories();
	List<KeywordVO> selectKeywordsByIds(List<String> keywordIds);
}
