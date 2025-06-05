package kr.kh.kihibooks.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import kr.kh.kihibooks.model.vo.InterestVO;
import kr.kh.kihibooks.model.vo.LibraryVO;
import kr.kh.kihibooks.dao.LibraryDAO;

@Service
public class LibraryService {
    @Autowired
    LibraryDAO libraryDAO;

    public List<LibraryVO> getMyBooks(int ur_num) {
        return libraryDAO.selectMyBooks(ur_num);
    }

    public List<InterestVO> getMyInterests(int ur_num) {
        return libraryDAO.selectMyInterests(ur_num);
    }
}
