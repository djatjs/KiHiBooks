package kr.kh.kihibooks.dao;

import java.util.List;

import kr.kh.kihibooks.model.vo.EventVO;

public interface EventDAO {
	List<EventVO> selectOngoingEvents();
  	List<EventVO> selectPastEvents();
	
}
