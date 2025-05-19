package kr.kh.kihibooks.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import kr.kh.kihibooks.dao.BookDAO;
import kr.kh.kihibooks.model.vo.BookVO;

@Service
public class BookService {

    @Autowired
    BookDAO bookDAO;

    public List<BookVO> getTopBooks() {
        return bookDAO.selectTopBooks();
    }

    public List<BookVO> getBookList(int ur_num) {
        return bookDAO.selectBookList(ur_num);
    }

    public List<BookVO> getWaitFreeBooks() {
        return bookDAO.selectWaitFreeBooks();
    }

    public List<BookVO> getNewBooks() {
        return bookDAO.selectNewBooks();
    }

    public List<BookVO> getFilteredNewBooks(String order, String adult, String fin) {
        return bookDAO.selectFilteredNewBooks(order, adult, fin);
    }
    

    public List<BookVO> getBestBookList(String order, String term, Boolean adult, Boolean fin) {
        return bookDAO.selectTopBooks();
    }
    

}
