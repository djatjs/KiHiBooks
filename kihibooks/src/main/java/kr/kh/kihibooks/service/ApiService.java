package kr.kh.kihibooks.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.security.core.Authentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import jakarta.servlet.http.HttpSession;
import kr.kh.kihibooks.dao.UserDAO;
import kr.kh.kihibooks.model.vo.UserVO;

@Service
public class ApiService {
    @Value("${kakao.client.id}")
    private String kakaoClientId;

    @Value("${kakao.redirect.uri}")
    private String kakaoRedirectUri;

    @Autowired
	RestTemplate restTemplate; // HTTP 통신을 위한 RestTemplate 주입
    
    @Autowired
	UserDAO userDAO;

    //1. 인가 코드를 사용하여 카카오로부터 액세스 토큰을 받아옵니다.
    public String getKakaoAccessToken(String code) {
        String tokenRequestUrl = "https://kauth.kakao.com/oauth/token";
        
        // HTTP Body에 담을 파라미터 설정
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("client_id", kakaoClientId);
        params.add("redirect_uri", kakaoRedirectUri);
        params.add("code", code);

        // HTTP Header 설정
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        // HTTP Entity 생성
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);
        try {
            // RestTemplate를 사용하여 POST 요청 보내고 응답 받기
            ResponseEntity<Map> response = restTemplate.exchange(
                    tokenRequestUrl,
                    HttpMethod.POST,
                    request,
                    Map.class
            );

            // 응답에서 "access_token" 값 추출
            Map<String, Object> responseBody = response.getBody();
            if (responseBody != null && responseBody.containsKey("access_token")) {
                return (String) responseBody.get("access_token");
            } else {
                // 응답 본문이 없거나 access_token이 없는 경우
                System.err.println("카카오 토큰 응답 오류: " + response.getBody()); // 오류 로깅
                throw new RuntimeException("카카오 액세스 토큰 응답 형식 오류");
            }


        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("카카오 액세스 토큰 발급 실패", e);
        }
    }
    //2. 액세스 토큰을 사용하여 카카오로부터 사용자 정보를 받아옵니다.
    public Map<String, Object> getKakaoUserInfo(String accessToken) {
        String userInfoRequestUrl = "https://kapi.kakao.com/v2/user/me";

        // HTTP Header 설정 (Authorization: Bearer [accessToken])
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + accessToken);
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        // HTTP Entity 생성 (Header만 포함)
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(headers);

        try {
            // RestTemplate를 사용하여 POST 요청 보내고 응답 받기
            ResponseEntity<Map> response = restTemplate.exchange(
                    userInfoRequestUrl,
                    HttpMethod.POST, // 카카오 사용자 정보 요청은 POST 방식입니다.
                    request,
                    Map.class
            );

            // 응답 본문 반환 (사용자 정보 포함)
            Map<String, Object> userInfo = response.getBody();
            if (userInfo != null) {
                return userInfo;
            } else {
                System.err.println("카카오 사용자 정보 응답 오류: 응답 본문 없음");
                throw new RuntimeException("카카오 사용자 정보 응답 오류");
            }

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("카카오 사용자 정보 가져오기 실패", e);
        }
    }
    
    
    //3. 받아온 카카오 사용자 정보를 기반으로 DB에 저장하거나 로그인 처리하기
    public UserDetails processKakaoUser(Map<String, Object> userInfo) {
        // 카카오 API 응답에서 필요한 정보(이메일, 닉네임) 파싱
        String email = null;
        String nickname = null;
        String birthyear = null;
        String gender = null;

        Map<String, Object> kakaoAccount = (Map<String, Object>) userInfo.get("kakao_account");

        if (kakaoAccount != null) {
            Boolean hasEmail = (Boolean) kakaoAccount.get("has_email");
            Boolean emailNeedsAgreement = (Boolean) kakaoAccount.get("email_needs_agreement");

            if (Boolean.TRUE.equals(hasEmail) && Boolean.FALSE.equals(emailNeedsAgreement)) {
                email = (String) kakaoAccount.get("email");
            } else {
                System.out.println("카카오 이메일 정보 접근 권한 없음 또는 동의 필요");
            }

            Map<String, Object> profile = (Map<String, Object>) kakaoAccount.get("profile");
            if (profile != null) {
                nickname = (String) profile.get("nickname");
            }

            Boolean hasBirthyear = (Boolean) kakaoAccount.get("has_birthyear");
            Boolean birthyearNeedsAgreement = (Boolean) kakaoAccount.get("birthyear_needs_agreement");

            if (Boolean.TRUE.equals(hasBirthyear) && Boolean.FALSE.equals(birthyearNeedsAgreement)) {
                birthyear = (String) kakaoAccount.get("birthyear");
            } else {
                System.out.println("카카오 생년월일 정보 접근 권한 없음 또는 동의 필요");
            }

            Boolean hasGender = (Boolean) kakaoAccount.get("has_gender");
            Boolean genderNeedsAgreement = (Boolean) kakaoAccount.get("gender_needs_agreement");

            if (Boolean.TRUE.equals(hasGender) && Boolean.FALSE.equals(genderNeedsAgreement)) {
                gender = (String) kakaoAccount.get("gender");
            } else {
                System.out.println("카카오 성별 정보 접근 권한 없음 또는 동의 필요");
            }
        }

        if (email == null || nickname == null) {
            System.err.println("카카오 사용자 정보 파싱 실패: 필수 정보(이메일, 닉네임) 누락 또는 동의하지 않음");
            throw new RuntimeException("카카오 사용자 정보 파싱 오류 또는 필수 정보 동의 필요");
        }

        UserVO existingUser = userDAO.selectEmail(email);

        UserDetails userDetails;

        if (existingUser == null) {
            // 신규 사용자일 경우 -> 회원가입 처리
            System.out.println("신규 카카오 사용자 발견: " + email + ", 닉네임: " + nickname);

            UserVO newUser = new UserVO();
            newUser.setUr_email(email);
            newUser.setUr_nickname(nickname);
            newUser.setUr_year(birthyear);
            
            if (gender != null) {
                if (gender.equals("female")) {
                    gender = "F";
                } else if (gender.equals("male")) {
                    gender = "M";
                }
                newUser.setUr_gender(gender);
            }

            userDAO.insertUserWithoutPw(newUser);
            System.out.println("신규 사용자 DB 저장 완료: " + email);
            userDetails = convertUserVoToUserDetails(newUser);
        } else {
            // 기존 사용자일 경우 -> 로그인 처리
            System.out.println("기존 카카오 사용자 발견: " + email + ", 닉네임: " + existingUser.getUr_nickname());

            try {
                userDetails = convertUserVoToUserDetails(existingUser);

                Authentication authentication = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                );
                SecurityContextHolder.getContext().setAuthentication(authentication);

                System.out.println("Spring Security 로그인 처리 완료: " + email);
            } catch (Exception e) {
                System.err.println("Spring Security 로그인 처리 중 오류 발생: " + e.getMessage());
                e.printStackTrace();
                throw new RuntimeException("카카오 계정 로그인 처리 실패", e);
            }
        }

        return userDetails;
    }


    // ApiService 클래스 내부에 UserVO 객체를 Spring Security의 UserDetails로 변환하는 헬퍼 메소드 추가
    /**
     * UserVO 객체를 Spring Security에서 사용하는 UserDetails 객체로 변환합니다.
     * 이 메소드는 UserVO의 정보를 바탕으로 Spring Security가 사용자의 정보와 권한을 인식하도록 합니다.
     * @param userVO 변환할 사용자 정보 (UserVO 객체)
     * @return Spring Security의 UserDetails 인터페이스 구현체
     */
    
    private UserDetails convertUserVoToUserDetails(UserVO userVO) {
        // 1. 사용자 권한(Authority) 목록 생성
        List<GrantedAuthority> authorities = new ArrayList<>();
        // UserVO의 권한 필드(ur_authority) 값을 Spring Security의 GrantedAuthority 객체로 변환하여 추가합니다.
        // UserVO의 ur_authority는 enum('ADMIN','USER','PUBLISHER') 형태라고 하셨으므로,
        // 이를 Spring Security의 "ROLE_" 접두사가 붙은 권한 문자열로 변환하는 것이 일반적입니다.
        if (userVO.getUr_authority() != null) {
            // UserVO의 getUr_authority() 메소드가 String을 반환한다고 가정합니다.
            String authority = userVO.getUr_authority();
            // 권한 문자열 앞에 "ROLE_" 접두사 추가 (Spring Security 관례)
            authorities.add(new SimpleGrantedAuthority(authority.toUpperCase())); // 예: "USER" -> "ROLE_USER"
        } else {
             // 권한 정보가 없는 경우 기본 권한을 추가할 수 있습니다. (예: ROLE_ANONYMOUS 또는 기본 ROLE_USER)
             authorities.add(new SimpleGrantedAuthority("USER")); // 권한이 없다면 기본 USER 권한 부여
        }


        // 2. Spring Security에서 제공하는 기본 UserDetails 구현체인 User 객체 생성
        // 이 객체는 사용자명(Principal), 비밀번호(Credentials), 활성화 상태, 계정 만료/잠금 상태, 권한 목록 등을 인자로 받습니다.
        return new org.springframework.security.core.userdetails.User(
                userVO.getUr_email(),           // 사용자명 (Principal): 보통 로그인 ID로 사용. 여기서는 이메일 사용
                "",                    // 비밀번호 (Credentials): 카카오 로그인은 비밀번호 인증이 아니므로 빈 문자열 "" 또는 null
                                                // Spring Security의 User 클래스는 null을 허용하지만, 일반적으로 빈 문자열을 사용합니다.
                true,                   // 계정 활성화 여부 (enabled) - true로 설정
                true,         // 계정 만료 여부 (accountNonExpired) - true로 설정
                true,     // 자격 증명 만료 여부 (credentialsNonExpired) - true로 설정
                true,          // 계정 잠금 여부 (accountNonLocked) - true로 설정
                authorities                     // 사용자의 권한 목록 (위에서 생성한 List<GrantedAuthority>)
        );

        // 만약 UserVO에 계정 활성화/만료/잠금 등에 대한 필드가 있다면, 해당 필드 값을 사용하여 위 boolean 값들을 동적으로 설정할 수 있습니다.
        // 예: return new org.springframework.security.core.userdetails.User(userVO.getUr_email(), "", userVO.isEnabled(), ... , authorities);

        // 또는 프로젝트에 UserDetails를 구현한 커스텀 클래스가 있다면 해당 클래스의 객체를 생성하여 반환합니다.
        // 예: return new YourCustomUserDetails(userVO.getUr_email(), "", userVO.isEnabled(), ..., authorities, userVO);
    }


}
