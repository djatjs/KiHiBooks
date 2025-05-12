package kr.kh.kihibooks.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import kr.kh.kihibooks.model.vo.BookVO;
import kr.kh.kihibooks.service.BookService;


@Controller
public class BookController {
    
    @Autowired
    private BookService bookService;
    
    @GetMapping("/realtime")
    public List<BookVO> getMethodName(@RequestParam String param) {
        return bookService.getTopBooks();
    }

    @GetMapping("/libaray/recents")
    public String recentList(Model model, int ur_num) {
        List<BookVO> list = bookService.getBookList(ur_num);
        model.addAttribute("bookList", list);
        return "book/list";
    }
    
}
