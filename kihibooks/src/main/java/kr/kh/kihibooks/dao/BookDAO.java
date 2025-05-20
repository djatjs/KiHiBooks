package kr.kh.kihibooks.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import kr.kh.kihibooks.model.vo.BookVO;

public interface BookDAO {

	List<BookVO> selectBookList(int ur_num);	

	List<BookVO> selectTopBooks();

	List<BookVO> selectWaitFreeBooks();

	List<BookVO> selectNewBooks();

	List<BookVO> selectFilteredBooks(@Param("offset") int offset, 
																	@Param("limit") int limit, 
																	@Param("order") String order, 
																	@Param("adultYN") String adultYN
	);

	int countFilteredBooks(@Param("oreder") String order,
												@Param("adultYN") String adultYN

	);
    
}
