package kr.kh.kihibooks.model.dto;

import java.util.List;

import kr.kh.kihibooks.model.vo.EpisodeVO;
import lombok.Data;

@Data
public class PaymentDTO {
    private Integer totalAmount;
    private Integer usePoint;
    private Integer finalAmount;
    private String method; //naver / kakao / toss
    private boolean isChargeAfter;
    private String chargeAmount; //isChargeAfter : true 경우만 필요요 -> 충전 금액
    private int userNum;
    private List<EpisodeVO> epList; //구매 회차 리스트
}
