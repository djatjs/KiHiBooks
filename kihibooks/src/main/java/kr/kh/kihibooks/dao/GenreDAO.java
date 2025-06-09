package kr.kh.kihibooks.dao;

import java.util.List;

import kr.kh.kihibooks.model.vo.BookVO;

public interface GenreDAO {

	List<BookVO> getNewBooksByGenre(int genreCode);

	List<BookVO> getBestBooksByGenre(int genreCode);

	List<BookVO> getWaitFreeBooksByGenre(int genreCode);

	List<BookVO> getRealtimeRankingByGenre(int genreCode);

    
}
