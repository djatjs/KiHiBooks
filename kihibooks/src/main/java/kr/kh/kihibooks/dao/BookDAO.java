package kr.kh.kihibooks.dao;

import java.util.List;
import java.util.Map;

import kr.kh.kihibooks.model.vo.BookVO;

public interface BookDAO {

	List<BookVO> selectBookList(int ur_num);	

	List<BookVO> selectTopBooks();

	List<BookVO> selectWaitFreeBooks();

	List<BookVO> selectNewBooks();

	List<BookVO> selectFilteredBooks(Map<String, Object> map);

	int countFilteredBooks(Map<String, Object> map);
    
}
