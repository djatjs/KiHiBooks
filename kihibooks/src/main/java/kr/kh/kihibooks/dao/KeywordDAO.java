package kr.kh.kihibooks.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import kr.kh.kihibooks.model.vo.KeywordCategoryVO;
import kr.kh.kihibooks.model.vo.KeywordVO;

public interface KeywordDAO {
	List<KeywordCategoryVO> selectKeywordCategories();

  List<KeywordCategoryVO> selectBooksKeywordList(String bo_code);

  boolean deleteKeywordFromBook(@Param("bk_bo_code")String bo_code);
    
	List<KeywordVO> selectKeywordsByIds(List<String> keywordIds);

	List<KeywordVO> getRandomKeywordsByGenre(int genreCode);
}
