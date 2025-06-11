package kr.kh.kihibooks.service;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;

import kr.kh.kihibooks.model.vo.EpisodeVO;
import kr.kh.kihibooks.model.vo.InterestVO;
import kr.kh.kihibooks.model.vo.LibraryVO;
import kr.kh.kihibooks.utils.CustomUser;
import kr.kh.kihibooks.model.vo.CommentVO;
import kr.kh.kihibooks.dao.BookDAO;
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

    public List<InterestVO> getInterestListForPage(int ur_num, int pageSize, int offset) {
        return libraryDAO.selectInterestListForPage(ur_num, pageSize, offset);
    }

    public List<LibraryVO> getBookListForPage(int ur_num, int pageSize, int offset) {
        return libraryDAO.selectBookListForPage(ur_num, pageSize, offset);
    }

    public List<EpisodeVO> getPurchasedEpisodeList(String bo_code, int ur_num) {
        return libraryDAO.selectPurchasedEpisodeList(bo_code, ur_num);
    }

    public List<CommentVO> getComments(String epCode) {
        return libraryDAO.selectComments(epCode);
    }

    public int getLikeCount(int coNum) {
        return libraryDAO.selectLikeCount(coNum);
    }

    public Set<Integer> getLikedComment(int ur_num) {
        return libraryDAO.selectLikedComment(ur_num);
    }

    public List<CommentVO> getCommentSorted(String sort, String ep_code) {
        List<CommentVO> allComments = libraryDAO.selectCommentBySort(sort, ep_code);
        
        // 최종적으로 반환할 메인 댓글만 담긴 리스트
        List<CommentVO> mainComments = new ArrayList<>();
        Map<Integer, List<CommentVO>> replyMap = new HashMap<>();
        for (CommentVO comment : allComments) {
            if (comment.getCo_ori_num() == 0) {
                mainComments.add(comment);
                mainComments.get(mainComments.size() - 1).setReplies(new ArrayList<>());
            }
            else{
                int originalCommentNum = comment.getCo_ori_num();
                replyMap.computeIfAbsent(originalCommentNum, k -> new ArrayList<>()).add(comment);
            }
        }
        for (CommentVO mainComment : mainComments) {
            int mainCommentNum = mainComment.getCo_num();
            List<CommentVO> repliesForThisComment = replyMap.get(mainCommentNum);
            if (repliesForThisComment != null) {
                mainComment.setReplies(repliesForThisComment);
            }
        }
        return mainComments;
    }

    public boolean insertComment(CommentVO comment, CustomUser customUser) {
        if (comment == null || customUser == null || comment.getCo_content().isBlank()) {
            System.out.println(comment);
            System.out.println(customUser);
            return false;
        }
        comment.setCo_ur_num(customUser.getUser().getUr_num());

        return libraryDAO.insertComment(comment);
    }

}
