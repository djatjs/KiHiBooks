package kr.kh.kihibooks.dao;

import java.util.List;
import java.util.Set;

import org.apache.ibatis.annotations.Param;

import kr.kh.kihibooks.model.vo.BookKeywordVO;
import kr.kh.kihibooks.model.vo.BookVO;
import kr.kh.kihibooks.model.vo.BuyListVO;
import kr.kh.kihibooks.model.vo.EpisodeVO;
import kr.kh.kihibooks.model.vo.KeywordCategoryVO;
import kr.kh.kihibooks.model.vo.NoticeVO;
import kr.kh.kihibooks.model.vo.ReviewLikeVO;
import kr.kh.kihibooks.model.vo.ReviewVO;
import kr.kh.kihibooks.model.vo.SubCategoryVO;

public interface BookDAO {

	List<BookVO> searchBooksByTitle(String query, int offset, int pageSize);

  int countBooksByTitle(String query);

	// ===== 장르 메인 요약 리스트 =====
	List<BookVO> selectNewBooks(@Param("mcCode") int mcCode);
	List<BookVO> selectBestBooks(@Param("mcCode") int mcCode);
	List<BookVO> selectWaitFreeBooks(@Param("mcCode") int mcCode);
	List<BookVO> selectRealtimeRankingBooks(@Param("mcCode") int mcCode);

	// ===== 탭 상세 리스트 - 신작 =====
	List<BookVO> selectNewBooksByGenre(
			@Param("mcCode") int mcCode,
			@Param("offset") int offset,
			@Param("pageSize") int pageSize,
			@Param("sort") String sort,
			@Param("adultYN") String adultYN,
			@Param("finished") String finished
	);

	int countNewBooksByGenre(
			@Param("mcCode") int mcCode,
			@Param("sort") String sort,
			@Param("adultYN") String adultYN,
			@Param("finished") String finished
	);

	// ===== 탭 상세 리스트 - 베스트 =====
	List<BookVO> selectBestBooksByGenre(
			@Param("mcCode") int mcCode,
			@Param("range") String range, // day/week/month
			@Param("offset") int offset,
			@Param("pageSize") int pageSize,
			@Param("adultYN") String adultYN,
			@Param("finished") String finished
	);

	int countBestBooksByGenre(
			@Param("mcCode") int mcCode,
			@Param("range") String range,
			@Param("adultYN") String adultYN,
			@Param("finished") String finished
	);

	// ===== 탭 상세 리스트 - 기다무 =====
	List<BookVO> selectWaitFreeBooksByGenre(
			@Param("mcCode") int mcCode,
			@Param("offset") int offset,
			@Param("pageSize") int pageSize,
			@Param("sort") String sort,
			@Param("keyword") String keyword
	);

	int countWaitFreeBooksByGenre(
			@Param("mcCode") int mcCode,
			@Param("sort") String sort,
			@Param("keyword") String keyword
	);

	List<BookVO> selectBookList(int ur_num);

	List<BookVO> selectTopBooks();

	List<KeywordCategoryVO> selectKeywordCategories();

	List<BookVO> selectBooksByKeywords(@Param("keywordIds") List<String> keywordIds,
			@Param("sort") String sort,
			@Param("limit") int limit,
			@Param("offset") int offset,
			@Param("keywordCount") int keywordCount);

	int countBooksByKeywords(@Param("keywordIds") List<String> keywordIds, @Param("keywordCount") int keywordCount);

	BookVO selectBook(String bo_code);

	List<EpisodeVO> selectEpisodeList(String bo_code);

	List<ReviewVO> selectReviewList(String bo_code);

	List<SubCategoryVO> selectSubCategory(int mainCategoryValue);

	boolean insertReview(ReviewVO review);

	boolean insertAuthor(@Param("au_name") String bo_author);

	Integer getAuthorNum(String bo_author);

	String getLatestBoNum(String psCode);

	boolean insertBook(BookVO book);

	BookVO getBookCode(int bo_au_num, String bo_title, int bo_pi_num);

	boolean insertBookKeyword(@Param("bk_bo_code") String bo_code, @Param("bk_kw_code") String keywordCode);

	List<BookVO> selectEditorsBookList(int pi_num);

	boolean updateRating(String bo_code);

	List<ReviewVO> findReviewBySort(String sort, String bo_code);

	boolean updateBookInfo(BookVO book);

	boolean insertReReview(ReviewVO review);

	ReviewVO selectReply(ReviewVO review);

	String getLatestEpNum(String bo_code);

	boolean insertEpisode(EpisodeVO ep);

	boolean updateEpisode(EpisodeVO ep);

	EpisodeVO getEpisodeByCode(String ep_code);

	ReviewVO selectReview(ReviewVO review);

	int selectLikeCount(int rv_num);

	ReviewLikeVO getLike(int rv_num, int ur_num);

	void insertLike(int rv_num, int ur_num);

	void updateLikeState(int rv_num, int ur_num, int ur_state);

	Set<Integer> selectLikedReview(int ur_num);

	boolean deleteReview(int rv_num);

	int countReview(String bo_code, int ur_num);

	int getAuthorNumByBocode(String bo_code);

	List<BookVO> getAuthorAnotherBookList(int au_num);

	List<NoticeVO> getNoticeList(String bo_code);

	String getScCodeByBoCode(String bo_code);

	List<BookVO> getBestList(String sc_code);

	List<BuyListVO> getBuyList(int ur_num, String bo_code);

	List<BookVO> getBestList6(String sc_code);

	List<BookKeywordVO> getKeywordList(String bo_code);

	int addCart(int ur_num, String ep_code);

	List<String> getCartEpCodesByUser(int ur_num);

	Integer getBookCount_BoFinIsN(String pu_code);

	List<BuyListVO> getBList(int ur_num, String bo_code);

	Integer getBookCount_BoFinIsY(String pu_code);

	boolean bookFinToY(String bo_code);

	boolean bookFinToN(String bo_code);

	// List<NoticeVO> getNoticeListForPublisher(String bo_code);

	boolean insertNotice(NoticeVO nt);

	Integer getNoticeCount(String bo_code);

	List<NoticeVO> selectNoticeList(@Param("bo_code") String bo_code, @Param("limit") int pageSize, @Param("offset") int offset);

	Integer getBookCount(String pu_code);

    List<BookVO> getPublishersBookList(String pu_code);

	boolean changeEditor(@Param("bo_code") String bo_code, @Param("pi_num") int pi_num);

	List<BookVO> getEditorsBookListToPage(@Param("pi_num") int pi_num, @Param("limit") int pageSize, @Param("offset") int offset);

	NoticeVO selectNotice(int nt_num);

	boolean updateNotice(NoticeVO nt);

	boolean deleteNotice(int nt_num);

	// List<BookVO> getAuthorsAnotherBookList(int au_num);
	
	boolean updateTotalEpisode(String bo_code);

	BookVO selectDetailBook(String bo_code);

	boolean insertBuyList(String bl_id, int ur_num, String ep_code);

	int countTodayOrders();

	Integer selectBlList(String ep_code, int ur_num);

	boolean insertLibrary(String ep_code, int ur_num);

	BookVO selectBookInfo(String bo_code);

}
