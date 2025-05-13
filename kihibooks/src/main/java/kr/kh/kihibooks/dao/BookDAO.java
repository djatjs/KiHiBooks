package kr.kh.kihibooks.dao;

import java.util.List;

import kr.kh.kihibooks.model.vo.BookVO;

public interface BookDAO {

    List<BookVO> selectBookList(int ur_num);	

    List<BookVO> selectTopBooks();

	List<BookVO> selectWaitFreeBooks();
    
}
