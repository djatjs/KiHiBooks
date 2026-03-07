package kr.kh.kihibooks.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import kr.kh.kihibooks.model.vo.BookVO;
import kr.kh.kihibooks.model.vo.EpisodeVO;
import kr.kh.kihibooks.service.BookService;
import kr.kh.kihibooks.dao.LibraryDAO;

@Component("ss")
public class SecurityService {

    @Autowired
    private BookService bookService;

    @Autowired
    private LibraryDAO libraryDAO;

    /**
     * 도서 접근 권한 확인 (출판사/관리자용)
     */
    public boolean canAccessBook(String bo_code) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !(auth.getPrincipal() instanceof CustomUser)) {
            return false;
        }

        CustomUser user = (CustomUser) auth.getPrincipal();
        
        // 1. 관리자면 통과
        if (user.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
            return true;
        }

        BookVO book = bookService.getBook(bo_code);
        if (book == null) {
            return false;
        }

        // 2. 출판사 관리자(SUPER)인 경우: 출판사 코드가 일치하는지 확인
        if (user.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_SUPER"))) {
            return book.getBo_pu_code() != null && book.getBo_pu_code().equals(user.getPu_code());
        }

        // 3. 일반 에디터(EDITOR)인 경우: 담당자 번호(pi_num)가 일치하는지 확인
        if (user.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_EDITOR"))) {
            return book.getBo_pi_num() == user.getPi_num();
        }

        return false;
    }

    /**
     * 특정 회차 파일 접근 권한 확인
     * 1. 관리자/출판사 관계자: canAccessBook 로직 활용
     * 2. 일반 사용자: 해당 회차 구매 여부 확인
     */
    public boolean canAccessEpisode(String ep_code) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !(auth.getPrincipal() instanceof CustomUser)) {
            return false;
        }

        CustomUser user = (CustomUser) auth.getPrincipal();
        EpisodeVO episode = bookService.getEpisodeByCode(ep_code);
        if (episode == null) {
            return false;
        }

        // 관리자/출판사 관계자 권한 체크
        if (canAccessBook(episode.getEp_bo_code())) {
            return true;
        }

        // 일반 사용자: 구매 여부 확인
        return libraryDAO.selectIsPurchased(user.getUser().getUr_num(), ep_code) > 0;
    }
}
