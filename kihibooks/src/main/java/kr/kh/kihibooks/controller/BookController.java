package kr.kh.kihibooks.controller;

import java.security.Principal;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import javax.swing.text.StyledEditorKit.BoldAction;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import kr.kh.kihibooks.model.vo.BookKeywordVO;
import jakarta.servlet.http.HttpServletRequest;
import kr.kh.kihibooks.model.vo.BookVO;
import kr.kh.kihibooks.model.vo.BuyListVO;
import kr.kh.kihibooks.model.vo.EpisodeVO;
import kr.kh.kihibooks.model.vo.KeywordCategoryVO;
import kr.kh.kihibooks.model.vo.KeywordVO;
import kr.kh.kihibooks.model.vo.NoticeVO;
import kr.kh.kihibooks.model.vo.ReviewVO;
import kr.kh.kihibooks.model.vo.SubCategoryVO;
import kr.kh.kihibooks.model.vo.UserVO;
import kr.kh.kihibooks.model.vo.WaitForFreeVO;
import kr.kh.kihibooks.pagination.PageInfo;
import kr.kh.kihibooks.service.BookService;
import kr.kh.kihibooks.service.HomeService;
import kr.kh.kihibooks.service.KeywordService;
import kr.kh.kihibooks.service.UserService;
import kr.kh.kihibooks.utils.CustomUser;
import kr.kh.kihibooks.utils.PageConstants;
import kr.kh.kihibooks.utils.PaginationUtils;

@Controller
public class BookController {

    @Autowired
    private BookService bookService;
    @Autowired
    private UserService userService;
    @Autowired
    private HomeService homeService;
    @Autowired
    private KeywordService keywordService;

    @GetMapping("/")
    public String home(Model model) {
        int mcCode = 1; // 로맨스 장르

        List<BookVO> topBooks = bookService.getTopBooks(mcCode);
        List<BookVO> bestBooks = bookService.getBestBooks(mcCode, "popular", null, null, 1);
        List<BookVO> waitFreeBooks = bookService.getWaitFreeBooks(mcCode, "recent", null, 1);
        List<BookVO> newBooks = bookService.getNewBooks(mcCode, "recent", null, 1);
        List<KeywordVO> randomKeywords = homeService.getRandomKeywords(15);

        model.addAttribute("topBooks", topBooks);
        model.addAttribute("bestBooks", bestBooks);
        model.addAttribute("waitFreeBooks", waitFreeBooks);
        model.addAttribute("newBooks", newBooks);
        model.addAttribute("keywords", randomKeywords);
        model.addAttribute("mcCode", mcCode);

        return "home";
    }

    @GetMapping("/book/{type}")
    public String bookPage(@PathVariable("type") String type,
                        @RequestParam(name = "mcCode", required = false) Integer mcCode,
                        @RequestParam(name = "sort", defaultValue = "recent") String sort,
                        @RequestParam(name = "adult", required = false) String adult,
                        @RequestParam(name = "fin", required = false) String fin,
                        @RequestParam(name = "keyword", required = false) String keyword,
                        @RequestParam(name = "page", defaultValue = "1") int page,
                        Model model) {

        List<BookVO> bookList;
        int totalCount;

        switch (type) {
            case "new-released":
                bookList = bookService.getNewBooks(mcCode, sort, adult, page);
                totalCount = bookService.countNewBooks(mcCode, sort, adult);
                break;

            case "best":
                bookList = bookService.getBestBooks(mcCode, sort, adult, fin, page);
                totalCount = bookService.countBestBooks(mcCode, sort, adult, fin);
                break;

            case "wait-for-free":
                bookList = bookService.getWaitFreeBooks(mcCode, sort, keyword, page);
                totalCount = bookService.countWaitFreeBooks(mcCode, sort, keyword);
                break;

            default:
                return "redirect:/error";
        }

        PageInfo<BookVO> pageInfo = PaginationUtils.paginate(
            bookList, totalCount, page,
            PageConstants.PAGE_SIZE, PageConstants.BLOCK_SIZE
        );

        model.addAttribute("pageInfo", pageInfo);
        model.addAttribute("mcCode", mcCode);
        model.addAttribute("sort", sort);
        model.addAttribute("adult", adult);
        model.addAttribute("fin", fin);
        model.addAttribute("keyword", keyword);
        model.addAttribute("currentPage", page);
        model.addAttribute("pageTitle", getPageTitle(type));

        return "book/" + type;
    }


    private String getPageTitle(String type) {
        switch (type) {
            case "new-released": return "신작 웹소설";
            case "best": return "베스트 웹소설";
            case "wait-for-free": return "기다리면 무료";
            default: return "웹소설";
        }
    }

    @GetMapping("/realtime")
    @ResponseBody
    public Map<String, Object> getTopBooks(@RequestParam("mcCode")int mcCode) {
        List<BookVO> books = bookService.getTopBooks(mcCode);
        String now = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        Map<String, Object> map = new HashMap<>();
        map.put("books", books);
        map.put("lastUpdated", now);

        return map;
    }

