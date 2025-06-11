package kr.kh.kihibooks.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import kr.kh.kihibooks.model.vo.EventVO;
import kr.kh.kihibooks.service.EventService;

@Controller
public class EventController {

		@Autowired
    private EventService eventService;

    @GetMapping("/event")
    public String eventPage(@RequestParam(defaultValue = "ongoing") String tab, Model model) {
        List<EventVO> eventList;
        if ("ago".equals(tab)) {
            eventList = eventService.getPastEvents();
        } else {
            eventList = eventService.getOngoingEvents();
        }
        model.addAttribute("eventList", eventList);
        model.addAttribute("tab", tab);
        return "events/event";
    }
}

