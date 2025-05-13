package kr.kh.kihibooks.service;

import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

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
                // 응답 본문이 없는 경우
                System.err.println("카카오 사용자 정보 응답 오류: 응답 본문 없음"); // 오류 로깅
                throw new RuntimeException("카카오 사용자 정보 응답 오류");
            }

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("카카오 사용자 정보 가져오기 실패", e);
        }
    }
    
    
    //3. 받아온 카카오 사용자 정보를 기반으로 DB에 저장하거나 로그인 처리를 합니다.
    //UserDAO를 사용하여 DB 작업을 수행합니다.
    public void processKakaoUser(Map<String, Object> userInfo) {
        // 카카오 API 응답에서 필요한 정보(이메일, 닉네임) 파싱
        // userInfo 맵 구조를 기반으로 안전하게 정보를 추출합니다.
        String email = null;
        String nickname = null;
        String birthyear = null; // 생년월일도 받아왔으니 파싱 가능

        // 카카오 API 응답 예시 구조:
        // {
        //   "id": 12345,
        //   "connected_at": "...",
        //   "properties": { "nickname": "..." },
        //   "kakao_account": {
        //     "profile_nickname_needs_agreement": false,
        //     "profile": { "nickname": "...", "is_default_nickname": false },
        //     "has_email": true,
        //     "email_needs_agreement": false,
        //     "is_email_valid": true,
        //     "is_email_verified": true,
        //     "email": "...", // 이메일
        //     "has_birthyear": true,
        //     "birthyear_needs_agreement": false,
        //     "birthyear": "..." // 생년월일
        //     // 기타 정보 (성별, 연령대 등)는 동의 설정 및 추가 API 호출 필요
        //   }
        // }

        // 'kakao_account' 맵 가져오기
        Map<String, Object> kakaoAccount = (Map<String, Object>) userInfo.get("kakao_account");

        if (kakaoAccount != null) {
            // 이메일 가져오기 (has_email이 true이고 email_needs_agreement가 false일 경우)
            Boolean hasEmail = (Boolean) kakaoAccount.get("has_email");
            Boolean emailNeedsAgreement = (Boolean) kakaoAccount.get("email_needs_agreement");

            if (Boolean.TRUE.equals(hasEmail) && Boolean.FALSE.equals(emailNeedsAgreement)) {
                email = (String) kakaoAccount.get("email");
            } else {
                System.out.println("카카오 이메일 정보 접근 권한 없음 또는 동의 필요");
                // 이메일 필수 항목인데 동의하지 않았다면 회원가입 진행 불가 로직 추가
            }

            // 'profile' 맵 가져오기 (kakao_account 안에 있음)
            Map<String, Object> profile = (Map<String, Object>) kakaoAccount.get("profile");
            if (profile != null) {
                // 닉네임 가져오기
                nickname = (String) profile.get("nickname");
            }

            // 생년월일 가져오기 (has_birthyear가 true이고 birthyear_needs_agreement가 false일 경우)
            Boolean hasBirthyear = (Boolean) kakaoAccount.get("has_birthyear");
            Boolean birthyearNeedsAgreement = (Boolean) kakaoAccount.get("birthyear_needs_agreement");

            if (Boolean.TRUE.equals(hasBirthyear) && Boolean.FALSE.equals(birthyearNeedsAgreement)) {
                birthyear = (String) kakaoAccount.get("birthyear");
            } else {
                System.out.println("카카오 생년월일 정보 접근 권한 없음 또는 동의 필요");
            }

            // 성별 정보 등 다른 정보도 kakao_account에 있을 수 있습니다.
            // 필요하다면 has_gender, gender_needs_agreement 등을 확인하고 gender 값을 파싱할 수 있습니다.
            // String gender = (String) kakaoAccount.get("gender"); // 예시

        }

        // 필수 정보(이메일, 닉네임)가 없는 경우 처리
        // 이메일은 로그인/회원가입의 핵심 식별자이므로 필수로 확인합니다.
        if (email == null || nickname == null) {
            System.err.println("카카오 사용자 정보 파싱 실패: 필수 정보(이메일, 닉네임) 누락 또는 동의하지 않음");
            // 사용자가 필수 정보 제공에 동의하지 않았거나 정보가 없는 경우
            // 회원가입 페이지에 오류 메시지를 보여주거나 특정 오류 페이지로 리다이렉트하는 등의 처리가 필요합니다.
            throw new RuntimeException("카카오 사용자 정보 파싱 오류 또는 필수 정보 동의 필요"); // 예외 발생
            // 또는 return "redirect:/signup?error=info_required"; // 오류 메시지와 함께 회원가입 페이지로
        }

        // 이메일로 기존 사용자 검색 (UserDAO의 findByUrEmail 메소드 사용)
        // UserDAO에 findByUrEmail 메소드가 구현되어 있어야 합니다.
        UserVO existingUser = userDAO.selectEmail(email);

        if (existingUser == null) {
            // ========================================
            // 신규 사용자일 경우 -> 회원가입 처리
            // ========================================
            System.out.println("신규 카카오 사용자 발견: " + email + ", 닉네임: " + nickname);

            UserVO newUser = new UserVO();
            newUser.setUr_email(email);
            newUser.setUr_nickname(nickname);

            // UR_PW 처리: 카카오 로그인은 비밀번호를 사용하지 않지만, DB 스키마에 UR_PW가 NOT NULL이라면 임의의 안전한 값(UUID) 설정
            // UR_PW 컬럼이 NOT NULL 제약조건이 있는지 확인하시고 필요에 따라 추가하세요.
            // import java.util.UUID; 필요
            //newUser.setUr_pw(UUID.randomUUID().toString()); // 임의의 UUID 생성

            // UR_GENDER, UR_YEAR 설정: 카카오 API 응답에서 파싱한 값이 있다면 설정
            // enum('M','F')
            // newUser.setUrGender(파싱한 성별 값); // 필요에 따라 구현
            if (birthyear != null) {
                newUser.setUr_year(birthyear);
            } else {
                newUser.setUr_year(null); // 생년월일 정보가 없다면 null
            }


            // UserDAO의 save 또는 insert 메소드를 사용하여 DB에 저장
            // UserDAO에 save(User user) 메소드가 구현되어 있어야 합니다.
            userDAO.insertUserWithoutPw(newUser); // 메소드 이름은 실제 UserDAO 구현에 맞게 수정하세요.
            System.out.println("신규 사용자 DB 저장 완료: " + email);

            // TODO: 회원가입 성공 후 추가 로직
            // 예: 초기 포인트 지급, 환영 알림 발송, 회원가입 완료 페이지로 리다이렉트 등
            // 이 메소드는 void를 반환하므로, 컨트롤러에서 리다이렉트 경로를 결정합니다.
            // 컨트롤러에서 신규 회원가입임을 구분하여 리다이렉트할 수도 있습니다.

        } else {
            // ========================================
            // 이미 존재하는 사용자일 경우 -> 로그인 처리
            // ========================================
            System.out.println("기존 카카오 사용자 발견: " + email + ", 닉네임: " + existingUser.getUr_nickname());

            // TODO: 여기에서 기존 사용자로 로그인 처리 로직 구현
            // 실제 로그인 처리: Spring Security와 연동하여 Authentication 객체 생성 및 SecurityContextHolder에 설정
            // 세션 생성 또는 JWT 토큰 발행 등의 방법이 있습니다.
            // 이 부분은 애플리케이션의 인증/권한 관리 전략에 따라 복잡해질 수 있습니다.
            // 간단히는 해당 User 객체를 사용하여 세션에 로그인 정보(예: user.getUrNum(), user.getUrEmail())를 저장하는 방식도 가능합니다.

            // 예시: 간단하게 사용자 번호와 이메일을 세션에 저장하는 방식 (세션 관리가 별도로 필요합니다)
            // HttpSession session = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest().getSession();
            // session.setAttribute("loggedInUserNum", existingUser.getUrNum());
            // session.setAttribute("loggedInUserEmail", existingUser.getUrEmail());
            // System.out.println("세션에 사용자 정보 저장: " + existingUser.getUrEmail());


            // 필요에 따라 사용자 정보 업데이트 (예: 카카오에서 닉네임을 변경했을 경우 업데이트)
            // UserDAO에 update(User user) 메소드가 있다면 호출
            if (!existingUser.getUr_nickname().equals(nickname)) {
                existingUser.setUr_nickname(nickname);
                //userDAO.update(existingUser); // 메소드 이름은 실제 UserDAO 구현에 맞게 수정하세요.
                System.out.println("기존 사용자 닉네임 업데이트: " + email + " -> " + nickname);
            } else {
                System.out.println("기존 사용자 정보 변경 없음.");
            }


            System.out.println("기존 사용자 로그인 처리 로직 완료: " + email);
            // TODO: 로그인 완료 후 적절한 페이지로 리다이렉트 (컨트롤러에서 결정)
        }
    }


}
