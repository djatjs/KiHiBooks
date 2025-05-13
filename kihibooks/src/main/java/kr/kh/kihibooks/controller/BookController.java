package kr.kh.kihibooks.controller;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;
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

    @Autowired
    private SqlSession sqlSession;
    
    @GetMapping("/realtime")
    @ResponseBody
    public Map<String, Object> getTopBooks() {
        List<BookVO> books = sqlSession.selectList("kr.kh.kihibooks.dao.BookDAO.selectTopBooks");
        String now = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        
        Map<String, Object> map = new HashMap<>();
        map.put("books", books);
        map.put("lastUpdated", now);

        return map;
    }
    
    @GetMapping("/library/recents")
	public String recentList(Model model, Integer ur_num) {
        List<BookVO> list = bookService.getBookList(ur_num==null?0:ur_num);
        model.addAttribute("bookList", list);
		return "user/recentList";
	}


    @GetMapping("/books")
    public String bookDetail(){
        return "book/detail";
    }
    
}
