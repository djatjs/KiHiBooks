package kr.kh.kihibooks.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import kr.kh.kihibooks.dao.LibraryDAO;
import kr.kh.kihibooks.model.vo.BookVO;
import kr.kh.kihibooks.model.vo.EpisodeVO;
import kr.kh.kihibooks.service.BookService;

/**
 * 데이터 소유권 검증을 한곳에 모은 클래스.
 * 인터셉터(PublisherInterceptor), 메서드 보안(@PreAuthorize("@ss...")), 파일 응답이 모두 이 규칙을 쓴다.
 */
@Component("ss")
public class SecurityService {

    @Autowired
    private BookService bookService;

    @Autowired
    private LibraryDAO libraryDAO;

    /**
     * 출판사 계정이 이 도서를 다룰 수 있는가.
     * 같은 출판사 도서여야 하고, 운영자(SUPER)는 출판사 전체, 편집자는 자신이 담당하는 도서만 가능하다.
     */
    public boolean canAccessBook(CustomUser user, BookVO book) {
        if (user == null || book == null || user.getPu_code() == null
                || !user.getPu_code().equals(book.getBo_pu_code())) {
            return false;
        }
        return "SUPER".equals(user.getAuthority()) || book.getBo_pi_num() == user.getPi_num();
    }

    /**
     * 현재 로그인한 사용자가 이 도서를 다룰 수 있는가. (@PreAuthorize용)
     */
    public boolean canAccessBook(String bo_code) {
        CustomUser user = currentUser();
        if (user == null || bo_code == null) {
            return false;
        }
        if (isAdmin(user)) {
            return true;
        }
        return canAccessBook(user, bookService.getBook(bo_code));
    }

    /**
     * 현재 로그인한 사용자가 이 회차의 본문 파일을 읽을 수 있는가.
     * 관리자와 해당 도서의 출판사 담당자는 가능하고, 일반 사용자는 구매했거나 서재에 담은 회차만 가능하다.
     */
    public boolean canAccessEpisode(String ep_code) {
        CustomUser user = currentUser();
        if (user == null || ep_code == null) {
            return false;
        }
        EpisodeVO episode = bookService.getEpisodeByCode(ep_code);
        if (episode == null) {
            return false;
        }
        if (isAdmin(user) || canAccessBook(user, bookService.getBook(episode.getEp_bo_code()))) {
            return true;
        }
        // 유료 구매는 buy_list에만, 무료 열람은 buy_list와 library에 기록되므로 둘 다 확인한다
        int urNum = user.getUser().getUr_num();
        return bookService.selectBlList(ep_code, urNum) || libraryDAO.selectIsPurchased(urNum, ep_code) > 0;
    }

    private CustomUser currentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !(auth.getPrincipal() instanceof CustomUser)) {
            return null;
        }
        return (CustomUser) auth.getPrincipal();
    }

    private boolean isAdmin(CustomUser user) {
        return user.getAuthorities().stream().anyMatch(a -> "ROLE_ADMIN".equals(a.getAuthority()));
    }
}
