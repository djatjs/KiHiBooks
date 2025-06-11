package kr.kh.kihibooks.model.dto;

import java.util.List;

import lombok.Data;

@Data
public class PaymentDTO {
	private Integer totalAmount;
    private Integer usePoint;
    private Integer finalAmount;
    private String method; //naver / kakao / toss
    private String isChargeAfter;
    private int chargeAmount; //isChargeAfter : true 경우만 필요 -> 충전 금액
    private int userNum;

    private List<String> epCodes;
}
