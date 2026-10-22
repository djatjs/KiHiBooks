package kr.kh.kihibooks.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import kr.kh.kihibooks.dao.PublisherDAO;
import kr.kh.kihibooks.dao.UserDAO;
import kr.kh.kihibooks.model.vo.PublisherIdVO;

/** 출판사 대표는 자기 출판사의 에디터만 등록·해제할 수 있다. */
class PublisherServiceEditorTest {

    private PublisherService publisherService;
    private PublisherDAO publisherDAO;
    private UserDAO userDAO;

    @BeforeEach
    void setUp() {
        publisherService = new PublisherService();
        publisherDAO = mock(PublisherDAO.class);
        userDAO = mock(UserDAO.class);
        ReflectionTestUtils.setField(publisherService, "publisherDAO", publisherDAO);
        ReflectionTestUtils.setField(publisherService, "userDAO", userDAO);
        when(publisherDAO.deleteEditorByUserNum(anyInt())).thenReturn(true);
        when(userDAO.updateAuthorityToUser(anyInt())).thenReturn(true);
        when(userDAO.updateAthourityToPublisher(anyInt())).thenReturn(true);
        when(publisherDAO.insertEditor(anyInt(), anyString())).thenReturn(true);
    }

    @Test
    void canRemoveOwnEditor() {
        when(publisherDAO.selectPublisherIdByNum(7)).thenReturn(member("P001"));

        assertThat(publisherService.deleteEditor(7, "P001")).isTrue();
        verify(publisherDAO).deleteEditorByUserNum(7);
    }

    @Test
    void cannotRemoveOtherPublishersEditor() {
        when(publisherDAO.selectPublisherIdByNum(7)).thenReturn(member("P002"));

        assertThatThrownBy(() -> publisherService.deleteEditor(7, "P001"))
                .isInstanceOf(IllegalArgumentException.class);
        verify(publisherDAO, never()).deleteEditorByUserNum(anyInt());
        verify(userDAO, never()).updateAuthorityToUser(anyInt());
    }

    @Test
    void cannotRemoveUserWithoutPublisher() {
        when(publisherDAO.selectPublisherIdByNum(7)).thenReturn(null);

        assertThatThrownBy(() -> publisherService.deleteEditor(7, "P001"))
                .isInstanceOf(IllegalArgumentException.class);
        verify(publisherDAO, never()).deleteEditorByUserNum(anyInt());
    }

    @Test
    void canAddUserWithoutPublisher() {
        when(publisherDAO.selectPublisherIdByNum(7)).thenReturn(null);

        assertThat(publisherService.addEditor(7, "P001")).isTrue();
        verify(publisherDAO).insertEditor(7, "P001");
    }

    @Test
    void cannotTakeEditorFromAnotherPublisher() {
        when(publisherDAO.selectPublisherIdByNum(7)).thenReturn(member("P002"));

        assertThatThrownBy(() -> publisherService.addEditor(7, "P001"))
                .isInstanceOf(IllegalArgumentException.class);
        verify(userDAO, never()).updateAthourityToPublisher(anyInt());
        verify(publisherDAO, never()).insertEditor(anyInt(), anyString());
    }

    private PublisherIdVO member(String puCode) {
        PublisherIdVO vo = new PublisherIdVO();
        vo.setPi_pu_code(puCode);
        return vo;
    }
}
