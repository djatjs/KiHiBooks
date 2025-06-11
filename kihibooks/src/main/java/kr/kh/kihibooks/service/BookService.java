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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import kr.kh.kihibooks.dao.BookDAO;
import kr.kh.kihibooks.dao.KeywordDAO;
import kr.kh.kihibooks.dao.PublisherDAO;
import kr.kh.kihibooks.model.vo.BookKeywordVO;
import kr.kh.kihibooks.model.vo.BookVO;
import kr.kh.kihibooks.model.vo.BuyListVO;
import kr.kh.kihibooks.model.vo.EpisodeVO;
import kr.kh.kihibooks.model.vo.KeywordCategoryVO;
import kr.kh.kihibooks.model.vo.KeywordVO;
import kr.kh.kihibooks.model.vo.NoticeVO;
import kr.kh.kihibooks.model.vo.ReviewLikeVO;
import kr.kh.kihibooks.model.vo.ReviewVO;
import kr.kh.kihibooks.model.vo.SubCategoryVO;
import kr.kh.kihibooks.pagination.PageInfo;
import kr.kh.kihibooks.utils.CustomUser;
import kr.kh.kihibooks.utils.PageConstants;
import kr.kh.kihibooks.utils.PaginationUtils;
import kr.kh.kihibooks.utils.UploadFileUtils;

import static kr.kh.kihibooks.utils.PageConstants.*;

@Service
public class BookService {

    @Autowired
    BookDAO bookDAO;

    @Autowired
    PublisherDAO publisherDao;

    @Autowired
    KeywordDAO keywordDao;

    @Autowired
    SqlSession sqlSession;

    @Value("${spring.path.upload}")
    String uploadPath;

    private final String NAMESPACE = "kr.kh.kihibooks.dao.BookDAO.";

     // 1. 실시간 랭킹 (장르 메인에서 사용)
    public List<BookVO> getRealtimeRankingBooks(int mcCode) {
        return bookDAO.selectRealtimeRankingBooks(mcCode);
    }

    // 2. 메인용 신작 리스트 (장르 메인에서 사용)
    public List<BookVO> getNewBooks(int mcCode) {
        return bookDAO.selectNewBooks(mcCode);
    }

    // 3. 메인용 베스트 리스트 (장르 메인에서 사용)
    public List<BookVO> getBestBooks(int mcCode) {
        return bookDAO.selectBestBooks(mcCode);
    }

    // 4. 메인용 기다무 리스트 (장르 메인에서 사용)
    public List<BookVO> getWaitFreeBooks(int mcCode) {
        return bookDAO.selectWaitFreeBooks(mcCode);
    }

    // 5. 신작 탭 상세 리스트
    public PageInfo<BookVO> getNewBooksByGenre(int mcCode, int page, String sort, String adultYN, String finished) {
        int pageSize = PageConstants.PAGE_SIZE;
        int blockSize = PageConstants.BLOCK_SIZE;
        int offset = (page - 1) * pageSize;

        List<BookVO> list = bookDAO.selectNewBooksByGenre(mcCode, offset, pageSize, sort, adultYN, finished);
        int totalCount = bookDAO.countNewBooksByGenre(mcCode, sort, adultYN, finished);

        return PaginationUtils.paginate(list, totalCount, page, pageSize, blockSize);
    }

    // 6. 베스트 탭 상세 리스트
    public PageInfo<BookVO> getBestBooksByGenre(int mcCode, String range, int page, String adultYN, String finished) {
        int pageSize = PageConstants.PAGE_SIZE;
        int blockSize = PageConstants.BLOCK_SIZE;
        int offset = (page - 1) * pageSize;

        List<BookVO> list = bookDAO.selectBestBooksByGenre(mcCode, range, offset, pageSize, adultYN, finished);
        int totalCount = bookDAO.countBestBooksByGenre(mcCode, range, adultYN, finished);

        return PaginationUtils.paginate(list, totalCount, page, pageSize, blockSize);
    }

