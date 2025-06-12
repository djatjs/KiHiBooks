package kr.kh.kihibooks.controller;

import java.text.NumberFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import kr.kh.kihibooks.model.dto.PaymentDTO;
import kr.kh.kihibooks.model.dto.PaymentRequest;
import kr.kh.kihibooks.model.dto.PointOrderRequest;
import kr.kh.kihibooks.model.dto.TokenRequest;
import kr.kh.kihibooks.model.vo.EpisodeVO;
import kr.kh.kihibooks.model.vo.OrderVO;
import kr.kh.kihibooks.service.UserService;
import kr.kh.kihibooks.utils.CustomUser;

@Controller
public class PayController {
    
    @Autowired
    UserService userService;

    @Autowired
    RestTemplate restTemplate;
    
    @Autowired
    ObjectMapper objectMapper;

    @GetMapping("/order/checkout/point")
    public String point() {
        return "user/point";
    }

    @GetMapping("/order/history_point")
    public String historyPoint() {
        return "user/historyPoint";
    }

    @GetMapping("/order/history")
    public String history() {
        return "/user/history";
    }

    @GetMapping("/cart")
    public String cart(Model model, @AuthenticationPrincipal CustomUser customUser) {
        List<EpisodeVO> epList = userService.getCartEpList(customUser.getUser().getUr_num());
        model.addAttribute("epList", epList);
        return "/user/cart";
    }

    @PostMapping("/cart/delete")
    @ResponseBody
    public boolean deleteCart(@RequestBody Map<String, String> payload,
            @AuthenticationPrincipal CustomUser customUser) {
        String epCode = payload.get("epCode");
        int urNum = customUser.getUser().getUr_num();

        if (epCode == null || epCode.isEmpty()) {
            return false;
        }

        return userService.deleteCart(urNum, epCode);
    }

    @PostMapping("/cart/deleteSelected")
    @ResponseBody
    public boolean deleteSelected(@RequestBody Map<String, List<String>> payload,
            @AuthenticationPrincipal CustomUser customUser) {
        List<String> epCodes = payload.get("epCodes");
        int urNum = customUser.getUser().getUr_num();

        if (epCodes == null || epCodes.isEmpty()) {
            return false;
        }

        return userService.deleteSelected(urNum, epCodes);
    }

    @GetMapping("/order/checkout/finished")
    public String checkoutFin(@RequestParam("merchant_uid") String contentsId, Model model) {
        model.addAttribute("contentsId", contentsId);
        return "/user/checkoutFin";
    }

