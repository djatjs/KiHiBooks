package kr.kh.kihibooks.dao;

import java.util.List;

import kr.kh.kihibooks.model.vo.KeywordCategoryVO;

public interface KeywordDAO {

	List<KeywordCategoryVO> selectKeywordCategories();
}
