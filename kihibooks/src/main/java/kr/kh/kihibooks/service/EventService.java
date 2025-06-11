package kr.kh.kihibooks.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import kr.kh.kihibooks.dao.EventDAO;
import kr.kh.kihibooks.model.vo.EventVO;

@Service
public class EventService {
	@Autowired
	private EventDAO eventDAO;

	public List<EventVO> getOngoingEvents() {
        return eventDAO.selectOngoingEvents();
    }

    public List<EventVO> getPastEvents() {
        return eventDAO.selectPastEvents();
    }
}
