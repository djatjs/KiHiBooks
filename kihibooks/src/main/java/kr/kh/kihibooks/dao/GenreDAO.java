package kr.kh.kihibooks.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import kr.kh.kihibooks.model.vo.BookVO;
import kr.kh.kihibooks.model.vo.KeywordVO;

public interface GenreDAO {

	List<KeywordVO> selectRandomKeywords(@Param("limit")int limit);


    
}
