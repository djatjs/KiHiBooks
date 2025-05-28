package kr.kh.kihibooks.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import kr.kh.kihibooks.dao.BookDAO;
import kr.kh.kihibooks.model.vo.BookVO;
import kr.kh.kihibooks.model.vo.BuyListVO;
import kr.kh.kihibooks.model.vo.EpisodeVO;
import kr.kh.kihibooks.model.vo.KeywordCategoryVO;
import kr.kh.kihibooks.model.vo.ReviewLikeVO;
import kr.kh.kihibooks.model.vo.ReviewVO;
import kr.kh.kihibooks.model.vo.SubCategoryVO;
import kr.kh.kihibooks.pagination.PageInfo;
import kr.kh.kihibooks.utils.CustomUser;
import kr.kh.kihibooks.utils.PageConstants;
import kr.kh.kihibooks.utils.PaginationUtils;

import static kr.kh.kihibooks.utils.PageConstants.*;

@Service
public class BookService {

    @Autowired
    BookDAO bookDAO;

    @Autowired
    SqlSession sqlSession;

    private final String NAMESPACE = "kr.kh.kihibooks.dao.BookDAO.";

    public List<BookVO> getTopBooks() {
        return bookDAO.selectTopBooks();
    }

    public List<BookVO> getBookList(int ur_num) {
        return bookDAO.selectBookList(ur_num);
    }

    public List<BookVO> getWaitFreeBooks() {
        return bookDAO.selectWaitFreeBooks();
    }

    public List<BookVO> getNewBooks() {
        return bookDAO.selectNewBooks();
    }

    public PageInfo<BookVO> getFilteredBooks(int page, String order, String adult) {
        int offset = (page - 1) * PAGE_SIZE;
        List<BookVO> books = bookDAO.selectFilteredBooks(offset, PageConstants.PAGE_SIZE, order, adult);
        int totalCount = bookDAO.countFilteredBooks(order, adult);

        return PaginationUtils.paginate(books, totalCount, page, PAGE_SIZE, BLOCK_SIZE);
    }

    public PageInfo<BookVO> getBestBooks(int page, int size, String range, String adultYN, String finished) {
        int offset = (page - 1) * size;
        int total = bookDAO.countBestBooks(range, adultYN, finished);

        List<BookVO> content = bookDAO.selectBestBooks(offset, size, range, adultYN, finished);

        return PaginationUtils.paginate(content, total, page, size, BLOCK_SIZE);
    }

    public PageInfo<BookVO> getBooksByKeywords(List<String> keywordIds, String sort, int page) {
        if (keywordIds == null) {
            keywordIds = new ArrayList<>();
        }
        int offset = (page - 1) * PAGE_SIZE;
        List<BookVO> books = bookDAO.selectBooksByKeywords(keywordIds, sort, PAGE_SIZE, offset, keywordIds.size());
        int totalCount = bookDAO.countBooksByKeywords(keywordIds, keywordIds.size());

        return PaginationUtils.paginate(books, totalCount, page, PAGE_SIZE, BLOCK_SIZE);
    }

    public BookVO getBook(String bo_code) {
        BookVO book = bookDAO.selectBook(bo_code);
        if (bookDAO.updateRating(bo_code)) {
            return book;
        }
        return null;
    }

    public List<EpisodeVO> getEpisodeList(String bo_code) {
        List<EpisodeVO> epiList = bookDAO.selectEpisodeList(bo_code);

        return epiList;
    }

    public List<ReviewVO> getReviewList(String bo_code) {
        List<ReviewVO> rvList = bookDAO.selectReviewList(bo_code);
        
        return rvList;
    }

    public Map<Integer, Double> calcRating(List<ReviewVO> rvList) {
        Map<Integer, Long> countMap = rvList.stream()
                .collect(Collectors.groupingBy(
                        rv -> rv.getRv_rating() / 2, // 정수 나눗셈인지 확인
                        Collectors.counting()));

        long total = rvList.size();

        Map<Integer, Double> ratioMap = new HashMap<>();
        for (int i = 1; i <= 5; i++) {
            long count = countMap.getOrDefault(i, 0L);
            double ratio = total > 0 ? (count * 100.0 / total) : 0.0;
            ratioMap.put(i, ratio);
        }
        return ratioMap;
    }

