package kr.kh.kihibooks.controller;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.text.StyledEditorKit.BoldAction;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import kr.kh.kihibooks.model.vo.BookVO;
import kr.kh.kihibooks.service.BookService;


@Controller
public class BookController {
    
    @Autowired
    private BookService bookService;

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
        List<BookVO> list = bookService.getBookList(ur_num==null?0:ur_num);
        model.addAttribute("bookList", list);
		return "user/recentList";
	}

    @GetMapping("/waitfree/list")
    @ResponseBody
    public List<BookVO> getWaitFreeList() {
        List<BookVO> list = bookService.getWaitFreeBooks();
        //System.out.println(list.size());
        return list;
    }


    @GetMapping("/books")
    public String bookDetail(){
        return "book/detail";
    }

    @GetMapping("/book/new-released")
    public String newBookPage(
        @RequestParam(defaultValue = "latest") String order,
        @RequestParam(required = false) Boolean adult,
        @RequestParam(required = false) Boolean fin,
        Model model) {

        String adultYN = (adult == null) ? null : (adult ? "Y" : "N");
        String finYN = (fin == null) ? null : (fin ? "Y" : "N");
        
        List<BookVO> bookList = bookService.getFilteredNewBooks(order, adultYN, finYN);
        model.addAttribute("bookList", bookList);
        model.addAttribute("order", order); // 인기순, 최신순 구별하기 위한 용도
        model.addAttribute("adult", adult);
        model.addAttribute("fin", fin);
        System.out.println(order + adultYN + finYN);
        return "book/new-released";
    }

    // @GetMapping("/book/best")
    // public String bestBookPage(@RequestParam(defaultValue = "today") String term, Model model) {
    //     List<BookVO> bestList = bookService.getBestBookList(term);
    //     model.addAttribute("bookList", bestList);
    //     model.addAttribute("term", term); // 오늘/주간/월간 중 어떤 기간인지
    //     return "book/best";
    // }


    
}
