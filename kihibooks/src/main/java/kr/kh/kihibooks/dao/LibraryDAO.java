package kr.kh.kihibooks.dao;

import java.util.List;
import java.util.Set;

import org.apache.ibatis.annotations.Param;

import kr.kh.kihibooks.model.vo.EpisodeVO;
import kr.kh.kihibooks.model.vo.InterestVO;
import kr.kh.kihibooks.model.vo.LibraryVO;
import kr.kh.kihibooks.model.vo.CommentVO;

public interface LibraryDAO {

    List<LibraryVO> selectMyBooks(int ur_num);

    List<InterestVO> selectMyInterests(int ur_num);

    List<InterestVO> selectInterestListForPage(@Param("ur_num")int ur_num, @Param("limit")int pageSize, @Param("offset")int offset);

    List<LibraryVO> selectBookListForPage(@Param("ur_num")int ur_num, @Param("limit")int pageSize, @Param("offset")int offset);

    List<EpisodeVO> selectPurchasedEpisodeList(String bo_code, int ur_num);

    List<CommentVO> selectComments(String ep_code);

    int selectLikeCount(int coNum);

    Set<Integer> selectLikedComment(int ur_num);

    List<CommentVO> selectCommentBySort(String sort, String ep_code);

    boolean insertComment(CommentVO comment);

    boolean deleteComment(int co_num);

    boolean insertRecomment(CommentVO comment);
    
}
