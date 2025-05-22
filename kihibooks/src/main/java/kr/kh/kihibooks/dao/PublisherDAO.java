package kr.kh.kihibooks.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import kr.kh.kihibooks.model.vo.PublisherIdVO;
import kr.kh.kihibooks.model.vo.PublisherVO;

public interface PublisherDAO {

    PublisherVO selectPublisher(String pu_name);
    
    boolean insertPublisher(String pu_name, String pu_code);

    List<PublisherVO> selectAllPublishers();

    PublisherIdVO selectPublisherId(PublisherIdVO publisherId);
    
    boolean insertPublisherId(PublisherIdVO publisherId);

    PublisherIdVO selectPublisherIdByNum(int ur_num);

    boolean insertEditor(@Param("userNum")int userNum, @Param("puCode")String puCode);

    String getLatestPuCode();
    
}
