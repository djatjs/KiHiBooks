package kr.kh.kihibooks.model.dto;

import lombok.Data;

@Data
public class PaymentRequest {
	private String impUid;
	private String merchantUid;
}