    @GetMapping("/book/keyword")
    public String keywordSearchPage(
        @RequestParam(value = "keywordIds", required = false) List<String> keywordIds,
        @RequestParam(defaultValue = "recent") String sort,
        @RequestParam(defaultValue = "1") int page,
        Model model,
        HttpServletRequest request
    ) {
        // 1. 키워드 카테고리 + 키워드 리스트
        List<KeywordCategoryVO> keywordCategories = keywordService.getAllKeywordCategories();
        model.addAttribute("keywordCategories", keywordCategories);
        model.addAttribute("selectedKeywordIds", keywordIds != null ? keywordIds : new ArrayList<>());

        // 2. 검색 결과 도서 리스트
        PageInfo<BookVO> pageInfo = bookService.getBooksByKeywords(keywordIds, sort, page);
        model.addAttribute("bookList", pageInfo.getContent());
        model.addAttribute("pageInfo", pageInfo);
        model.addAttribute("selectedKeywordIds", keywordIds);

        System.out.println("bookList size: " + pageInfo.getContent().size());  // 책 리스트 사이즈 확인
        System.out.println("bookList: " + pageInfo.getContent()); 

        List<KeywordVO> selectedKeywords = keywordService.getSelectedKeywordsPreserveOrder(keywordIds);
        model.addAttribute("selectedKeywords", selectedKeywords);

        // 3. Ajax 요청 여부 판별 (헤더로 구분)
        String requestedWith = request.getHeader("X-Requested-With");
        if ("XMLHttpRequest".equals(requestedWith)) {
            // Ajax 요청에 대해서만 갱신된 HTML 반환
            return "book/keyword :: bookResultFragment";
        }

        // 최초 접속 or 전체 페이지 렌더링
        return "book/keyword";
    }

    @GetMapping("/book/keyword/updated")
    public String updatedKeywordSearchPage(
        @RequestParam(value = "keywordIds", required = false) List<String> keywordIds,
        @RequestParam(defaultValue = "recent") String sort,
        @RequestParam(defaultValue = "1") int page,
        Model model,
        HttpServletRequest request
    ) {
        // 1. 키워드 카테고리 + 키워드 리스트
        List<KeywordCategoryVO> keywordCategories = keywordService.getAllKeywordCategories();
        model.addAttribute("keywordCategories", keywordCategories);
        model.addAttribute("selectedKeywordIds", keywordIds != null ? keywordIds : new ArrayList<>());

        // 2. 검색 결과 도서 리스트
        PageInfo<BookVO> pageInfo = bookService.getBooksByKeywords(keywordIds, sort, page);
        model.addAttribute("bookList", pageInfo.getContent());
        model.addAttribute("pageInfo", pageInfo);
        model.addAttribute("selectedKeywordIds", keywordIds);

        System.out.println("bookList size: " + pageInfo.getContent().size());  // 책 리스트 사이즈 확인
        System.out.println("bookList: " + pageInfo.getContent()); 

        List<KeywordVO> selectedKeywords = keywordService.getSelectedKeywordsPreserveOrder(keywordIds);
        model.addAttribute("selectedKeywords", selectedKeywords);

        // Ajax 요청에 대해 fragment만 리턴
        return "book/keyword :: bookResultFragment";
    }

    //준호 영역 끝

    @GetMapping("/library/recents")
    public String recentList(Model model, Integer ur_num) {
        List<BookVO> list = bookService.getBookList(ur_num == null ? 0 : ur_num);
        model.addAttribute("bookList", list);
        return "user/recentList";
    }

