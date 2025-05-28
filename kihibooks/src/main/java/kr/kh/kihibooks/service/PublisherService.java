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
        String pu_code = generatePu_code();
        System.out.println("새로운 코드 : "+pu_code);
        
        //등록된 출판사인지 확인
        PublisherVO pb = publisherDAO.selectPublisher(pu_name);
        if(pb!= null){
            return false;
        }

        //출판사 등록
        return publisherDAO.insertPublisher(pu_name, pu_code);
    }

    private String generatePu_code() {
        // DB에서 가장 마지막 코드 조회 (예: "P001", "P002", ...)
        String latestCode = publisherDAO.getLatestPuCode();
        int nextNumber = 1;

        if (latestCode != null && latestCode.startsWith("P")) {
            try {
                nextNumber = Integer.parseInt(latestCode.substring(1)) + 1;
            } catch (NumberFormatException e) {
                throw new RuntimeException("코드 형식 오류: " + latestCode);
            }
        }

        // "P" 접두사 + 3자리 숫자 형식 (예: P001, P002, ...)
        return String.format("P%03d", nextNumber);
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

    public List<EditorVO> getEditorList(String pu_code) {
        return publisherDAO.selectEditors(pu_code);
    }
}