    public boolean insertReview(ReviewVO review, CustomUser customUser) {
        if (review == null || customUser == null || review.getRv_content().isBlank()) {
            System.out.println(review);
            System.out.println(customUser);
            return false;
        }
        review.setRv_ur_num(customUser.getUser().getUr_num());
        return bookDAO.insertReview(review);
    }

    public List<SubCategoryVO> getSubCategory(int mainCategoryValue) {
        return bookDAO.selectSubCategory(mainCategoryValue);
    }

    public int getAuthorNum(String bo_author) {
        Integer num = bookDAO.getAuthorNum(bo_author);
        return (num != null) ? num : 0;
    }

    public int addAuthor(String bo_author) {
        if (!bookDAO.insertAuthor(bo_author)) {
            return 0;
        }
        return bookDAO.getAuthorNum(bo_author);
    }

    public boolean addBook(BookVO book, String pu_code) {
        // bo_code 생성 (출판사 코드 4자리 + 카테고리 2자리 + 도서번호 3자리) EX: P0001310001
        // 1. 출판사 코드 4자리
        String puCode = pu_code;
        System.out.println("출판사 코드 : "+puCode);

        // 2. 카테고리 2자리 : bo_sc_code
        String scCode = book.getBo_sc_code();
        System.out.println("카테고리 : "+scCode);

        // 3. 도서번호 3자리
        String psCode = puCode+ scCode;
        System.out.println("psCode : "+psCode);
        String boNum = bookDAO.getLatestBoNum(psCode);
        
        // 4. 생성된 bo_code 반환
        String bo_code = psCode + boNum +"";
        System.out.println(bo_code);
        book.setBo_code(bo_code);
        return bookDAO.insertBook(book);
    }

    public String getBookCode(int bo_au_num, String bo_title, int bo_pi_num) {
        BookVO book = bookDAO.getBookCode(bo_au_num, bo_title, bo_pi_num);
        return book.getBo_code();
    }

    public boolean addBookKeyword(String bo_code, List<String> keywordCodes) {
        for (String keywordCode : keywordCodes) {
            if (!bookDAO.insertBookKeyword(bo_code, keywordCode)) {
                return false;
            }
        }
        return true;
    }

    public List<ReviewVO> getRvList(String sort, String bo_code) {
        return bookDAO.findReviewBySort(sort, bo_code);
    }

    public List<BookVO> getEditorsBookList(int pi_num) {
        return bookDAO.selectEditorsBookList(pi_num);
    }

    public boolean insertReReview(ReviewVO review, CustomUser customUser) {
        if (review == null || customUser == null || review.getRv_content().isBlank()) {
            System.out.println("대댓 : "+review);
            return false;
        }
        review.setRv_ur_num(customUser.getUser().getUr_num());
        System.out.println(review);
        return bookDAO.insertReReview(review);
    }

	public ReviewVO selectReply(ReviewVO review) {
        return bookDAO.selectReply(review);
	}

	public int getLikeCount(int rv_num) {
        int likeCount = bookDAO.selectLikeCount(rv_num);

		
        return likeCount;
	}

    public boolean toggleLike(int rv_num, int ur_num) {
        ReviewLikeVO like = bookDAO.getLike(rv_num, ur_num);
        if(like == null) {
            bookDAO.insertLike(rv_num, ur_num);

            return true;
        } else {
            if(like.getRl_state() == 1) {
                bookDAO.updateLikeState(rv_num, ur_num, 0);
                return false;
            } else {
                bookDAO.updateLikeState(rv_num, ur_num, 1);
                return true;
            }
        }
    }

		public Set<Integer> getLikedReview(int ur_num) {
			return bookDAO.selectLikedReview(ur_num);
		}

        public boolean deleteReview(int rv_num) {
            return bookDAO.deleteReview(rv_num);
        }

        public int countReview(String bo_code, int ur_num) {
            return bookDAO.countReview(bo_code, ur_num);
        }

        public List<BookVO> getAuthorAnotherBook(String bo_code) {
            int au_num = bookDAO.getAuthorNumByBocode(bo_code);
            
            return bookDAO.getAuthorAnotherBookList(au_num);
        }
}
