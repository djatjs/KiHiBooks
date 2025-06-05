package kr.kh.kihibooks.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import kr.kh.kihibooks.model.vo.InterestVO;
import kr.kh.kihibooks.model.vo.LibraryVO;

public interface LibraryDAO {

    List<LibraryVO> selectMyBooks(int ur_num);

    List<InterestVO> selectMyInterests(int ur_num);

    List<InterestVO> selectInterestListForPage(@Param("ur_num")int ur_num, @Param("limit")int pageSize, @Param("offset")int offset);

    List<LibraryVO> selectBookListForPage(@Param("ur_num")int ur_num, @Param("limit")int pageSize, @Param("offset")int offset);
    
}
