package kr.kh.kihibooks.controller;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import kr.kh.kihibooks.model.vo.PublisherBookKpiVO;
import kr.kh.kihibooks.model.vo.PublisherDailyKpiVO;
import kr.kh.kihibooks.model.vo.PublisherEditorKpiVO;
import kr.kh.kihibooks.model.vo.PublisherKpiVO;
import kr.kh.kihibooks.model.vo.PublisherVO;
import kr.kh.kihibooks.model.vo.UserVO;
import kr.kh.kihibooks.service.BookService;
import kr.kh.kihibooks.service.KeywordService;
import kr.kh.kihibooks.service.PublisherService;
import kr.kh.kihibooks.service.UserService;
import kr.kh.kihibooks.utils.CustomUser;

@WebMvcTest(PublisherController.class)
class PublisherDashboardTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    BookService bookService;

    @MockBean
    UserService userService;

    @MockBean
    PublisherService publisherService;

    @MockBean
    KeywordService keywordService;

    @Test
    void rendersPublisherKpiDashboard() throws Exception {
        PublisherKpiVO kpi = new PublisherKpiVO();
        kpi.setSalesAmount(125000);
        kpi.setPaidPurchaseCount(1250);
        kpi.setBuyerCount(430);
        kpi.setReviewCount(38);
        kpi.setAverageRating(4.6);
        kpi.setTotalBookCount(24);
        kpi.setPublishingBookCount(17);
        kpi.setCompletedBookCount(7);
        kpi.setInactiveBookCount(2);
        kpi.setUnassignedBookCount(1);

        PublisherBookKpiVO book = new PublisherBookKpiVO();
        book.setBoCode("P00111001");
        book.setBoTitle("너를 기다리며");
        book.setEditorName("출판사1");
        book.setSalesAmount(52000);
        book.setPaidPurchaseCount(520);
        book.setBuyerCount(180);
        book.setAverageRating(4.8);
        book.setLastEpisodeDate(LocalDateTime.now());

        PublisherDailyKpiVO daily = new PublisherDailyKpiVO();
        daily.setSalesDate(LocalDate.now());
        daily.setSalesAmount(12000);
        daily.setPaidPurchaseCount(120);

        PublisherVO publisher = new PublisherVO();
        publisher.setPu_code("P001");
        publisher.setPu_name("은하출판사");

        when(publisherService.getKpiSummary(eq("P001"), any(), any(), any(), any())).thenReturn(kpi);
        when(publisherService.getTopBookKpis(eq("P001"), any(), any(), any(), anyInt())).thenReturn(List.of(book));
        when(publisherService.getDailyKpis(eq("P001"), any(), any(), any())).thenReturn(List.of(daily));
        when(publisherService.getPublisherByCode("P001")).thenReturn(publisher);
        when(publisherService.getEditorCount("P001")).thenReturn(3);
        PublisherEditorKpiVO editorKpi = new PublisherEditorKpiVO();
        editorKpi.setEditorName("에디터1");
        editorKpi.setSalesAmount(52000);
        when(publisherService.getEditorKpis(eq("P001"), any(), any(), any())).thenReturn(List.of(editorKpi));

        UserVO userVo = new UserVO();
        userVo.setUr_email("publisher@example.com");
        userVo.setUr_pw("{noop}password");
        userVo.setUr_authority("PUBLISHER");
        userVo.setUr_del("N");
        CustomUser customUser = new CustomUser(userVo, "SUPER", "P001", 1);

        mockMvc.perform(get("/publisher/dashboard?days=30").with(user(customUser)))
                .andExpect(status().isOk())
                .andExpect(view().name("/publisher/publisherDashboard"))
                .andExpect(model().attribute("publisherName", "은하출판사"))
                .andExpect(model().attribute("isSuper", true))
                .andExpect(model().attribute("selectedDays", 30))
                .andExpect(content().string(containsString("은하출판사")))
                .andExpect(content().string(containsString("에디터별 담당 운영 현황")))
                .andExpect(content().string(containsString("출판사 작품별 성과 TOP 5")));
    }

    @Test
    void editorDashboardOnlyShowsAssignedContentScope() throws Exception {
        PublisherKpiVO kpi = new PublisherKpiVO();
        PublisherVO publisher = new PublisherVO();
        publisher.setPu_code("P001");
        publisher.setPu_name("은하출판사");

        when(publisherService.getKpiSummary(eq("P001"), eq(11), any(), any(), any())).thenReturn(kpi);
        when(publisherService.getTopBookKpis(eq("P001"), eq(11), any(), any(), anyInt())).thenReturn(List.of());
        when(publisherService.getDailyKpis(eq("P001"), eq(11), any(), any())).thenReturn(List.of());
        when(publisherService.getPublisherByCode("P001")).thenReturn(publisher);

        UserVO userVo = new UserVO();
        userVo.setUr_email("edit1@edit.com");
        userVo.setUr_pw("{noop}password");
        userVo.setUr_authority("PUBLISHER");
        userVo.setUr_del("N");
        CustomUser customUser = new CustomUser(userVo, "EDITOR", "P001", 11);

        mockMvc.perform(get("/publisher/dashboard?days=30").with(user(customUser)))
                .andExpect(status().isOk())
                .andExpect(model().attribute("isSuper", false))
                .andExpect(model().attribute("scopeLabel", "내 담당 작품"))
                .andExpect(content().string(containsString("에디터 대시보드")))
                .andExpect(content().string(containsString("다른 에디터의 매출·독자·평가 정보는 제공하지 않습니다")))
                .andExpect(content().string(not(containsString("에디터별 담당 운영 현황"))));
    }
}