    @GetMapping("/books/{bo_code}")
    public String bookDetail(Model model, @PathVariable String bo_code,
            @AuthenticationPrincipal CustomUser customUser) {
        BookVO book = bookService.getBook(bo_code);
        List<EpisodeVO> epiList = bookService.getEpisodeList(bo_code);
        List<ReviewVO> rvList = bookService.getReviewList(bo_code);
        Map<Integer, Double> rating = bookService.calcRating(rvList);
        Map<Integer, Integer> replyCountMap = new HashMap<>();
        for (ReviewVO r : rvList) {
            int oriNum = r.getRv_ori_num();
            if (oriNum != 0) { // 댓글이면
                replyCountMap.put(oriNum, replyCountMap.getOrDefault(oriNum, 0) + 1);
            }
        }
        long freeCount = epiList.stream().filter(e -> e.getEp_price() == 0).count();
        Map<Integer, Integer> likeCountMap = new HashMap<>();
        for (ReviewVO review : rvList) {
            int rvNum = review.getRv_num();
            int likeCount = bookService.getLikeCount(rvNum);
            likeCountMap.put(rvNum, likeCount);
        }
        Set<Integer> likedReviewIds = new HashSet<>();
        
        Optional<Timestamp> latestDateOpt = epiList.stream()
                .map(EpisodeVO::getEp_date)
                .max(Comparator.naturalOrder());

        String latestDate = latestDateOpt
                .map(ts -> new SimpleDateFormat("yyyy.MM.dd").format(ts))
                .orElse("날짜 없음");
        List<BookVO> abList = bookService.getAuthorAnotherBook(bo_code);
        List<NoticeVO> notiList = bookService.getNoticeList(bo_code);
        List<BookVO> bestList10 = bookService.getBestList(bo_code);
        List<BookVO> bestList5 = bookService.getBestList5(bo_code);
        List<BuyListVO> buyList = new ArrayList<>();
        Set<String> buyCodeSet = new HashSet<>();
        List<BookKeywordVO> kwList = bookService.getKeywordList(bo_code);
        WaitForFreeVO wff = new WaitForFreeVO();
        UserVO user = new UserVO();
        
        if (customUser != null) {
            int ur_num = customUser.getUser().getUr_num();
            likedReviewIds = bookService.getLikedReview(ur_num);
            buyList = bookService.getBuyList(ur_num, bo_code);
            buyCodeSet = buyList.stream().map(BuyListVO::getBl_ep_code).collect(Collectors.toSet());
            wff = userService.getWff(ur_num, bo_code);
            user = customUser.getUser();
        }
        
        model.addAttribute("book", book);
        model.addAttribute("epiList", epiList);
        model.addAttribute("rvList", rvList);
        model.addAttribute("rating", rating);
        model.addAttribute("replyCountMap", replyCountMap);
        model.addAttribute("freeCount", freeCount);
        model.addAttribute("likeCountMap", likeCountMap);
        model.addAttribute("likedReviewIds", likedReviewIds);
        model.addAttribute("latestEpDate", latestDate);
        model.addAttribute("abList", abList);
        model.addAttribute("notiList", notiList);
        model.addAttribute("bestList10", bestList10);
        model.addAttribute("bestList5", bestList5);
        model.addAttribute("buyList", buyList);
        model.addAttribute("buyCodeSet", buyCodeSet);
        model.addAttribute("kwList", kwList);
        model.addAttribute("wff", wff);
        model.addAttribute("user", user);

        return "book/detail";
    }

    @PostMapping("/review/insert")
    @ResponseBody
    public boolean insert(@RequestBody ReviewVO review, @AuthenticationPrincipal CustomUser customUser) {

        int reviewCnt = bookService.countReview(review.getRv_bo_code(), customUser.getUser().getUr_num());
        System.out.println(reviewCnt);

        if(reviewCnt == 0) {
            return bookService.insertReview(review, customUser);
        }
        
        return false;
    }

    @PostMapping("/rereview/insert")
    @ResponseBody
    public ReviewVO insertReview(@RequestBody ReviewVO review, @AuthenticationPrincipal CustomUser customUser) {
        if (!bookService.insertReReview(review, customUser)) {
            System.out.println("리뷰 등록 실패");
            return null;
        }
        System.out.println("리뷰 등록은 성공함");
        ReviewVO reply = bookService.selectReply(review);
        System.out.println("reply: " + reply);
        return reply;
    }
    
    @ResponseBody
    @GetMapping("/book/getSubCategory")
    public List<SubCategoryVO> getSubCategory(@RequestParam int mainCategoryValue) {
        if (mainCategoryValue == 0) {
            return null;
        }
        List<SubCategoryVO> subCategories = bookService.getSubCategory(mainCategoryValue);
        if (subCategories != null && !subCategories.isEmpty()) {
            return subCategories;
        }
        return null;
    }

    @GetMapping("/review/sort")
    public String getSortedReview(@RequestParam String sort, @RequestParam("bo_code") String bo_code, Model model) {
        List<ReviewVO> rvList = bookService.getRvList(sort, bo_code);
        System.out.println(rvList);
        model.addAttribute("rvList", rvList);

        return "book/reviewSort :: rvListFlag";
    }

    @PostMapping("/review/like")
    @ResponseBody
    public Map<String, Object> toggleReviewLike(@RequestBody Map<String, Integer> payload,
            @AuthenticationPrincipal CustomUser customUser) {
        int rvNum = payload.get("rv_num");
        int urNum = customUser.getUser().getUr_num();
        System.out.println(rvNum + urNum);
        boolean liked = bookService.toggleLike(rvNum, urNum);
        Integer likeCount = bookService.getLikeCount(rvNum);
        System.out.println(likeCount);
        Map<String, Object> result = new HashMap<>();

        result.put("liked", liked);
        result.put("likeCount", likeCount);
        System.out.println(result);

        return result;
    }

    @PostMapping("/review/delete")
    @ResponseBody
    public boolean deleteReview(int rv_num) {
        System.out.println(rv_num);
        boolean res = bookService.deleteReview(rv_num);
        System.out.println(res);
        return res;
    }

    @PostMapping("/cart/add")
    @ResponseBody
    public boolean addCart(@RequestBody Map<String, List<String>> payload, @AuthenticationPrincipal CustomUser customUser) {
        List<String> epCodes = payload.get("epCodes");
        int urNum = customUser.getUser().getUr_num();

        if(epCodes == null || epCodes.isEmpty()) {
            return false;
        }

        return bookService.addCart(urNum, epCodes);
    }
}
