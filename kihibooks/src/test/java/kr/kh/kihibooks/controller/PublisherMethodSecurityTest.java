package kr.kh.kihibooks.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.ExtendedModelMap;

import kr.kh.kihibooks.dao.LibraryDAO;
import kr.kh.kihibooks.model.vo.BookVO;
import kr.kh.kihibooks.model.vo.UserVO;
import kr.kh.kihibooks.service.BookService;
import kr.kh.kihibooks.service.KeywordService;
import kr.kh.kihibooks.service.PublisherService;
import kr.kh.kihibooks.service.UserService;
import kr.kh.kihibooks.utils.CustomUser;
import kr.kh.kihibooks.utils.SecurityService;

/**
 * 인터셉터를 거치지 않고 컨트롤러 메서드를 직접 호출해,
 * 메서드에 붙은 @PreAuthorize("@ss.canAccessBook(#bo_code)")가 그 자체로 막는지 확인한다.
 */
@WebMvcTest(PublisherController.class)
@Import({ PublisherMethodSecurityTest.MethodSecurityConfig.class, SecurityService.class })
class PublisherMethodSecurityTest {

    @TestConfiguration
    @EnableMethodSecurity
    static class MethodSecurityConfig {
    }

    @Autowired
    PublisherController controller;

    @MockBean
    BookService bookService;

    @MockBean
    UserService userService;

    @MockBean
    PublisherService publisherService;

    @MockBean
    KeywordService keywordService;

    @MockBean
    LibraryDAO libraryDAO;

    @BeforeEach
    void setUp() {
        BookVO book = new BookVO();
        book.setBo_pu_code("P001");
        book.setBo_pi_num(11);
        when(bookService.getBook("BOOK-1")).thenReturn(book);
    }

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void assignedEditorCanCallMethod() {
        login("EDITOR", "P001", 11);

        assertThat(controller.manageEpisodeEpisode("BOOK-1", new ExtendedModelMap()))
                .isEqualTo("/publisher/editor_manageEpisode");
    }

    @Test
    void editorNotAssignedToBookIsDeniedByMethodSecurity() {
        login("EDITOR", "P001", 22);

        assertThatThrownBy(() -> controller.manageEpisodeEpisode("BOOK-1", new ExtendedModelMap()))
                .isInstanceOf(AccessDeniedException.class);
    }

    @Test
    void superOfAnotherPublisherIsDeniedByMethodSecurity() {
        login("SUPER", "P002", 1);

        assertThatThrownBy(() -> controller.manageEpisodeEpisode("BOOK-1", new ExtendedModelMap()))
                .isInstanceOf(AccessDeniedException.class);
    }

    private void login(String publisherRole, String publisherCode, int editorNumber) {
        UserVO user = new UserVO();
        user.setUr_email("publisher@example.com");
        user.setUr_pw("password");
        user.setUr_authority("PUBLISHER");
        CustomUser principal = new CustomUser(user, publisherRole, publisherCode, editorNumber);
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(principal, principal.getPassword(), principal.getAuthorities()));
    }
}
