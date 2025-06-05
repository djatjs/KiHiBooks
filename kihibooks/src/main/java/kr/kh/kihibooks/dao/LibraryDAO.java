package kr.kh.kihibooks.dao;

import java.util.List;

import kr.kh.kihibooks.model.vo.InterestVO;
import kr.kh.kihibooks.model.vo.LibraryVO;

public interface LibraryDAO {

    List<LibraryVO> selectMyBooks(int ur_num);

    List<InterestVO> selectMyInterests(int ur_num);
    
}
