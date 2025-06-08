package kr.kh.kihibooks.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.HttpHeaders;


import kr.kh.kihibooks.model.vo.BookVO;
import kr.kh.kihibooks.model.vo.EpisodeVO;
import kr.kh.kihibooks.model.vo.InterestVO;
import kr.kh.kihibooks.model.vo.LibraryVO;
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
        List<EpisodeVO> epiList = bookService.getEpisodeList(bo_code);
        model.addAttribute("bo_code", bo_code);
        model.addAttribute("book", book);
        model.addAttribute("epiList", epiList);
        return "/library/books";
    }
    
    @GetMapping("/mylibrary/readEpisode/{ep_code}")
    public String readEpubEpisode(@PathVariable("ep_code") String epCode, Model model) {
        EpisodeVO episode = bookService.getEpisodeByCode(epCode);
        String bo_code = episode.getEp_bo_code();
        model.addAttribute("epCode", epCode);
        model.addAttribute("boCode", bo_code);

        return "/library/readEpisode";
    }


}
