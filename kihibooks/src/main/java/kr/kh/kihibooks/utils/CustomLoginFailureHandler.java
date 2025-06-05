package kr.kh.kihibooks.utils;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.security.core.AuthenticationException;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


@Component
public class CustomLoginFailureHandler extends SimpleUrlAuthenticationFailureHandler {

    private ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 401 Unauthorized
        response.setContentType("application/json;charset=UTF-8");

        Map<String, Object> data = new HashMap<>();

        // 1. BadCredentialsException (비밀번호 불일치)
        if (exception instanceof BadCredentialsException) {
            data.put("message", "잘못된 아이디 혹은 비밀번호입니다.");
        }
        // 2. UsernameNotFoundException (아이디 없음)
        else if (exception instanceof UsernameNotFoundException) {
            data.put("message", "해당하는 사용자가 존재하지 않습니다.");
        }
        // 3. InternalAuthenticationServiceException을 확인하고, 그 안에 DisabledException이 있는지 확인합니다.
        else if (exception instanceof InternalAuthenticationServiceException) {
            Throwable cause = exception.getCause(); // 내부 예외를 가져옵니다.
            if (cause instanceof DisabledException) {
                data.put("message", "탈퇴한 사용자입니다. 다른 계정으로 로그인 해주세요.");
                // data.put("action", "recover");
            }
            // InternalAuthenticationServiceException 내부에 다른 특정 예외가 있다면 여기서 추가 처리
            else {
                data.put("message", "로그인 처리 중 내부 오류가 발생했습니다.");
                System.err.println("Internal Authentication Service Exception with unexpected cause: " + cause.getClass().getName() + " - " + cause.getMessage());
            }
        }
        // 4. 직접 DisabledException이 넘어오는 경우 (이전 InternalAuthenticationServiceException 처리로 대부분 걸러지겠지만, 대비)
        else if (exception instanceof DisabledException) {
            data.put("message", "탈퇴한 사용자입니다. 다른 계정으로 로그인 해주세요.");
            // data.put("action", "recover");
        }
        // 5. 그 외 예상치 못한 예외
        else {
            data.put("message", "로그인에 실패했습니다.");
            System.err.println("Unexpected Login Failure Exception: " + exception.getClass().getName() + " - " + exception.getMessage());
        }

        response.getWriter().write(objectMapper.writeValueAsString(data));
    }
}