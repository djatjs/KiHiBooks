package kr.kh.kihibooks.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import kr.kh.kihibooks.dao.GenreDAO;
import kr.kh.kihibooks.model.vo.KeywordVO;

@Service
public class GenreService {

    @Autowired
    private GenreDAO genreDAO;

    public List<KeywordVO> getRandomKeywords() {
        return genreDAO.selectRandomKeywords(25);
    }
    
}
