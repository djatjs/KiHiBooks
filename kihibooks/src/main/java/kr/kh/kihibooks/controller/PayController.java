package kr.kh.kihibooks.controller;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

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

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpSession;
import kr.kh.kihibooks.model.dto.ChargeDTO;
import kr.kh.kihibooks.model.dto.PaymentDTO;
import kr.kh.kihibooks.model.dto.PointOrderRequest;
import kr.kh.kihibooks.model.vo.BuyListVO;
import kr.kh.kihibooks.model.vo.EpisodeVO;
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
    public String history(Model model, @AuthenticationPrincipal CustomUser customUser) {
        List<BuyListVO> bList = userService.getBList(customUser.getUser().getUr_num());
        List<BuyListVO> buyList = userService.getBuyList(customUser.getUser().getUr_num());

        Map<String, Integer> bCountMap = new HashMap<>();
        for (BuyListVO buy : bList) {
            String bl_id = buy.getBl_id();
            bCountMap.put(bl_id, bCountMap.getOrDefault(bl_id, 0) + 1);
        }

        Map<BuyListVO, Integer> buyListMap = new LinkedHashMap<>();
        for (BuyListVO buy : buyList) {
            String blId = buy.getBl_id();
            Integer count = bCountMap.get(blId);
            if (count != null) {
                buyListMap.put(buy, count);
            }
        }

        model.addAttribute("buyListMap", buyListMap);
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
    public String checkoutFin(@RequestParam("merchant_uid") String contentsId,
            @AuthenticationPrincipal CustomUser customUser, Model model) {

        List<String> epCodes = userService.getEpCodesByBlId(contentsId);
        List<EpisodeVO> epList = userService.getEpisodeByCodes(epCodes);
        EpisodeVO episode = epList.get(0);
        String epiTitle = episode.getEp_title() + (epList.size() < 2 ? "" : " 외 " + ((epList.size() - 1) + "권"));
        int total = 0;
        for (EpisodeVO epi : epList) {
            total += epi.getEp_price();
        }
        DecimalFormat formatter = new DecimalFormat("#,###");
        String formattedTotal = formatter.format(total);

        model.addAttribute("contentsId", contentsId);
        model.addAttribute("episode", episode);
        model.addAttribute("formattedTotal", formattedTotal);
        model.addAttribute("epiTitle", epiTitle);
        return "/user/checkoutFin";
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
        System.out.println("payment: " + payment);

        // 1. 유효성 검사: 사용 포인트가 총 주문 금액을 초과하는지 확인
        if (payment.getUsePoint() > payment.getTotalAmount()) {
            response.put("success", false);
            response.put("error", "사용 포인트가 결제 금액을 초과합니다.");
            return response;
        }

        // 2. 사용자 정보 및 주문 정보 추출
        List<String> epCodes = payment.getEpCodes();
        int userNum = customUser.getUser().getUr_num();
        String orderId = userService.saveTempOrder(payment, userNum);

        // 3. 결제 금액 및 적립 포인트 처리
        int charge = 0; // 결제 API로 전송되는 금액 (기본 금액)
        int totalCredit = 0; // 사용자 계정에 적립되는 금액 (기본 금액 + 적립 포인트)

        if ("true".equals(payment.getIsChargeAfter())) {
            // 클라이언트에서 받은 chargeAmount는 기본 금액
            charge = payment.getChargeAmount();

            // 유효한 금액인지 검증
            if (!POINT_RATES.containsKey(charge)) {
                response.put("success", false);
                response.put("error", "유효하지 않은 충전 금액입니다.");
                return response;
            }

            // 적립 포인트 계산
            int bonusPoints = (int) Math.floor(charge * POINT_RATES.get(charge));
            totalCredit = charge + bonusPoints; // 실제 들어오는 금액
        }

        // 4. 결제 처리
        if ("true".equals(payment.getIsChargeAfter())) {
            // 충전 후 결제
            userService.chargeBeforePay(orderId, userNum, epCodes, totalCredit, payment.getFinalAmount());
        } else {
            // 일반 결제
            userService.justPay(orderId, userNum, epCodes);
        }

        // 5. 응답 구성
        String method = payment.getMethod();
        String redirectUrl = "/payment/" + method.toLowerCase() + "/start?orderId=" + orderId;

        response.put("success", true);
        response.put("orderId", orderId);
        response.put("amount", charge != 0 ? charge : payment.getFinalAmount()); // 결제 API에 사용할 금액
        response.put("method", method);
        response.put("redirectUrl", redirectUrl);
        response.put("email", customUser.getUser().getUr_email());
        response.put("nickname", customUser.getUser().getUr_nickname());
        response.put("totalCredit", totalCredit); // 디버깅용: 실제 적립 금액

        return response;
    }

    @GetMapping("/order/checkout/point/finished")
    public String chargePoint(@RequestParam("merchant_uid") String contentsId,
            @AuthenticationPrincipal CustomUser customUser, HttpSession session, Model model) {

        int amount = (int) session.getAttribute("amount");
        String method = (String) session.getAttribute("method");
        String methodString;
        if(method.equals("kakaopay")) {
            methodString = "카카오페이";
        } else if(method.equals("tosspay")) {
            methodString = "토스페이";
        } else {
            methodString = "null";
        }
        
        DecimalFormat formatter = new DecimalFormat("#,###");
        String formattedAmount = formatter.format(amount);

        model.addAttribute("formattedAmount", formattedAmount);
        model.addAttribute("methodString", methodString);
        return "user/chargePoint";
    }

    @PostMapping("/payment/pay")
    @ResponseBody
    public Map<String, Object> processPay(@RequestBody ChargeDTO chargeDTO,
            @AuthenticationPrincipal CustomUser customUser, HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        System.out.println("chargeDTO: " + chargeDTO);

        // 사용자 정보 검증
        if (customUser == null || customUser.getUser() == null) {
            response.put("success", false);
            response.put("error", "로그인이 필요합니다.");
            return response;
        }

        int userNum = customUser.getUser().getUr_num();
        String paymentMethod = chargeDTO.getMethod();
        int chargeAmount = chargeDTO.getChargeAmount();

        // 결제 수단 검증
        if (!Arrays.asList("kakaopay", "tosspay").contains(paymentMethod)) {
            response.put("success", false);
            response.put("error", "유효하지 않은 결제 수단입니다.");
            return response;
        }
        
        // 유효한 금액인지 검증
        if (!POINT_RATES.containsKey(chargeAmount)) {
            response.put("success", false);
            response.put("error", "유효하지 않은 충전 금액입니다.");
            return response;
        }

        // 적립 포인트 계산
        double rate = POINT_RATES.getOrDefault(chargeAmount, 0.0);
        int bonusPoints = (int) Math.floor(chargeAmount * rate);
        int totalCredit = chargeAmount + bonusPoints;

        userService.charge(userNum, totalCredit);

        String chargeUid = UUID.randomUUID().toString();

        String redirectUrl = "/payment/" + paymentMethod.toLowerCase() + "/start?chargeUid=" + chargeUid;

        response.put("success", true);
        response.put("orderId", chargeUid);
        response.put("amount", chargeAmount); // 결제 API에 사용할 금액
        response.put("method", paymentMethod);
        response.put("redirectUrl", redirectUrl);
        response.put("email", customUser.getUser().getUr_email());
        response.put("nickname", customUser.getUser().getUr_nickname());
        response.put("totalCredit", totalCredit); // 디버깅용: 실제 적립 금액

        session.setAttribute("amount", chargeAmount);
        session.setAttribute("method", paymentMethod);
        return response;
    }

    // 적립 비율 정의
    private static final Map<Integer, Double> POINT_RATES = new HashMap<>();
    static {
        POINT_RATES.put(2000, 0.03);
        POINT_RATES.put(5000, 0.03);
        POINT_RATES.put(10000, 0.03);
        POINT_RATES.put(20000, 0.03);
        POINT_RATES.put(30000, 0.04);
        POINT_RATES.put(50000, 0.04);
        POINT_RATES.put(100000, 0.05);
        POINT_RATES.put(200000, 0.05);
        POINT_RATES.put(300000, 0.05);
        POINT_RATES.put(500000, 0.05);
        POINT_RATES.put(1000000, 0.05);
        POINT_RATES.put(2000000, 0.05);
    }
}
