package kr.kh.kihibooks.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import kr.kh.kihibooks.model.vo.BookVO;
import kr.kh.kihibooks.model.vo.BuyListVO;
import kr.kh.kihibooks.model.vo.EpisodeVO;
import kr.kh.kihibooks.model.vo.KeywordCategoryVO;
import kr.kh.kihibooks.model.vo.ReviewVO;
import kr.kh.kihibooks.model.vo.SubCategoryVO;

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

	List<BookVO> selectBooksByKeywords(@Param("keywordIds")List<String> keywordIds, 
																		@Param("sort")String sort, 
																		@Param("limit")int limit, 
																		@Param("offset")int offset, 
																		@Param("keywordCount")int keywordCount);

	int countBooksByKeywords(@Param("keywordIds")List<String> keywordIds, @Param("keywordCount")int keywordCount);

	BookVO selectBook(String bo_code);

	List<EpisodeVO> selectEpisodeList(String bo_code);

	List<ReviewVO> selectReviewList(String bo_code);

    List<SubCategoryVO> selectSubCategory(int mainCategoryValue);
	
	boolean insertReview(ReviewVO review);

	boolean insertAuthor(@Param("au_name")String bo_author);

	Integer getAuthorNum(String bo_author);

	String getLatestBoNum(String psCode);

	boolean insertBook(BookVO book);

	BookVO getBookCode(int bo_au_num, String bo_title, int bo_pi_num);

	boolean insertBookKeyword(@Param("bk_bo_code")String bo_code, @Param("bk_kw_code")String keywordCode);

	List<BookVO> selectEditorsBookList(int pi_num);

	boolean updateRating(String bo_code);

	List<ReviewVO> findReviewBySort(String sort, String bo_code);

    boolean updateBookInfo(BookVO book);
	
}
