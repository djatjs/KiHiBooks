package kr.kh.kihibooks.controller;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import javax.swing.text.StyledEditorKit.BoldAction;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import jakarta.servlet.http.HttpServletRequest;
import kr.kh.kihibooks.model.vo.BookVO;
import kr.kh.kihibooks.model.vo.BuyListVO;
import kr.kh.kihibooks.model.vo.EpisodeVO;
import kr.kh.kihibooks.model.vo.KeywordCategoryVO;
import kr.kh.kihibooks.model.vo.KeywordVO;
import kr.kh.kihibooks.model.vo.ReviewVO;
import kr.kh.kihibooks.model.vo.SubCategoryVO;
import kr.kh.kihibooks.pagination.PageInfo;
import kr.kh.kihibooks.service.BookService;
import kr.kh.kihibooks.service.KeywordService;
import kr.kh.kihibooks.utils.CustomUser;
import kr.kh.kihibooks.utils.PageConstants;

@Controller
public class BookController {

    @Autowired
    private BookService bookService;

    @Autowired
    private KeywordService keywordService;

    @GetMapping("/realtime")
    @ResponseBody
    public Map<String, Object> getTopBooks() {
        List<BookVO> books = bookService.getTopBooks();
        String now = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        Map<String, Object> map = new HashMap<>();
        map.put("books", books);
        map.put("lastUpdated", now);

        return map;
    }

    @GetMapping("/best")
    @ResponseBody
    public List<BookVO> getBestBooks() {
        List<BookVO> books = bookService.getTopBooks();
        return books;
    }

    @GetMapping("/newpublish")
    @ResponseBody
    public List<BookVO> getNewPublishBooks() {
        List<BookVO> books = bookService.getNewBooks();
        return books;
    }

    @GetMapping("/library/recents")
    public String recentList(Model model, Integer ur_num) {
        List<BookVO> list = bookService.getBookList(ur_num == null ? 0 : ur_num);
        model.addAttribute("bookList", list);
        return "user/recentList";
    }

    @GetMapping("/waitfree/list")
    @ResponseBody
    public List<BookVO> getWaitFreeList() {
        List<BookVO> list = bookService.getWaitFreeBooks();
        // System.out.println(list.size());
        return list;
    }

    @GetMapping("/books/{bo_code}")
    public String bookDetail(Model model, @PathVariable String bo_code) {
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
        
        model.addAttribute("book", book);
        model.addAttribute("epiList", epiList);
        model.addAttribute("rvList", rvList);
        model.addAttribute("rating", rating);
        model.addAttribute("replyCountMap", replyCountMap);

        return "book/detail";
    }

    @PostMapping("/review/insert")
    @ResponseBody
    public boolean insert(@RequestBody ReviewVO review, @AuthenticationPrincipal CustomUser customUser) {
        System.out.println("컨트롤러" + customUser);
        return bookService.insertReview(review, customUser);
    }

    @PostMapping("/rereview/insert")
    @ResponseBody
    public ReviewVO insertReview(@RequestBody ReviewVO review, @AuthenticationPrincipal CustomUser customUser) {
        if(!bookService.insertReReview(review, customUser)){
            System.out.println("리뷰 등록 실패");
            return null;
        }
        System.out.println("리뷰 등록은 성공함");
        ReviewVO reply = bookService.selectReply(review);
        System.out.println("reply: " + reply);
        return reply;
    }

    // 신간
    @GetMapping("/book/new-released")
    public String newReleased(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "latest") String order,
            @RequestParam(required = false) String adult,
            Model model) {

        System.out.println(order);

        if (order == null || order.equals("latest")) {
            order = "recent";
        }

        PageInfo<BookVO> pageInfo = bookService.getFilteredBooks(page, order, adult);

        model.addAttribute("bookList", pageInfo.getContent());
        model.addAttribute("pageInfo", pageInfo);
        model.addAttribute("order", order);
        model.addAttribute("adult", adult);

        return "book/new-released";
    }

    @GetMapping("/book/best")
    public String bestBooks(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "day") String range,
            @RequestParam(required = false) String adultYN,
            @RequestParam(required = false) String finished,
            Model model) {
        if (range == null || range.isEmpty()) {
            range = "day";
        }
        int size = PageConstants.PAGE_SIZE;
        PageInfo<BookVO> pageInfo = bookService.getBestBooks(page, size, range, adultYN, finished);
        System.out.println(pageInfo.getTotalPages());

        model.addAttribute("bookList", pageInfo.getContent());
        model.addAttribute("pageInfo", pageInfo);
        model.addAttribute("range", range);
        model.addAttribute("adultYN", adultYN);
        model.addAttribute("finished", finished);

        return "book/best";
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

}
