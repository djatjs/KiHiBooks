package kr.kh.kihibooks.service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import kr.kh.kihibooks.dao.UserDAO;
import kr.kh.kihibooks.model.dto.PaymentRequest;
import kr.kh.kihibooks.model.dto.PaymentResponse;

@Service
public class PaymentService {

    @Autowired
    UserDAO userDAO;

    public PaymentResponse processPayment(PaymentRequest request) {
        PaymentResponse response = new PaymentResponse();

        try {
            // 결제 요청 유효성 검증
            if (request.getSelectedMethod() == null || request.getAmount() <= 0) {
                response.setSuccess(false);
                response.setMessage("유효하지 않은 결제 정보입니다.");
                return response;
            }

            // 주문 ID 생성 및 결제 초기화
            String orderId = generateOdId();
            boolean isValid = validatePaymentRequest(request);

            if (!isValid) {
                response.setSuccess(false);
                response.setMessage("결제 요청이 유효하지 않습니다.");
                return response;
            }

            // 포인트 결제의 경우 즉시 완료 처리
            if ("POINT".equals(request.getSelectedMethod())) {
                boolean pointDeducted = deductPoints(request.getUserId(), request.getAmount());
                if (pointDeducted) {
                    response.setSuccess(true);
                    response.setOrderId(orderId);
                } else {
                    response.setSuccess(false);
                    response.setMessage("포인트가 부족합니다.");
                }
            } else {
                // 외부 결제 수단 처리 (Naver, Kakao, Toss)
                response.setSuccess(true);
                response.setOrderId(orderId);
            }

        } catch (Exception e) {
            response.setSuccess(false);
            response.setMessage("결제 처리 중 오류가 발생했습니다: " + e.getMessage());
        }

        return response;
    }

    public String getPaymentRedirectUrl(String method, String orderId) {
        // 결제 수단별 리다이렉션 URL 생성
        switch (method.toLowerCase()) {
            case "naver":
                return "/payment/naver/process?orderId=" + orderId;
            case "kakao":
                return "/payment/kakao/process?orderId=" + orderId;
            case "toss":
                return "/payment/toss/process?orderId=" + orderId;
            default:
                throw new IllegalArgumentException("지원하지 않는 결제 수단입니다: " + method);
        }
    }

    private String generateOdId() {
        String date = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        long count = userDAO.countTodayOrders() + 1;

        return date + String.format("%05d", count);
    }

    private boolean validatePaymentRequest(PaymentRequest request) {
        // 결제 요청 유효성 검증 로직
        return request.getUserId() != null && request.getAmount() > 0;
    }

    private boolean deductPoints(String userId, long amount) {
        // 포인트 차감 로직 (DB 연동 필요)
        // 예: 사용자 포인트 조회 및 차감
        return true; // 임시로 true 반환
    }
}
