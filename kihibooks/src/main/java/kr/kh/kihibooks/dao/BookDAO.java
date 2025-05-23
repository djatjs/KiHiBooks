package kr.kh.kihibooks.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import kr.kh.kihibooks.model.vo.BookVO;
import kr.kh.kihibooks.model.vo.EpisodeVO;
import kr.kh.kihibooks.model.vo.KeywordCategoryVO;
import kr.kh.kihibooks.model.vo.ReviewVO;

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

	List<BookVO> selectBestBooks(@Param("offset")int offset, 
															@Param("size")int size, // Mapper의 Limit 에 사용됨
															@Param("range")String range, // 오늘의/주간/월간 베스트 
															@Param("adultYN")String adultYN, 
															@Param("finished")String finished);

	int countBestBooks(@Param("range")String range, 
											@Param("adultYN")String adultYN, 
											@Param("finished")String finished);

	List<KeywordCategoryVO> selectKeywordCategories();

	List<BookVO> selectBooksByKeywords(@Param("keywordIds")List<Integer> keywordIds, 
																		@Param("sort")String sort, 
																		@Param("limit")int limit, 
																		@Param("offset")int offset, 
																		@Param("keywordCount")int keywordCount);

	int countBooksByKeywords(@Param("keywordIds")List<Integer> keywordIds, @Param("keywordCount")int keywordCount);

	BookVO selectBook(String bo_code);

	List<EpisodeVO> selectEpisodeList(String bo_code);

	List<ReviewVO> selectReviewList(String bo_code);
    
}
