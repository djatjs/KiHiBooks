package kr.kh.kihibooks.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import kr.kh.kihibooks.model.vo.BookVO;
import kr.kh.kihibooks.service.BookService;

@Component("ss")
public class SecurityService {

    @Autowired
    private BookService bookService;

    /**
     * 도서 접근 권한 확인
     * 1. ADMIN: 모든 도서 접근 가능
     * 2. SUPER: 자신이 속한 출판사의 모든 도서 접근 가능
     * 3. EDITOR: 자신이 담당한 도서만 접근 가능
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
}
