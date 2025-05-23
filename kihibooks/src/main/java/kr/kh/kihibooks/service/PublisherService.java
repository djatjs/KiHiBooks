package kr.kh.kihibooks.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;

import kr.kh.kihibooks.dao.PublisherDAO;
import kr.kh.kihibooks.dao.UserDAO;
import kr.kh.kihibooks.model.vo.EditorVO;
import kr.kh.kihibooks.model.vo.PublisherIdVO;
import kr.kh.kihibooks.model.vo.PublisherVO;
import kr.kh.kihibooks.pagination.PageInfo;
import kr.kh.kihibooks.utils.PageConstants;
import kr.kh.kihibooks.utils.PaginationUtils;

@Controller
public class PublisherService {

    @Autowired
    PublisherDAO publisherDAO;

    @Autowired
    UserDAO userDAO;

    public boolean signup(String pu_name) {
        // 등록할 때 4자리의 숫자 코드를 만들어서 pu_code에 넣기
        String pu_code = generateCode();
        
        //등록된 출판사인지 확인
        PublisherVO pb = publisherDAO.selectPublisher(pu_name);
        if(pb!= null){
            return false;
        }

        //출판사 등록
        return publisherDAO.insertPublisher(pu_name, pu_code);
    }

    private String generateCode() {
        // 4자리의 숫자 코드를 생성하는 로직 구현
        // db에 0000부터 시작하여 최근 항목에서 1씩 증가하는 코드를 생성
        String latestCode = publisherDAO.getLatestPuCode();
        int nextCode = 1;
        if (latestCode != null) {
            try {
                nextCode = Integer.parseInt(latestCode) + 1;
            } catch (NumberFormatException e) {
                throw new RuntimeException("코드 형식 오류: " + latestCode);
            }
        }
        
        // 4자리로 포맷팅 (0001, 0002, ..., 9999)
        return String.format("%04d", nextCode);
    }

    public List<PublisherVO> getAllPublishers() {
        return publisherDAO.selectAllPublishers();
    }

    public PublisherVO getPublisherByName(String pu_name) {
        return publisherDAO.selectPublisher(pu_name);
    }

    public boolean insertPublisherId(PublisherIdVO publisherId) {
        if(publisherId == null){
            return false;
        }
        //중복인지 확인
        PublisherIdVO pb = publisherDAO.selectPublisherId(publisherId);
        if(pb != null){
            return false;
        }
        return publisherDAO.insertPublisherId(publisherId);
    }

    @Transactional
    public boolean addEditor(int userNum, String puCode) {
        if (userNum == 0 || puCode == null) {
            throw new IllegalArgumentException("userNum 또는 puCode가 잘못됨");
        }
        // USER권한 -> PUBLISHER
        if (!userDAO.updateAthourityToPublisher(userNum)) {
            throw new RuntimeException("권한 업데이트 실패");
        }
        // 출판사 소속 에디터로 등록
        if (!publisherDAO.insertEditor(userNum, puCode)) {
            throw new RuntimeException("에디터 등록 실패");
        }
        return true;
    }

    // public List<EditorVO> getEditorList(String puCode) {
    //     return publisherDAO.selectEditorList(puCode);
    // }

    @Transactional
    public boolean deleteEditor(int userNum) {
        // 1. 출판사 ID 테이블에서 해당 유저 삭제
        if (!publisherDAO.deleteEditorByUserNum(userNum)) {
            throw new RuntimeException("에디터 삭제 실패");
        }
        // 2. 권한을 다시 USER로 되돌리기
        if (!userDAO.updateAuthorityToUser(userNum)) {
            throw new RuntimeException("권한 복구 실패");
        }
        return true;
    }

    public List<EditorVO> getEditorList(String puCode, int limit, int offset) {
        return publisherDAO.selectEditorList(puCode, limit, offset);
    }
    public int getEditorCount(String puCode) {
        return publisherDAO.selectEditorCount(puCode);
    }
}
