package kr.kh.kihibooks.model.dto;

import lombok.Data;

@Data
public class PaymentRequest {
    private String userId;
    private String selectedMethod;
    private long amount;
    private String contentsId;
}
