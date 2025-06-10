package kr.kh.kihibooks.model.dto;

import lombok.Data;

@Data
public class PaymentResponse {
    private boolean success;
    private String orderId;
    private String message;
}
