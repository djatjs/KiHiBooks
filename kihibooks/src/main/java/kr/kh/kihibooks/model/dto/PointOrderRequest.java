package kr.kh.kihibooks.model.dto;

import java.util.List;

import lombok.Data;

@Data
public class PointOrderRequest {
	private List<String> epCodes;
	private String orderId;
	private int usePoint;
}
