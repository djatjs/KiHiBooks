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
import kr.kh.kihibooks.service.LibraryService;
import kr.kh.kihibooks.utils.CustomUser;


@Controller
public class LibraryCotroller {
    
    @Autowired
    LibraryService libraryService;

    @GetMapping("/mylibrary")
    public String mylibrary(@AuthenticationPrincipal CustomUser customUser, Model model) {
        List<LibraryVO> myBooks = libraryService.getMyBooks(customUser.getUser().getUr_num());
        List<InterestVO> myInterests = libraryService.getMyInterests(customUser.getUser().getUr_num());
        System.out.println(myInterests);
        model.addAttribute("myBooks", myBooks);
        model.addAttribute("myInterests", myInterests);
        return "/library/mylibrary";
    }
    

}
