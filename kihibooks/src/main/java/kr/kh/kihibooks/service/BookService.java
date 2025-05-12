package kr.kh.kihibooks.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import kr.kh.kihibooks.dao.BookDAO;
import kr.kh.kihibooks.model.vo.BookVO;

public class BookService {

    @Autowired
    BookDAO bookDAO;

    public List<BookVO> getTopBooks() {
        throw new UnsupportedOperationException("Unimplemented method 'getTopBooks'");
    }

    public List<BookVO> getBookList(int ur_num) {
        return bookDAO.selectBookList(ur_num);
    }

}
