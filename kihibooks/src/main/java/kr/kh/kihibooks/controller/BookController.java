package kr.kh.kihibooks.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import kr.kh.kihibooks.model.vo.BookVO;
import kr.kh.kihibooks.service.BookService;


@Controller
public class BookController {
    
    @Autowired
    private BookService bookService;
    
    @GetMapping("/realtime")
    @ResponseBody
    public List<BookVO> getTopBooks() {
        List<BookVO> books = bookService.getTopBooks();
        if(books == null){
            System.out.println("books is null");
        }else{
            System.out.println(books.size());
        }
        return books;
    }
    
    @GetMapping("/library/recents")
	public String recentList(Model model, Integer ur_num) {
        List<BookVO> list = bookService.getBookList(ur_num==null?0:ur_num);
        model.addAttribute("bookList", list);
		return "user/recentList";
	}
    
}
