// package kr.kh.kihibooks.service;

// import lombok.extern.java.Log;

// import org.springframework.transaction.annotation.Transactional;

// import lombok.RequiredArgsConstructor;

// import org.springframework.stereotype.Service;

// @Service
// @RequiredArgsConstructor
// @Transactional
// @Log
// public class KakaoPayService {


// 	private KakaoPayDTO kakaoPayDTO;

//     public String kakaoPayReady() {
//         RestTemplate restTemplate = new RestTemplate();
//         restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory()); // 정확한 에러 파악을 위해 생성

//         // Server Request Header : 서버 요청 헤더
//         HttpHeaders headers = new HttpHeaders();
//         headers.add("Authorization", "KakaoAK " + kakaoAdminKey); // 어드민 키
//         headers.add("Accept", "application/json");
//         headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

//         // Server Request Body : 서버 요청 본문
//         MultiValueMap<String, String> params = new LinkedMultiValueMap<String, String>();
				
// 				String contents_id = 
// 				String successUrl = "https://localhost:8080/order/checkout/finished?contents_id=" + contentsId;

//         params.add("cid", "TC0ONETIME"); // 가맹점 코드 - 테스트용
//         params.add("partner_order_id", "1001"); // 주문 번호
//         params.add("partner_user_id", "goguma"); // 회원 아이디
//         params.add("item_name", "비둘기"); // 상품 명
//         params.add("quantity", "1"); // 상품 수량
//         params.add("total_amount", "20000"); // 상품 가격
//         params.add("tax_free_amount", "100"); // 상품 비과세 금액
//         params.add("approval_url", "http://localhost:8080/order/checkout/finished?"); // 성공시 url
//         params.add("cancel_url", "http://localhost:8080/kakaoPayCancel"); // 실패시 url
//         params.add("fail_url", "http://localhost:8080/kakaoPayFail");

//         // 헤더와 바디 붙이기
//         HttpEntity<MultiValueMap<String, String>> body = new HttpEntity<MultiValueMap<String, String>>(params, headers);

//         try {
//             kakaoPayDTO = restTemplate.postForObject(new URI(Host + "/v1/payment/ready"), body, KakaoPayDTO.class);

//             log.info(""+ kakaoPayDTO);
//             return kakaoPayDTO.getNext_redirect_pc_url();

//         } catch (RestClientException e) {
//             e.printStackTrace();
//         } catch (URISyntaxException e) {
//             e.printStackTrace();
//         }
//         return "/pay";
//     }

// }
