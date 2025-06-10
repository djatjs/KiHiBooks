package kr.kh.kihibooks.model.dto;

import lombok.Data;

@Data
public class KakaoPayApproveRequest {
    private String pgToken;
    private String partnerOdId;
}
