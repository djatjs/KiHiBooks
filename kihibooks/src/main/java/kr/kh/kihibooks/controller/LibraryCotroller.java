package kr.kh.kihibooks.controller;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

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

import kr.kh.kihibooks.model.vo.BookKeywordVO;
import kr.kh.kihibooks.model.vo.BookVO;
import kr.kh.kihibooks.model.vo.EpisodeVO;
import kr.kh.kihibooks.model.vo.InterestVO;
import kr.kh.kihibooks.model.vo.LibraryVO;
import kr.kh.kihibooks.model.vo.ReviewVO;
import kr.kh.kihibooks.model.vo.CommentVO;
import kr.kh.kihibooks.pagination.PageInfo;
import kr.kh.kihibooks.service.BookService;
import kr.kh.kihibooks.service.LibraryService;
import kr.kh.kihibooks.utils.CustomUser;
import kr.kh.kihibooks.utils.PaginationUtils;


@Controller
public class LibraryCotroller {
    
    @Autowired
    LibraryService libraryService;

    @Autowired
    BookService bookService;
        
    @GetMapping("/mylibrary")
    public String mylibrary(
            @AuthenticationPrincipal CustomUser customUser,
            Model model,
            @RequestParam(defaultValue = "1", name = "Bpage", required = false) int Bpage,
            @RequestParam(defaultValue = "1", name = "Ipage", required = false) int Ipage,
            @RequestParam(defaultValue = "ownedBooks") String tab) {
        int ur_num = customUser.getUser().getUr_num();
        List<LibraryVO> myBooks = libraryService.getMyBooks(ur_num);
        List<InterestVO> myInterests = libraryService.getMyInterests(ur_num);

        int pageSize = 5;
        int blockSize = 3;

        // --- 소장중인 책 데이터 처리 ---
        int totalBookCount = myBooks.size();
        int bookOffset = (Bpage - 1) * pageSize;
        List<LibraryVO> bookList = libraryService.getBookListForPage(ur_num, pageSize, bookOffset);
        PageInfo<LibraryVO> bookPageInfo = PaginationUtils.paginate(bookList, totalBookCount, Bpage, pageSize, blockSize);
        model.addAttribute("bookPageInfo", bookPageInfo);

        // --- 관심 목록 데이터 처리 ---
        int totalInterestCount = myInterests.size();
        int interestOffset = (Ipage - 1) * pageSize;
        List<InterestVO> interestList = libraryService.getInterestListForPage(ur_num, pageSize, interestOffset);
        PageInfo<InterestVO> interestPageInfo = PaginationUtils.paginate(interestList, totalInterestCount, Ipage, pageSize, blockSize);
        model.addAttribute("interestPageInfo", interestPageInfo);

        // 현재 활성화된 탭 정보를 뷰로 전달 (JavaScript에서 탭 상태 유지에 사용)
        model.addAttribute("activeTab", tab);

        return "/library/mylibrary";
    }

    @GetMapping("/library/books/{bo_code}")
    public String getMethodName(@PathVariable String bo_code, Model model,@AuthenticationPrincipal CustomUser customUser) {
        BookVO book = bookService.getBook(bo_code);
        List<EpisodeVO> epiList = libraryService.getPurchasedEpisodeList(bo_code, customUser.getUser().getUr_num());
        Optional<Timestamp> latestDateOpt = epiList.stream()
        .map(EpisodeVO::getEp_date)
        .max(Comparator.naturalOrder());
        List<BookKeywordVO> kwList = bookService.getKeywordList(bo_code);
        List<BookVO> abList = bookService.getAuthorsAnotherBook(bo_code);
        String latestDate = latestDateOpt
        .map(ts -> new SimpleDateFormat("yyyy.MM.dd").format(ts))
        .orElse("날짜 없음");
        String mainCategory = bookService.getMainCategoryUrlKeyword(book.getBo_main_cate());
        System.out.println(mainCategory+"씨ㅣㅣㅣ이이이이이이");

        model.addAttribute("latestEpDate", latestDate);
        model.addAttribute("bo_code", bo_code);
        model.addAttribute("book", book);
        model.addAttribute("epiList", epiList);
        model.addAttribute("kwList", kwList);
        model.addAttribute("abList", abList);
        model.addAttribute("mainCategory", mainCategory);
        return "/library/books";
    }
    
