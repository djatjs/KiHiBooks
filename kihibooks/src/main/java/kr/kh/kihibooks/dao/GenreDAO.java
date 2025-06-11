package kr.kh.kihibooks.dao;

import java.util.List;

import kr.kh.kihibooks.model.vo.BookVO;
import kr.kh.kihibooks.model.vo.KeywordVO;

public interface GenreDAO {

	List<KeywordVO> selectRandomKeywords(int limit);


    
}
