package kr.kh.kihibooks.interceptor;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.HandlerMapping;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kr.kh.kihibooks.model.vo.BookVO;
import kr.kh.kihibooks.model.vo.EpisodeVO;
import kr.kh.kihibooks.service.BookService;
import kr.kh.kihibooks.utils.CustomUser;

@Component
public class PublisherInterceptor implements HandlerInterceptor {

    @Autowired
    private BookService bookService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        
        // 1. 로그인 정보 확인
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !(auth.getPrincipal() instanceof CustomUser)) {
            response.sendRedirect("/login");
            return false;
        }

        CustomUser user = (CustomUser) auth.getPrincipal();
        String uri = request.getRequestURI();
        
        // 2. 권한 레벨별 접근 제한 (Functional Auth)
        // /publisher/** 경로는 SUPER 권한만 접근 가능 (대시보드는 예외 가능하나 여기서는 엄격히 제한)
        if (uri.startsWith("/publisher") && !uri.equals("/publisher/dashboard")) {
            if (!"SUPER".equals(user.getAuthority())) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "관리자(대표) 권한이 필요합니다.");
                return false;
            }
        }

        // 3. 리소스별 소유권 확인 (Resource-based Auth)
        @SuppressWarnings("unchecked")
        Map<String, String> pathVariables = (Map<String, String>) request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
        
        if (pathVariables != null) {
            String bo_code = pathVariables.get("bo_code");
            String ep_code = pathVariables.get("ep_code");
            String pu_code_path = pathVariables.get("pu_code");

            // 출판사 코드 직접 대조 (URL에 포함된 경우)
            if (pu_code_path != null && !pu_code_path.equals(user.getPu_code())) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "타 출판사 데이터에 접근할 권한이 없습니다.");
                return false;
            }

            // 도서 코드 소유권 확인
            if (bo_code != null) {
                BookVO book = bookService.getBook(bo_code);
                if (book == null || !user.getPu_code().equals(book.getBo_pu_code())) {
                    response.sendError(HttpServletResponse.SC_FORBIDDEN, "해당 도서에 대한 권한이 없습니다.");
                    return false;
                }
                
                // EDITOR인 경우 본인 담당 도서인지 추가 확인 (선택 사항 - 필요시 활성화)
                /*
                if ("EDITOR".equals(user.getAuthority()) && book.getBo_pi_num() != user.getPi_num()) {
                    response.sendError(HttpServletResponse.SC_FORBIDDEN, "본인 담당 도서가 아닙니다.");
                    return false;
                }
                */
            }

            // 회차 코드 소유권 확인
            if (ep_code != null) {
                EpisodeVO episode = bookService.getEpisodeByCode(ep_code);
                if (episode != null) {
                    BookVO book = bookService.getBook(episode.getEp_bo_code());
                    if (book == null || !user.getPu_code().equals(book.getBo_pu_code())) {
                        response.sendError(HttpServletResponse.SC_FORBIDDEN, "해당 회차에 대한 권한이 없습니다.");
                        return false;
                    }
                }
            }
        }

        return true;
    }
}
