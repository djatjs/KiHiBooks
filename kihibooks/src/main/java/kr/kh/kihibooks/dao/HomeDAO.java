package kr.kh.kihibooks.dao;

import java.util.List;

import kr.kh.kihibooks.model.vo.BookVO;
import kr.kh.kihibooks.model.vo.KeywordVO;

public interface HomeDAO {
    List<BookVO> selectNewBooks(int mcCode);
    List<BookVO> selectBestBooks(int mcCode);
    List<BookVO> selectWaitFreeBooks(int mcCode);
    List<BookVO> selectRealtimeBooks(int mcCode);
    List<KeywordVO> selectRandomKeywords(int count); 
}
