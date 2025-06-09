package kr.kh.kihibooks.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import kr.kh.kihibooks.dao.HomeDAO;
import kr.kh.kihibooks.model.vo.BookVO;
import kr.kh.kihibooks.model.vo.KeywordVO;

@Service
public class HomeService {

    @Autowired
    private HomeDAO homeDAO;

    public List<BookVO> getNewBooks(int mcCode) {
        return homeDAO.selectNewBooks(mcCode);
    }

    public List<BookVO> getBestBooks(int mcCode) {
        return homeDAO.selectBestBooks(mcCode);
    }

    public List<BookVO> getWaitFreeBooks(int mcCode) {
        return homeDAO.selectWaitFreeBooks(mcCode);
    }

    public List<BookVO> getRealtimeBooks(int mcCode) {
        return homeDAO.selectRealtimeBooks(mcCode);
    }

    public List<KeywordVO> getRandomKeywords(int count) {
        return homeDAO.selectRandomKeywords(count);
    }
}