    // 7. 기다무 탭 상세 리스트
    public PageInfo<BookVO> getWaitFreeBooksByGenre(int mcCode, int page, String sort, String keyword) {
        int pageSize = PageConstants.PAGE_SIZE;
        int blockSize = PageConstants.BLOCK_SIZE;
        int offset = (page - 1) * pageSize;

        List<BookVO> list = bookDAO.selectWaitFreeBooksByGenre(mcCode, offset, pageSize, sort, keyword);
        int totalCount = bookDAO.countWaitFreeBooksByGenre(mcCode, sort, keyword);

        return PaginationUtils.paginate(list, totalCount, page, pageSize, blockSize);
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
    
    public List<BookVO> getTopBooks(int mcCode) {
        return bookDAO.selectTopBooks();
    }

    public List<BookVO> getBookList(int ur_num) {
        return bookDAO.selectBookList(ur_num);
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

        // 2. 카테고리 2자리 : bo_sc_code
        String scCode = book.getBo_sc_code();

        // 3. 도서번호 3자리
        String psCode = puCode + scCode;
        String boNum = bookDAO.getLatestBoNum(psCode);

        // 4. 생성된 bo_code 반환
        String bo_code = psCode + boNum + "";
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

    @Transactional
    public boolean updateBookInfo(BookVO book, List<String> bo_keywords) {
        if (book == null || book.getBo_code() == null || book.getBo_title().length() == 0
                || book.getBo_author().length() == 0) {
            throw new IllegalArgumentException("도서 정보가 잘못됨");
        }
        if (bo_keywords == null) {
            throw new IllegalArgumentException("키워드 목록이 잘못됨");
        }
        if (!bookDAO.updateBookInfo(book)) {
            throw new RuntimeException("도서 정보 수정 실패");
        }
        if (!keywordDao.deleteKeywordFromBook(book.getBo_code())) {
            throw new RuntimeException("키워드 삭제 실패");
        }
        for (String keywordCode : bo_keywords) {
            if (!bookDAO.insertBookKeyword(book.getBo_code(), keywordCode)) {
                throw new RuntimeException("키워드 추가 실패");
            }
        }
        return true;
    }

    public boolean insertReReview(ReviewVO review, CustomUser customUser) {
        if (review == null || customUser == null || review.getRv_content().isBlank()) {
            System.out.println("대댓 : " + review);
            return false;
        }
        review.setRv_ur_num(customUser.getUser().getUr_num());
        System.out.println(review);
        return bookDAO.insertReReview(review);
    }

    public ReviewVO selectReply(ReviewVO review) {
        return bookDAO.selectReply(review);
    }

    public boolean insertEpisode(EpisodeVO ep, String bo_code, MultipartFile epubFile, MultipartFile coverImage) {
        if (ep == null || epubFile == null || epubFile.getOriginalFilename().isEmpty() || coverImage == null
                || coverImage.getOriginalFilename().isEmpty()) {
            return false;
        }
        // 에피소드 코드 생성
        String ep_code = bo_code + bookDAO.getLatestEpNum(bo_code);
        // 썸네일 작업
        String coverName = coverImage.getOriginalFilename();
        String coverSuffix = getSuffix(coverName);
        String newCoverName = ep_code + coverSuffix;
        // epub 파일 작업
        String epubName = epubFile.getOriginalFilename();
        String epubSuffix = getSuffix(epubName);
        String newFilerName = ep_code + epubSuffix;
        // 설정한 값들 ep에 저장후 DB에 저장
        ep.setEp_file_name(newFilerName);
        ep.setEp_cover_img(newCoverName);
        ep.setEp_bo_code(bo_code);
        ep.setEp_code(ep_code);

        boolean res = bookDAO.insertEpisode(ep);
        if (!res) {
            return false;
        }
        String ep_cover_img;
        String ep_file_name;
        try {
            ep_cover_img = UploadFileUtils.uploadFile(uploadPath, newCoverName, coverImage.getBytes(),
                    bo_code + "/covers");
            ep.setEp_cover_img(ep_cover_img);
            ep_file_name = UploadFileUtils.uploadFile(uploadPath, newFilerName, epubFile.getBytes(),
                    bo_code + "/epubs");
            ep.setEp_file_name(ep_file_name);
            return bookDAO.updateEpisode(ep);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private String getSuffix(String fileName) {
        int index = fileName.lastIndexOf(".");
        return index < 0 ? null : fileName.substring(index);
    }

    public boolean updateEpisode(EpisodeVO ep, String ep_code, String bo_code, MultipartFile epubFile,
            MultipartFile coverImage) {
        if (ep == null || ep.getEp_code() == null) {
            return false;
        }
        // 기존 정보 가져오기
        EpisodeVO existing = bookDAO.getEpisodeByCode(ep.getEp_code());
        if (existing == null) {
            System.out.println("없는데요");
            return false;
        }
        String ep_cover_img;
        String ep_file_name;
        try {
            // 썸네일 변경 처리
            if (coverImage != null && !coverImage.isEmpty()) {
                // 이미지 삭제
                UploadFileUtils.deleteFile(uploadPath, existing.getEp_cover_img());
                String coverName = coverImage.getOriginalFilename();
                String coverSuffix = getSuffix(coverName);
                String newCoverName = ep.getEp_code() + coverSuffix;
                System.out.println(newCoverName);
                ep_cover_img = UploadFileUtils.uploadFile(uploadPath, newCoverName, coverImage.getBytes(),
                        bo_code + "/covers");
                ep.setEp_cover_img(ep_cover_img);
                System.out.println("이미지 삭제 및 재업로드 완료");
            }
            // epub 변경 처리
            if (epubFile != null && !epubFile.isEmpty()) {
                // EPUB 파일 삭제
                UploadFileUtils.deleteFile(uploadPath, existing.getEp_file_name());
                String epubName = epubFile.getOriginalFilename();
                String epubSuffix = getSuffix(epubName);
                String newFilerName = ep.getEp_code() + epubSuffix;
                System.out.println(newFilerName);
                ep_file_name = UploadFileUtils.uploadFile(uploadPath, newFilerName, epubFile.getBytes(),
                        bo_code + "/epubs");
                System.out.println("EPUB 파일 삭제 및 재업로드 완료");
                ep.setEp_file_name(ep_file_name);
            }
            ep.setEp_bo_code(bo_code);
            return bookDAO.updateEpisode(ep);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public EpisodeVO getEpisodeByCode(String ep_code) {
        return bookDAO.getEpisodeByCode(ep_code);
    }

    public int getLikeCount(int rv_num) {
        int likeCount = bookDAO.selectLikeCount(rv_num);

        return likeCount;
    }

    public boolean toggleLike(int rv_num, int ur_num) {
        ReviewLikeVO like = bookDAO.getLike(rv_num, ur_num);
        if (like == null) {
            bookDAO.insertLike(rv_num, ur_num);

            return true;
        } else {
            if (like.getRl_state() == 1) {
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

    public List<NoticeVO> getNoticeList(String bo_code) {
        return bookDAO.getNoticeList(bo_code);
    }

    public List<BookVO> getBestList(String bo_code) {
        String sc_code = bookDAO.getScCodeByBoCode(bo_code);

        return bookDAO.getBestList(sc_code);
    }

    public List<BuyListVO> getBuyList(int ur_num, String bo_code) {
        return bookDAO.getBuyList(ur_num, bo_code);
    }

    public List<BookVO> getBestList5(String bo_code) {
        String sc_code = bookDAO.getScCodeByBoCode(bo_code);

        return bookDAO.getBestList5(sc_code);
    }

    public List<BookKeywordVO> getKeywordList(String bo_code) {
        return bookDAO.getKeywordList(bo_code);
    }

    public int getBookCount(String pu_code) {
        if(pu_code == null || pu_code.equals("")){
            return 0;
        }
        Integer count = bookDAO.getBookCount(pu_code);
        if(count == null){
            return 0;
        }
        return count;
    }

    public int getPublishedCount(String pu_code) {
        if(pu_code == null || pu_code.equals("")){
            return 0;
        }
        Integer count = bookDAO.getBookCount_BoFinIsN(pu_code);
        if(count == null){
            return 0;
        }
        return count;
    }

    public int getCompletedCount(String pu_code) {
        if(pu_code == null || pu_code.equals("")){
            return 0;
        }
        Integer count = bookDAO.getBookCount_BoFinIsY(pu_code);
        if(count == null){
            return 0;
        }
        return count;
    }

    public boolean bookFinToY(String bo_code) {
        return bookDAO.bookFinToY(bo_code);
    }

    public boolean bookFinToN(String bo_code) {
        return bookDAO.bookFinToN(bo_code);
    }

    public boolean insertNotice(NoticeVO nt) {
        return bookDAO.insertNotice(nt);
    }

    public int getNoticeCount(String bo_code) {
        Integer count = bookDAO.getNoticeCount(bo_code);
        if(count == null){
            return 0;
        }
        return count;
    }

    public List<NoticeVO> getNoticeListForPage(String bo_code, int pageSize, int offset) {
        return bookDAO.selectNoticeList(bo_code, pageSize, offset);
    }
    
    public boolean addCart(int ur_num, List<String> epCodes) {
        List<String> existingEpCodes = bookDAO.getCartEpCodesByUser(ur_num);

        List<String> newEpCodes = epCodes.stream()
                .filter(epCode -> !existingEpCodes.contains(epCode))
                .collect(Collectors.toList());
        boolean add = false;

        for (String ep_code : newEpCodes) {
            int result = bookDAO.addCart(ur_num, ep_code);
            if (result > 0) {
                add = true;
            }
        }

        return add;
    }

    public List<BuyListVO> getBList(int ur_num, String bo_code) {
        return bookDAO.getBList(ur_num, bo_code);
    }

    public List<EpisodeVO> getEpisodeByCodes(List<String> epCodes) {
        List<EpisodeVO> epList = new ArrayList<>();
        for (String ep_code : epCodes) {
            EpisodeVO epi = bookDAO.getEpisodeByCode(ep_code);

            epList.add(epi);
        }

        return epList;
    }

    public List<BookVO> getPublishersBookList(String pu_code) {
        return bookDAO.getPublishersBookList(pu_code);
    }

    public boolean changeEditor(String bo_code, int pi_num) {
        return bookDAO.changeEditor(bo_code, pi_num);
    }

    public boolean keepBook(String bo_code, String pu_code) {
        int superNum = publisherDao.selectSuperNum(pu_code);
        return bookDAO.changeEditor(bo_code, superNum);
    }

    public List<BookVO> getEditorsBookListToPage(int pi_num, int pageSize, int offset) {
        return bookDAO.getEditorsBookListToPage(pi_num, pageSize, offset);
    }

    public NoticeVO getNotice(int nt_num) {
        return bookDAO.selectNotice(nt_num);
    }

    public boolean updateNotice(NoticeVO nt) {
        if(nt == null || nt.getNt_title() == null || nt.getNt_content() == null){
            return false;
        }
        return bookDAO.updateNotice(nt);
    }

    public boolean deleteNotice(int nt_num) {
        return bookDAO.deleteNotice(nt_num);
    }

    
}
