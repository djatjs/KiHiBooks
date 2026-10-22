package kr.kh.kihibooks.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.util.ReflectionTestUtils;

import kr.kh.kihibooks.utils.SecurityService;

/** 본문(epub) 파일은 폴더 이름을 바꿔 불러도 권한 검사를 건너뛰지 못한다. */
class FileControllerTest {

    private FileController controller;
    private SecurityService securityService;

    @BeforeEach
    void setUp() {
        controller = new FileController();
        securityService = mock(SecurityService.class);
        ReflectionTestUtils.setField(controller, "securityService", securityService);
        ReflectionTestUtils.setField(controller, "uploadPath", System.getProperty("java.io.tmpdir"));
    }

    @Test
    void epubWithoutPurchaseIsForbidden() {
        when(securityService.canAccessEpisode("EP1")).thenReturn(false);

        assertThat(controller.serveFile("B1", "epubs", "EP1.epub", new MockHttpServletRequest()).getStatusCode())
                .isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    void otherSpellingsOfTheEpubFolderAreRejected() {
        for (String type : new String[] { "EPUBS", "Epubs", "epubs.", "epubs " }) {
            assertThat(controller.serveFile("B1", type, "EP1.epub", new MockHttpServletRequest()).getStatusCode())
                    .as(type)
                    .isEqualTo(HttpStatus.NOT_FOUND);
        }
        verify(securityService, never()).canAccessEpisode("EP1");
    }
}