    @GetMapping("/mylibrary/readEpisode/{ep_code}")
    public String readEpubEpisode(@PathVariable("ep_code") String epCode, Model model, @AuthenticationPrincipal CustomUser customUser) {
        EpisodeVO episode = bookService.getEpisodeByCode(epCode);
        String bo_code = episode.getEp_bo_code();
        
        List<CommentVO> comments = libraryService.getCommentSorted("", epCode);
        int commentCnt = comments.size();
        Map<Integer, Integer> commentCountMap = new HashMap<>();
        for (CommentVO c : comments) {
            int oriNum = c.getCo_ori_num();
            if (oriNum != 0) { // 댓글이면
                commentCountMap.put(oriNum, commentCountMap.getOrDefault(oriNum, 0) + 1);
            }
        }
        Map<Integer, Integer> likeCountMap = new HashMap<>();
        for (CommentVO c : comments) {
            int coNum = c.getCo_num();
            int likeCount = libraryService.getLikeCount(coNum);
            likeCountMap.put(coNum, likeCount);
        }
        Set<Integer> likedReviewIds = new HashSet<>();
        likedReviewIds = libraryService.getLikedComment(customUser.getUser().getUr_num());

        model.addAttribute("epCode", epCode);
        model.addAttribute("boCode", bo_code);
        model.addAttribute("comments", comments);
        model.addAttribute("commentCnt", commentCnt);
        model.addAttribute("commentCountMap", commentCountMap);
        model.addAttribute("likeCountMap", likeCountMap);
        model.addAttribute("user", customUser.getUser());
        model.addAttribute("likedReviewIds", likedReviewIds);

        return "/library/readEpisode";
    }

    @GetMapping("/comment/sort")
    public String getSortedReview(@RequestParam String sort, @RequestParam String ep_code, Model model, @AuthenticationPrincipal CustomUser customUser) {
        List<CommentVO> commentList = libraryService.getCommentSorted(sort, ep_code);
        System.out.println(commentList);

        Map<Integer, Integer> commentCountMap = new HashMap<>();
        for (CommentVO c : commentList) {
            int oriNum = c.getCo_ori_num();
            if (oriNum != 0) { // 댓글이면
                commentCountMap.put(oriNum, commentCountMap.getOrDefault(oriNum, 0) + 1);
            }
        }
        Map<Integer, Integer> likeCountMap = new HashMap<>();
        for (CommentVO c : commentList) {
            int coNum = c.getCo_num();
            int likeCount = libraryService.getLikeCount(coNum);
            likeCountMap.put(coNum, likeCount);
        }
        Set<Integer> likedReviewIds = new HashSet<>();
        likedReviewIds = libraryService.getLikedComment(customUser.getUser().getUr_num());

        model.addAttribute("commentList", commentList);
        model.addAttribute("commentCountMap", commentCountMap);
        model.addAttribute("likedReviewIds", likedReviewIds);
        model.addAttribute("likeCountMap", likeCountMap);

        return "library/commentSort :: commentListFlag";
    }

    @ResponseBody
    @PostMapping("/comment/insert")
    public boolean insertComment(@RequestBody CommentVO comment, @AuthenticationPrincipal CustomUser customUser) {
        return libraryService.insertComment(comment, customUser);
    }

    @ResponseBody
    @PostMapping("/comment/delete")
    public boolean deleteComment(@RequestParam int co_num, @AuthenticationPrincipal CustomUser customUser) {
        return libraryService.deleteComment(co_num, customUser);
    }

    
    @ResponseBody
    @PostMapping("/comment/insertRecomment")
    public boolean insertRecomment(@RequestBody CommentVO comment, @AuthenticationPrincipal CustomUser customUser) {
        return libraryService.insertRecomment(comment, customUser);
    }
}
