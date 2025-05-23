package kr.kh.kihibooks.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import kr.kh.kihibooks.dao.BookDAO;
import kr.kh.kihibooks.model.vo.BookVO;
import kr.kh.kihibooks.model.vo.EpisodeVO;
import kr.kh.kihibooks.model.vo.ReviewVO;
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
        return book;
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
        if(review == null || customUser == null || review.getRv_content().isBlank()){
            System.out.println(review);
            System.out.println(customUser);
            return false;
        }
        review.setRv_ur_num(customUser.getUser().getUr_num());
        System.out.println(review);
        return bookDAO.insertReview(review);
    }

}
