package kr.kh.kihibooks.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import kr.kh.kihibooks.dao.GenreDAO;
import kr.kh.kihibooks.model.vo.BookVO;

@Service
public class GenreService {

    @Autowired
    private GenreDAO genreDAO;

    public List<BookVO> getNewBooksByGenre(int genreCode) {
        return genreDAO.getNewBooksByGenre(genreCode);
    }

    public List<BookVO> getBestBooksByGenre(int genreCode) {
        return genreDAO.getBestBooksByGenre(genreCode);
    }

    public List<BookVO> getWaitFreeBooksByGenre(int genreCode) {
        return genreDAO.getWaitFreeBooksByGenre(genreCode);
    }

    public List<BookVO> getRealtimeRankingByGenre(int genreCode) {
        return genreDAO.getRealtimeRankingByGenre(genreCode);
    }

    
}
