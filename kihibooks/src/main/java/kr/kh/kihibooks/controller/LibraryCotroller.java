package kr.kh.kihibooks.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import kr.kh.kihibooks.model.vo.InterestVO;
import kr.kh.kihibooks.model.vo.LibraryVO;
import kr.kh.kihibooks.model.vo.NoticeVO;
import kr.kh.kihibooks.pagination.PageInfo;
import kr.kh.kihibooks.service.LibraryService;
import kr.kh.kihibooks.utils.CustomUser;
import kr.kh.kihibooks.utils.PaginationUtils;


@Controller
public class LibraryCotroller {
    
    @Autowired
    LibraryService libraryService;

    @GetMapping("/mylibrary")
    public String mylibrary(@AuthenticationPrincipal CustomUser customUser, Model model, @RequestParam(defaultValue = "1") int page) {
        int ur_num = customUser.getUser().getUr_num();
        List<LibraryVO> myBooks = libraryService.getMyBooks(ur_num);
        List<InterestVO> myInterests = libraryService.getMyInterests(ur_num);

        int pageSize = 5;
        int blockSize = 3;
        int offset = (page - 1) * pageSize;

        int bookCount = myBooks.size();
        int interestCount = myInterests.size();
        List<LibraryVO> bookList = libraryService.getBookListForPage(ur_num, pageSize, offset);
        List<InterestVO> interestList = libraryService.getInterestListForPage(ur_num, pageSize, offset);
        PageInfo<LibraryVO> bookPageInfo = PaginationUtils.paginate(bookList, bookCount, page, pageSize, blockSize);
        PageInfo<InterestVO> interestPageInfo = PaginationUtils.paginate(interestList, interestCount, page, pageSize, blockSize);
        model.addAttribute("bookPageInfo", bookPageInfo);
        model.addAttribute("interestPageInfo", interestPageInfo);

        return "/library/mylibrary";
    }
    

}
