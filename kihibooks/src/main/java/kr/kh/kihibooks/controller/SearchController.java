package kr.kh.kihibooks.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import kr.kh.kihibooks.model.vo.BookVO;
import kr.kh.kihibooks.pagination.PageInfo;
import kr.kh.kihibooks.service.BookService;

@Controller
public class SearchController {

    @Autowired
    private BookService bookService;

    @GetMapping("/search")
    public String searchBooks(@RequestParam("query") String query,
                              @RequestParam(defaultValue = "1") int page,
                              Model model) {

        // 검색어로 도서 목록 조회 + 페이징
        PageInfo<BookVO> pageInfo = bookService.searchBooksByTitle(query, page);

        model.addAttribute("query", query);
        model.addAttribute("pageInfo", pageInfo);
        return "book/search-result"; 
    }
}

