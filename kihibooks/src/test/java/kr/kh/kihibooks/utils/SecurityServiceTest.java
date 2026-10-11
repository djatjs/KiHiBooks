package kr.kh.kihibooks.utils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.util.ReflectionTestUtils;

import kr.kh.kihibooks.dao.LibraryDAO;
import kr.kh.kihibooks.model.vo.BookVO;
import kr.kh.kihibooks.model.vo.EpisodeVO;
import kr.kh.kihibooks.model.vo.UserVO;
import kr.kh.kihibooks.service.BookService;

class SecurityServiceTest {

    private SecurityService securityService;
    private BookService bookService;
    private LibraryDAO libraryDAO;

    @BeforeEach
    void setUp() {
        securityService = new SecurityService();
        bookService = mock(BookService.class);
        libraryDAO = mock(LibraryDAO.class);
        ReflectionTestUtils.setField(securityService, "bookService", bookService);
        ReflectionTestUtils.setField(securityService, "libraryDAO", libraryDAO);

        EpisodeVO episode = new EpisodeVO();
        episode.setEp_bo_code("BOOK-1");
        when(bookService.getEpisodeByCode("EP-1")).thenReturn(episode);
        when(bookService.getBook("BOOK-1")).thenReturn(book("P001", 11));
    }

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    // ── 도서 접근 (출판사)

    @Test
    void anonymousCannotAccessBook() {
        assertThat(securityService.canAccessBook("BOOK-1")).isFalse();
    }

    @Test
    void assignedEditorCanAccessBook() {
        login("PUBLISHER", "EDITOR", "P001", 11, 100);
        assertThat(securityService.canAccessBook("BOOK-1")).isTrue();
    }

    @Test
    void editorOfSamePublisherButNotAssignedCannotAccessBook() {
        login("PUBLISHER", "EDITOR", "P001", 22, 101);
        assertThat(securityService.canAccessBook("BOOK-1")).isFalse();
    }

    @Test
    void superOfAnotherPublisherCannotAccessBook() {
        login("PUBLISHER", "SUPER", "P002", 1, 102);
        assertThat(securityService.canAccessBook("BOOK-1")).isFalse();
    }

    @Test
    void unknownBookIsDenied() {
        login("PUBLISHER", "SUPER", "P001", 1, 103);
        assertThat(securityService.canAccessBook("NO-SUCH-BOOK")).isFalse();
    }

    // ── 회차 본문 접근 (독자)

    @Test
    void readerWithoutPurchaseCannotReadEpisode() {
        login("USER", null, null, 0, 200);
        assertThat(securityService.canAccessEpisode("EP-1")).isFalse();
    }

    @Test
    void readerWhoPaidCanReadEpisode() {
        login("USER", null, null, 0, 200);
        when(bookService.selectBlList("EP-1", 200)).thenReturn(true);   // 유료 구매는 buy_list에만 기록된다
        assertThat(securityService.canAccessEpisode("EP-1")).isTrue();
    }

    @Test
    void readerWithEpisodeInLibraryCanReadEpisode() {
        login("USER", null, null, 0, 200);
        when(libraryDAO.selectIsPurchased(200, "EP-1")).thenReturn(1);
        assertThat(securityService.canAccessEpisode("EP-1")).isTrue();
    }

    @Test
    void assignedEditorCanReadOwnEpisodeWithoutPurchase() {
        login("PUBLISHER", "EDITOR", "P001", 11, 100);
        assertThat(securityService.canAccessEpisode("EP-1")).isTrue();
    }

    @Test
    void adminCanReadAnyEpisode() {
        login("ADMIN", null, null, 0, 1);
        assertThat(securityService.canAccessEpisode("EP-1")).isTrue();
    }

    @Test
    void anonymousCannotReadEpisode() {
        assertThat(securityService.canAccessEpisode("EP-1")).isFalse();
    }

    private void login(String role, String publisherRole, String publisherCode, int editorNumber, int userNumber) {
        UserVO user = new UserVO();
        user.setUr_num(userNumber);
        user.setUr_email("user" + userNumber + "@example.com");
        user.setUr_pw("password");
        user.setUr_authority(role);
        CustomUser principal = new CustomUser(user, publisherRole, publisherCode, editorNumber);
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(principal, principal.getPassword(), principal.getAuthorities()));
    }

    private BookVO book(String publisherCode, int editorNumber) {
        BookVO book = new BookVO();
        book.setBo_pu_code(publisherCode);
        book.setBo_pi_num(editorNumber);
        return book;
    }
}
