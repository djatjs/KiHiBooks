package kr.kh.kihibooks.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import kr.kh.kihibooks.dao.PublisherDAO;
import kr.kh.kihibooks.model.vo.PublisherIdVO;
import kr.kh.kihibooks.model.vo.PublisherVO;

@Controller
public class PublisherService {

    @Autowired
    PublisherDAO publisherDAO;

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
        
        return "1234"; // 예시 코드
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
    
    
}
