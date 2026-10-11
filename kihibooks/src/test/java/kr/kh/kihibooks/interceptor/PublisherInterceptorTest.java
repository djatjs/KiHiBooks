package kr.kh.kihibooks.interceptor;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Map;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.servlet.HandlerMapping;

import kr.kh.kihibooks.model.vo.BookVO;
import kr.kh.kihibooks.model.vo.EpisodeVO;
import kr.kh.kihibooks.model.vo.NoticeVO;
import kr.kh.kihibooks.model.vo.UserVO;
import kr.kh.kihibooks.service.BookService;
import kr.kh.kihibooks.utils.CustomUser;

class PublisherInterceptorTest {

    private PublisherInterceptor interceptor;
    private BookService bookService;

    @BeforeEach
    void setUp() {
        interceptor = new PublisherInterceptor();
        bookService = mock(BookService.class);
        ReflectionTestUtils.setField(interceptor, "bookService", bookService);
        ReflectionTestUtils.setField(interceptor, "securityService", new kr.kh.kihibooks.utils.SecurityService());
    }

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void superCanAccessEveryBookOwnedByPublisher() throws Exception {
        authenticate("SUPER", "P001", 1);
        when(bookService.getBook("BOOK-2")).thenReturn(book("P001", 22));

        MockHttpServletResponse response = new MockHttpServletResponse();
        assertThat(interceptor.preHandle(request("/editor/manageEpisode/BOOK-2", Map.of("bo_code", "BOOK-2")), response, new Object()))
                .isTrue();
        assertThat(response.getStatus()).isEqualTo(200);
    }

    @Test
    void superCannotAccessBookOfAnotherPublisher() throws Exception {
        authenticate("SUPER", "P001", 1);
        when(bookService.getBook("BOOK-9")).thenReturn(book("P002", 1));

        MockHttpServletResponse response = new MockHttpServletResponse();
        assertThat(interceptor.preHandle(request("/editor/manageEpisode/BOOK-9", Map.of("bo_code", "BOOK-9")), response, new Object()))
                .isFalse();
        assertThat(response.getStatus()).isEqualTo(403);
    }

    @Test
    void editorCannotAccessBookOfAnotherPublisherEvenWithSameEditorNumber() throws Exception {
        authenticate("EDITOR", "P001", 11);
        when(bookService.getBook("BOOK-9")).thenReturn(book("P002", 11));

        MockHttpServletResponse response = new MockHttpServletResponse();
        assertThat(interceptor.preHandle(request("/editor/manageEpisode/BOOK-9", Map.of("bo_code", "BOOK-9")), response, new Object()))
                .isFalse();
        assertThat(response.getStatus()).isEqualTo(403);
    }

    @Test
    void editorCannotAccessBookAssignedToAnotherEditor() throws Exception {
        authenticate("EDITOR", "P001", 11);
        when(bookService.getBook("BOOK-2")).thenReturn(book("P001", 22));

        MockHttpServletResponse response = new MockHttpServletResponse();
        assertThat(interceptor.preHandle(request("/editor/manageEpisode/BOOK-2", Map.of("bo_code", "BOOK-2")), response, new Object()))
                .isFalse();
        assertThat(response.getStatus()).isEqualTo(403);
    }

    @Test
    void editorCanManageEpisodeOfAssignedBook() throws Exception {
        authenticate("EDITOR", "P001", 11);
        EpisodeVO episode = new EpisodeVO();
        episode.setEp_bo_code("BOOK-1");
        when(bookService.getEpisodeByCode("EP-1")).thenReturn(episode);
        when(bookService.getBook("BOOK-1")).thenReturn(book("P001", 11));

        MockHttpServletResponse response = new MockHttpServletResponse();
        assertThat(interceptor.preHandle(request("/editor/updateEpisode/EP-1", Map.of("ep_code", "EP-1")), response, new Object()))
                .isTrue();
    }

    @Test
    void editorCannotChangeCompletionStateUsingAnotherEditorsBookParameter() throws Exception {
        authenticate("EDITOR", "P001", 11);
        when(bookService.getBook("BOOK-2")).thenReturn(book("P001", 22));
        MockHttpServletRequest request = request("/editor/bookFinToY", Map.of());
        request.addParameter("bo_code", "BOOK-2");

        MockHttpServletResponse response = new MockHttpServletResponse();
        assertThat(interceptor.preHandle(request, response, new Object())).isFalse();
        assertThat(response.getStatus()).isEqualTo(403);
    }

    @Test
    void editorCannotManageNoticeForAnotherEditorsBook() throws Exception {
        authenticate("EDITOR", "P001", 11);
        NoticeVO notice = new NoticeVO();
        notice.setNt_bo_code("BOOK-2");
        when(bookService.getNotice(7)).thenReturn(notice);
        when(bookService.getBook("BOOK-2")).thenReturn(book("P001", 22));
        MockHttpServletRequest request = request("/editor/deleteNotice", Map.of());
        request.addParameter("nt_num", "7");

        MockHttpServletResponse response = new MockHttpServletResponse();
        assertThat(interceptor.preHandle(request, response, new Object())).isFalse();
        assertThat(response.getStatus()).isEqualTo(403);
    }

    @Test
    void editorStillHasAccessToOwnDashboard() throws Exception {
        authenticate("EDITOR", "P001", 11);
        MockHttpServletResponse response = new MockHttpServletResponse();

        assertThat(interceptor.preHandle(request("/publisher/dashboard", Map.of()), response, new Object())).isTrue();
    }

    @Test
    void editorCannotOpenPublisherEditorManagement() throws Exception {
        authenticate("EDITOR", "P001", 11);
        MockHttpServletResponse response = new MockHttpServletResponse();

        assertThat(interceptor.preHandle(request("/publisher/editors", Map.of()), response, new Object())).isFalse();
        assertThat(response.getStatus()).isEqualTo(403);
    }

    private void authenticate(String authority, String publisherCode, int publisherId) {
        UserVO user = new UserVO();
        user.setUr_email("publisher@example.com");
        user.setUr_pw("password");
        user.setUr_authority("PUBLISHER");
        CustomUser principal = new CustomUser(user, authority, publisherCode, publisherId);
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(principal, principal.getPassword(), principal.getAuthorities()));
    }

    private MockHttpServletRequest request(String uri, Map<String, String> pathVariables) {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", uri);
        request.setAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE, pathVariables);
        return request;
    }

    private BookVO book(String publisherCode, int publisherId) {
        BookVO book = new BookVO();
        book.setBo_pu_code(publisherCode);
        book.setBo_pi_num(publisherId);
        return book;
    }
}
