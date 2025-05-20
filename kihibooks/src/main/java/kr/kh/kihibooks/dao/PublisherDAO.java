package kr.kh.kihibooks.dao;

import java.util.List;

import kr.kh.kihibooks.model.vo.PublisherIdVO;
import kr.kh.kihibooks.model.vo.PublisherVO;

public interface PublisherDAO {

    PublisherVO selectPublisher(String pu_name);
    
    boolean insertPublisher(String pu_name, String pu_code);

    List<PublisherVO> selectAllPublishers();

    PublisherIdVO selectPublisherId(PublisherIdVO publisherId);
    
    boolean insertPublisherId(PublisherIdVO publisherId);
    
}