    @PostMapping("/order/chargeBeforePay")
    public ResponseEntity<?> completePayment(@RequestBody PaymentRequest request) {
        try {
            // 1. 포트원 API 엑세스 토큰 발급
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            String tokenRequestBody = objectMapper.writeValueAsString(
                    new TokenRequest("imp_apikey", "ekKoeW8RyKuT0zgaZsUtXXTLQ4AhPFW"));

            HttpEntity<String> tokenEntity = new HttpEntity<>(tokenRequestBody, headers);
            ResponseEntity<JsonNode> tokenResponse = restTemplate.exchange(
                    "https://api.iamport.kr/users/getToken",
                    HttpMethod.POST,
                    tokenEntity,
                    JsonNode.class);

            if (!tokenResponse.getStatusCode().is2xxSuccessful()) {
                throw new RuntimeException("Failed to get token: " + tokenResponse.getBody());
            }

            String accessToken = tokenResponse.getBody().get("response")
                    .get("access_token").asText();

            // 2. 포트원 결제내역 단건조회 API 호출
            HttpHeaders paymentHeaders = new HttpHeaders();
            paymentHeaders.set("Authorization", accessToken);

            HttpEntity<?> paymentEntity = new HttpEntity<>(paymentHeaders);
            ResponseEntity<JsonNode> paymentResponse = restTemplate.exchange(
                    "https://api.iamport.kr/payments/" + request.getImpUid(),
                    HttpMethod.GET,
                    paymentEntity,
                    JsonNode.class);

            if (!paymentResponse.getStatusCode().is2xxSuccessful()) {
                throw new RuntimeException("Failed to get payment: " + paymentResponse.getBody());
            }

            JsonNode payment = paymentResponse.getBody();

            // 3. 고객사 내부 주문 데이터의 가격과 실제 지불된 금액을 비교
            OrderVO order = userService.findById(request.getMerchantUid());
            int paymentAmount = payment.get("amount").asInt();

            if (order.getOd_final_amount() == paymentAmount) {
                String status = payment.get("status").asText();
                switch (status) {
                    case "ready":
                        // 가상 계좌가 발급된 상태
                        // 계좌 정보를 이용한 로직 구현
                        break;
                    case "paid":
                        return ResponseEntity.ok().body("Payment completed successfully");
                }
                return ResponseEntity.ok().build();
            } else {
                // 결제 금액 불일치
                return ResponseEntity.badRequest().body("Payment amount mismatch");
            }
        } catch (Exception e) {
            // 결제 검증 실패
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/order/free")
    @ResponseBody
    public Map<String, Object> processFreeOrder(@RequestBody Map<String, List<String>> payload,
            @AuthenticationPrincipal CustomUser customUser) {

        List<String> epCodes = payload.get("epCodes");

        String contentsId = userService.insertFreeOrder(epCodes, customUser.getUser().getUr_num());

        Map<String, Object> res = new HashMap<>();
        res.put("contentsId", contentsId);

        return res;
    }

    @PostMapping("/order/point")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> processPointOrder(
            @RequestBody PointOrderRequest payload,
            @AuthenticationPrincipal CustomUser customUser) {
        List<String> epCodes = payload.getEpCodes();
        String orderId = payload.getOrderId();
        int usePoint = payload.getUsePoint();
        String contentsId = userService.updatePointOrder(epCodes, customUser.getUser().getUr_num(), orderId, usePoint);

        Map<String, Object> res = new HashMap<>();
        res.put("contentsId", contentsId);
        res.put("success", true);

        return ResponseEntity.ok(res);
    }

    @PostMapping("/order/checkout")
    @ResponseBody
    public boolean checkout(@RequestParam List<String> epCodes, @AuthenticationPrincipal CustomUser customUser) {
        if (epCodes == null || epCodes.isEmpty()) {
            return false;
        }

        int urNum = customUser.getUser().getUr_num();

        userService.deleteOrderList(urNum);

        boolean saved = userService.saveOrderList(epCodes, urNum);

        return saved;
    }

    @GetMapping("/order/checkout")
    public String checkout(Model model, @AuthenticationPrincipal CustomUser customUser) {
        int urNum = customUser.getUser().getUr_num();
        List<String> epCodes = userService.selectOrderList(urNum);
        List<EpisodeVO> epList = userService.getEpisodeByCodes(epCodes);

        int total = 0;
        if (epList != null) {
            for (EpisodeVO episode : epList) {
                total += episode.getEp_price();
            }
        }

        int point = customUser.getUser().getUr_point();

        NumberFormat formatter = NumberFormat.getInstance();
        String formattedPoint = formatter.format(point);
        String formattedTotal = formatter.format(total);

        model.addAttribute("epList", epList);
        model.addAttribute("total", total);
        model.addAttribute("point", point);
        model.addAttribute("formattedPoint", formattedPoint);
        model.addAttribute("formattedTotal", formattedTotal);

        return "user/checkout";
    }

    @PostMapping("/payment/process")
    @ResponseBody
    public Map<String, Object> processPayment(@RequestBody PaymentDTO payment,
            @AuthenticationPrincipal CustomUser customUser) {
        Map<String, Object> response = new HashMap<>();
        System.out.println(payment);
        if (payment.getUsePoint() > payment.getTotalAmount()) {
            response.put("success", false);
            response.put("error", "사용 포인트가 결제 금액을 초과합니다.");
            return response;
        }
        
        List<String> epCodes = payment.getEpCodes();
        int userNum = customUser.getUser().getUr_num();
        String orderId = userService.saveTempOrder(payment, userNum);

        String method = payment.getMethod();
        String redirectUrl = "/payment/" + method.toLowerCase() + "/start?orderId=" + orderId;
        int charge = 0;
        
        if(payment.getIsChargeAfter().equals("true")) {
            charge = payment.getChargeAmount();
        }

        response.put("success", true);
        response.put("orderId", orderId);
        response.put("amount", payment.getTotalAmount());
        response.put("method", method);
        response.put("redirectUrl", redirectUrl);
        response.put("email", customUser.getUser().getUr_email());
        response.put("nickname", customUser.getUser().getUr_nickname());
        
        userService.chargeBeforePay(orderId, userNum, epCodes, charge, payment.getFinalAmount());
        System.out.println(response);
        return response;
    }
}
